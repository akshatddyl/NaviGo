package com.sensecode.navigo.data.remote.gemini

import com.sensecode.navigo.data.remote.gemini.model.GeminiRequest
import com.sensecode.navigo.data.remote.gemini.model.GeminiResponse
import com.sensecode.navigo.data.remote.gemini.model.GeminiContent
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>

    @POST("v1beta/models/text-embedding-004:embedContent")
    suspend fun generateEmbedding(
        @Query("key") apiKey: String,
        @Body request: GeminiEmbeddingRequest
    ): Response<GeminiEmbeddingResponse>
}

// === NEW EMBEDDING DATA CLASSES ===

data class GeminiEmbeddingRequest(
    val content: com.sensecode.navigo.data.remote.gemini.model.GeminiContent
)

data class GeminiEmbeddingResponse(
    val embedding: GeminiEmbeddingDetail
)

data class GeminiEmbeddingDetail(
    val values: List<Float>
)
