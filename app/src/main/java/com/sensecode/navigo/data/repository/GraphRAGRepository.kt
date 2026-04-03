package com.sensecode.navigo.data.repository

import com.sensecode.navigo.data.remote.gemini.GeminiClient
import com.sensecode.navigo.data.remote.neo4j.Neo4jClient
import com.sensecode.navigo.domain.model.LocationNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraphRAGRepository @Inject constructor(
    private val geminiClient: GeminiClient,
    private val neo4jClient: Neo4jClient,
    private val navigationRepository: NavigationRepository
) {
    // In-memory cache for session to avoid re-querying Neo4j
    private val queryCache = mutableMapOf<String, LocationNode>()

    private val systemPrompt = """
        You are an enterprise-grade NLU (Natural Language Understanding) engine for an indoor navigation system.
        Users will speak complex, rambling sentences in English, Hindi, or Hinglish. 

        YOUR TASK:
        1. Understand the user's intent fluently in English, Hindi, and Hinglish.
        2. Extract the core navigational intent (the place or thing they are looking for).
        3. ALWAYS TRANSLATE the extracted entity into plain, universal English. Do not output Hindi/Hinglish words in the extracted_entity field.
        4. Output ONLY a valid JSON object.
        
        Format: { "extracted_entity": "The English translation of the core noun", "category": "room|facility|exit|medical|unknown" }

        Examples:
        - "bhaiya mujhe washroom jana hai kahan jau" -> {"extracted_entity": "toilet", "category": "facility"}
        - "I really need to see a doctor quickly my arm hurts" -> {"extracted_entity": "medical clinic", "category": "medical"}
        - "mujhe dant ke doctor ke paas jana hai" -> {"extracted_entity": "dentist", "category": "medical"}
        - "mujhe billi ke paas le chalo" -> {"extracted_entity": "cat", "category": "unknown"}
        - "kutta kidhar hai" -> {"extracted_entity": "dog", "category": "unknown"}
        - "bhookh lagi hai khana khana hai" -> {"extracted_entity": "food restaurant", "category": "facility"}
    """.trimIndent()

    suspend fun resolveNaturalLanguageQuery(
        userQuery: String,
        venueId: String,
        currentFloor: Int
    ): Result<LocationNode> = withContext(Dispatchers.IO) {
        // Check cache
        val cacheKey = "$venueId:$currentFloor:${userQuery.lowercase().trim()}"
        queryCache[cacheKey]?.let { return@withContext Result.success(it) }

        try {
            // Step 1: Extract intent via Gemini (Translation-First Approach for Hindi -> English)
            val jsonResult = geminiClient.generateCypher(
                systemPrompt = systemPrompt,
                userQuery = userQuery,
                fewShotExamples = emptyList() // No few-shot examples needed
            )
            
            val jsonString = jsonResult.getOrNull()
            
            if (jsonString != null) {
                // Safe JSON parsing to handle any extra conversational text from Gemini
                val startIndex = jsonString.indexOf("{")
                val endIndex = jsonString.lastIndexOf("}")
                
                val extractedEntity = if (startIndex != -1 && endIndex != -1 && startIndex <= endIndex) {
                    val cleanJson = jsonString.substring(startIndex, endIndex + 1)
                    val jsonObject = org.json.JSONObject(cleanJson)
                    jsonObject.optString("extracted_entity", userQuery)
                } else {
                    userQuery
                }
                
                // Step 2: Generate Embedding
                val embeddingResult = geminiClient.generateEmbedding(extractedEntity)
                val embedding = embeddingResult.getOrNull()
                
                if (embedding != null) {
                    // Extract float list to string
                    val vectorStr = embedding.joinToString(prefix = "[", postfix = "]")

                    // Execute Vector Search in Neo4j with High Confidence Threshold
                    val cypher = """
                        CALL db.index.vector.queryNodes('location_embeddings', 1, $vectorStr) 
                        YIELD node, score 
                        WHERE score > 0.89 
                        RETURN node.id AS id, node.name AS name, node.floor AS floor, score 
                        LIMIT 1
                    """.trimIndent()
                    
                    val neo4jResult = neo4jClient.executeCypher(cypher)
                    val response = neo4jResult.getOrNull()
                    
                    if (response != null) {
                        val rows = response.getAllRows()
                        if (rows.isNotEmpty()) {
                            val firstRow = rows.first()
                            val nodeId = extractNodeId(firstRow)
                            val nodeName = extractNodeName(firstRow)
                            
                            if (nodeId != null) {
                                // Step 4: Look up in local Room DB by direct ID
                                val directNode = navigationRepository.getNodeById(nodeId)
                                if (directNode != null && directNode.venueId == venueId) {
                                    queryCache[cacheKey] = directNode
                                    return@withContext Result.success(directNode)
                                }
                                
                                // Fallback by best matching nodeName
                                val searchName = nodeName ?: extractedEntity
                                val localNode = navigationRepository.findBestMatchingNode(
                                    venueId, currentFloor, searchName
                                )
                                if (localNode != null) {
                                    queryCache[cacheKey] = localNode
                                    return@withContext Result.success(localNode)
                                }
                            }
                        } else {
                            // If Neo4j returns empty (score < 0.89), it means low confidence
                            return@withContext Result.failure(LowConfidenceException("Low matching confidence. Please ask the user to repeat the destination clearly."))
                        }
                    }
                }
            }

            // Step 5: Fallback to local Dictionary text search (Offline Mode)
            val localMatch = navigationRepository.findBestMatchingNode(
                venueId, currentFloor, userQuery
            )
            if (localMatch != null) {
                queryCache[cacheKey] = localMatch
                Result.success(localMatch)
            } else {
                Result.failure(LowConfidenceException("Could not find a high-confidence matching location for: $userQuery. Please ask the user to repeat."))
            }
        } catch (e: Exception) {
            // Final fallback to local search
            val localMatch = navigationRepository.findBestMatchingNode(
                venueId, currentFloor, userQuery
            )
            if (localMatch != null) {
                queryCache[cacheKey] = localMatch
                Result.success(localMatch)
            } else {
                Result.failure(LowConfidenceException("Failed to resolve query accurately. Please ask the user to repeat."))
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractNodeId(row: List<Any>): String? {
        for (item in row) {
            when (item) {
                is String -> return item
                is Map<*, *> -> {
                    val map = item as? Map<String, Any>
                    val id = map?.get("id") as? String
                    val name = map?.get("name") as? String
                    return id ?: name
                }
            }
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractNodeName(row: List<Any>): String? {
        for (item in row) {
            when (item) {
                is String -> return item
                is Map<*, *> -> {
                    val map = item as? Map<String, Any>
                    val name = map?.get("name") as? String
                    return name
                }
            }
        }
        return null
    }
}

class LowConfidenceException(message: String) : Exception(message)
