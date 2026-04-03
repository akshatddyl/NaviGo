package com.sensecode.navigo.data.repository

import android.util.Log
import com.sensecode.navigo.data.local.dao.EdgeDao
import com.sensecode.navigo.data.local.dao.LocationNodeDao
import com.sensecode.navigo.data.local.entity.EdgeEntity
import com.sensecode.navigo.data.local.entity.LocationNodeEntity
import com.sensecode.navigo.domain.algorithm.Dijkstra
import com.sensecode.navigo.domain.model.Edge
import com.sensecode.navigo.domain.model.LocationNode
import com.sensecode.navigo.domain.model.Route
import com.sensecode.navigo.util.HindiNlpHelper
import com.sensecode.navigo.util.HospitalNodeDictionary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRepository @Inject constructor(
    private val nodeDao: LocationNodeDao,
    private val edgeDao: EdgeDao
) {

    suspend fun getNodeById(id: String): LocationNode? = withContext(Dispatchers.IO) {
        nodeDao.getNodeById(id)?.toDomain()
    }

    suspend fun getRoute(
        startNodeId: String,
        destinationNodeId: String,
        venueId: String,
        accessibleOnly: Boolean
    ): Result<Route> = withContext(Dispatchers.IO) {
        try {
            val nodes = getVenueNodes(venueId)
            val edges = getVenueEdges(venueId)

            if (nodes.isEmpty()) {
                return@withContext Result.failure(Exception("No nodes found for venue $venueId"))
            }

            val route = Dijkstra.findShortestPath(
                startNodeId = startNodeId,
                destinationNodeId = destinationNodeId,
                nodes = nodes,
                edges = edges,
                accessibleOnly = accessibleOnly
            )

            if (route != null) {
                Result.success(route)
            } else {
                Result.failure(
                    Exception(
                        if (accessibleOnly) "No accessible route found. Try without accessibility filter."
                        else "No route found between the selected locations."
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVenueNodes(venueId: String): List<LocationNode> = withContext(Dispatchers.IO) {
        nodeDao.getNodesByVenue(venueId).map { it.toDomain() }
    }

    suspend fun getVenueEdges(venueId: String): List<Edge> = withContext(Dispatchers.IO) {
        edgeDao.getEdgesForVenue(venueId).map { it.toDomain() }
    }

    /**
     * Find the best matching node for a spoken query — supports English and Hindi.
     *
     * Matching pipeline:
     * 1. Direct exact/contains match (handles English and simple queries)
     * 2. Hindi NLP processing — extract intent, keywords, numbers from conversational Hindi
     * 3. Match by extracted english keywords against node names
     * 4. Match by extracted node types against node type field
     * 5. Combine gender/accessibility modifiers with type (e.g., "male" + "restroom")
     * 6. Fallback: Room LIKE search + Levenshtein fuzzy match
     * 7. If floor-specific search fails, search ALL floors
     */
    suspend fun findBestMatchingNode(
        venueId: String,
        floor: Int,
        nameQuery: String
    ): LocationNode? = withContext(Dispatchers.IO) {
        val query = nameQuery.trim().lowercase()
        Log.d(TAG, "findBestMatchingNode: query='$query', floor=$floor, venue=$venueId")

        // 1. Try on specified floor
        val result = findNodeOnFloor(venueId, floor, query)
        if (result != null) {
            Log.d(TAG, "✓ Found on floor $floor: ${result.name}")
            return@withContext result
        }

        // 2. Search ALL floors (handles wrong floor parsing)
        val allNodes = nodeDao.getNodesByVenue(venueId)
        val fallback = findNodeInList(allNodes, query)
            ?: findNodeViaDictionary(allNodes, query)
            ?: findNodeViaHindiNlp(allNodes, query)
        
        if (fallback != null) {
            Log.d(TAG, "✓ Found on all-floors search: ${fallback.name}")
            return@withContext fallback.toDomain()
        }

        Log.w(TAG, "✗ No match found for: $query")
        null
    }

    private suspend fun findNodeOnFloor(
        venueId: String,
        floor: Int,
        query: String
    ): LocationNode? {
        val allNodes = nodeDao.getNodesByFloor(venueId, floor)

        // Step 1: Direct string match
        val direct = findNodeInList(allNodes, query)
        if (direct != null) return direct.toDomain()

        // Step 1.5: Explicit Dictionary match mapping (e.g. NODE_PHARMACY)
        val dictMatch = findNodeViaDictionary(allNodes, query)
        if (dictMatch != null) return dictMatch.toDomain()

        // Step 2: Hindi NLP-based matching
        val nlpMatch = findNodeViaHindiNlp(allNodes, query)
        if (nlpMatch != null) return nlpMatch.toDomain()

        // Step 3: Database LIKE search
        val dbMatch = findNodeViaDatabaseSearch(venueId, floor, query)
        if (dbMatch != null) return dbMatch

        // Step 4: Fuzzy Levenshtein match
        val fuzzyMatch = findNodeViaFuzzyMatch(venueId, query)
        if (fuzzyMatch != null) return fuzzyMatch

        return null
    }

    /**
     * Direct string matching: exact, contains, both directions.
     */
    private fun findNodeInList(
        nodes: List<LocationNodeEntity>,
        query: String
    ): LocationNodeEntity? {
        // Exact match
        val exact = nodes.find { it.name.equals(query, ignoreCase = true) }
        if (exact != null) return exact

        // Contains match (both directions)
        val containsMatch = nodes.filter {
            it.name.lowercase().contains(query) || query.contains(it.name.lowercase())
        }
        if (containsMatch.isNotEmpty()) return containsMatch.first()

        return null
    }

    /**
     * Resolve explicit Dictionary Mapping via HospitalNodeDictionary.
     */
    private fun findNodeViaDictionary(
        nodes: List<LocationNodeEntity>,
        query: String
    ): LocationNodeEntity? {
        val resolvedNodeType = HospitalNodeDictionary.matchQueryToNodeType(query)
        if (resolvedNodeType != null) {
            // Find nodes matching this standard type (e.g. "NODE_PHARMACY" mapped closely to "Pharmacy")
            val standardizedTypeName = resolvedNodeType.removePrefix("NODE_").replace("_", " ").lowercase()
            
            // E.g. Check if "pharmacy" is in the actual graph node type or name
            val match = nodes.find { 
                it.type.lowercase().contains(standardizedTypeName) || 
                it.name.lowercase().contains(standardizedTypeName) 
            }
            if (match != null) {
                Log.d(TAG, "Dictionary match: '$query' -> $resolvedNodeType -> ${match.name}")
                return match
            }
        }
        return null
    }

    /**
     * Use HindiNlpHelper to understand conversational Hindi and match to nodes.
     *
     * This handles phrases like:
     * - "मुझे खाने की जगह बताओ" → strips filler → "खाना" → intent: FOOD → matches "Canteen"
     * - "बाहर कैसे जाऊं" → strips filler → "बाहर" → intent: EXIT → matches "Rear Exit"
     * - "कक्षा 101" → keyword "कक्षा" → "room" + number 101 → matches "Room 101"
     * - "पुरुष शौचालय" → "male" + "restroom" → matches "Male Restroom"
     */
    private fun findNodeViaHindiNlp(
        nodes: List<LocationNodeEntity>,
        query: String
    ): LocationNodeEntity? {
        val nlpResult = HindiNlpHelper.process(query)
        Log.d(TAG, "Hindi NLP: keywords=${nlpResult.englishKeywords}, types=${nlpResult.matchedNodeTypes}, nums=${nlpResult.extractedNumbers}")

        if (nlpResult.englishKeywords.isEmpty() && nlpResult.matchedNodeTypes.isEmpty()) {
            return null
        }

        val numberSuffix = nlpResult.extractedNumbers.firstOrNull()?.toString() ?: ""

        // Strategy 1: Try keyword + number combinations against node names
        for (keyword in nlpResult.englishKeywords) {
            // Try with number: "room 101", "classroom 2"
            if (numberSuffix.isNotEmpty()) {
                val withNumber = "$keyword $numberSuffix"
                val nameMatch = nodes.find {
                    it.name.lowercase().contains(withNumber.lowercase())
                }
                if (nameMatch != null) {
                    Log.d(TAG, "NLP match (keyword+number): '$withNumber' → ${nameMatch.name}")
                    return nameMatch
                }
            }

            // Try keyword alone: "canteen", "library"
            val keywordMatch = nodes.filter {
                it.name.lowercase().contains(keyword.lowercase())
            }
            if (keywordMatch.isNotEmpty()) {
                Log.d(TAG, "NLP match (keyword): '$keyword' → ${keywordMatch.first().name}")
                return keywordMatch.first()
            }
        }

        // Strategy 2: Combine multi-intent keywords for compound queries
        // e.g., "male" + "restroom" → "Male Restroom"
        if (nlpResult.englishKeywords.size >= 2) {
            val keywords = nlpResult.englishKeywords
            for (i in keywords.indices) {
                for (j in keywords.indices) {
                    if (i == j) continue
                    val combo = "${keywords[i]} ${keywords[j]}"
                    val comboMatch = nodes.find {
                        it.name.lowercase().contains(combo.lowercase())
                    }
                    if (comboMatch != null) {
                        Log.d(TAG, "NLP match (combo): '$combo' → ${comboMatch.name}")
                        return comboMatch
                    }
                }
            }
        }

        // Strategy 3: Match by node type
        for (nodeType in nlpResult.matchedNodeTypes) {
            val typeMatches = nodes.filter { it.type.equals(nodeType, ignoreCase = true) }
            if (typeMatches.isNotEmpty()) {
                // If we also have a gender modifier, filter further
                val genderKeyword = nlpResult.englishKeywords.find {
                    it in listOf("male", "female", "accessible", "boys", "girls")
                }
                if (genderKeyword != null) {
                    val genderedMatch = typeMatches.find {
                        it.name.lowercase().contains(genderKeyword)
                    }
                    if (genderedMatch != null) {
                        Log.d(TAG, "NLP match (type+gender): type=$nodeType, gender=$genderKeyword → ${genderedMatch.name}")
                        return genderedMatch
                    }
                }

                // Return first type match
                Log.d(TAG, "NLP match (type): $nodeType → ${typeMatches.first().name}")
                return typeMatches.first()
            }
        }

        // Strategy 4: Try number-only match (e.g., user said "101" → "Room 101")
        if (numberSuffix.isNotEmpty()) {
            val numberMatch = nodes.find {
                it.name.lowercase().contains(numberSuffix)
            }
            if (numberMatch != null) {
                Log.d(TAG, "NLP match (number only): '$numberSuffix' → ${numberMatch.name}")
                return numberMatch
            }
        }

        return null
    }

    private suspend fun findNodeViaDatabaseSearch(
        venueId: String,
        floor: Int,
        query: String
    ): LocationNode? {
        val searchResults = nodeDao.searchNodesByName(venueId, query)
        if (searchResults.isNotEmpty()) {
            val floorMatch = searchResults.find { it.floor == floor }
            return (floorMatch ?: searchResults.first()).toDomain()
        }
        return null
    }

    private suspend fun findNodeViaFuzzyMatch(
        venueId: String,
        query: String
    ): LocationNode? {
        val allVenueNodes = nodeDao.getNodesByVenue(venueId)
        val closest = allVenueNodes.minByOrNull { levenshteinDistance(it.name.lowercase(), query) }
        if (closest != null && levenshteinDistance(closest.name.lowercase(), query) <= query.length / 2) {
            return closest.toDomain()
        }
        return null
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] = if (s1[i - 1] == s2[j - 1]) {
                    dp[i - 1][j - 1]
                } else {
                    minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + 1)
                }
            }
        }
        return dp[m][n]
    }

    companion object {
        private const val TAG = "NavigationRepository"
    }
}

fun LocationNodeEntity.toDomain(): LocationNode = LocationNode(
    id = id,
    name = name,
    floor = floor,
    venueId = venueId,
    accessible = accessible,
    type = type,
    relativeX = relativeX,
    relativeY = relativeY
)

fun LocationNode.toEntity(): LocationNodeEntity = LocationNodeEntity(
    id = id,
    name = name,
    floor = floor,
    venueId = venueId,
    accessible = accessible,
    type = type,
    relativeX = relativeX,
    relativeY = relativeY
)

fun EdgeEntity.toDomain(): Edge = Edge(
    fromNodeId = fromNodeId,
    toNodeId = toNodeId,
    venueId = venueId,
    distanceM = distanceM,
    directionDegrees = directionDegrees,
    directionLabel = directionLabel,
    instruction = instruction,
    hasStairs = hasStairs,
    estimatedSeconds = estimatedSeconds
)

fun Edge.toEntity(): EdgeEntity = EdgeEntity(
    fromNodeId = fromNodeId,
    toNodeId = toNodeId,
    venueId = venueId,
    distanceM = distanceM,
    directionDegrees = directionDegrees,
    directionLabel = directionLabel,
    instruction = instruction,
    hasStairs = hasStairs,
    estimatedSeconds = estimatedSeconds
)
