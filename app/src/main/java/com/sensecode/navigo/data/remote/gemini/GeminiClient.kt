package com.sensecode.navigo.data.remote.gemini

import com.sensecode.navigo.BuildConfig
import com.sensecode.navigo.data.remote.gemini.model.GeminiContent
import com.sensecode.navigo.data.remote.gemini.model.GeminiPart
import com.sensecode.navigo.data.remote.gemini.model.GeminiRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiClient @Inject constructor() {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun generateCypher(
        systemPrompt: String,
        userQuery: String,
        fewShotExamples: List<Pair<String, String>> = emptyList()
    ): Result<String> {
        return try {
            val contents = mutableListOf<GeminiContent>()

            // Add few-shot examples
            for ((example, response) in fewShotExamples) {
                contents.add(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = example))
                    )
                )
                contents.add(
                    GeminiContent(
                        role = "model",
                        parts = listOf(GeminiPart(text = response))
                    )
                )
            }

            // Add the actual user query
            contents.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = userQuery))
                )
            )

            val request = GeminiRequest(
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = systemPrompt))
                ),
                contents = contents
            )

            val response = apiService.generateContent(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )

            if (response.isSuccessful) {
                val text = response.body()?.extractText()
                if (text != null) {
                    // Clean up the response — remove any markdown backticks or extra text
                    val cleanedCypher = text
                        .replace("```json", "")
                        .replace("```cypher", "")
                        .replace("```", "")
                        .trim()
                    Result.success(cleanedCypher)
                } else {
                    Result.failure(Exception("Empty response from Gemini"))
                }
            } else {
                Result.failure(Exception("Gemini API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateEmbedding(text: String): Result<List<Float>> {
        return try {
            val request = GeminiEmbeddingRequest(
                content = GeminiContent(
                    parts = listOf(com.sensecode.navigo.data.remote.gemini.model.GeminiPart(text = text))
                )
            )
            
            val response = apiService.generateEmbedding(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )

            if (response.isSuccessful) {
                val values = response.body()?.embedding?.values
                if (values != null) {
                    Result.success(values)
                } else {
                    Result.failure(Exception("Empty embedding from Gemini"))
                }
            } else {
                Result.failure(Exception("Gemini Embedding API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
