package com.sensecode.navigo.data.remote.neo4j

import android.util.Base64
import com.sensecode.navigo.BuildConfig
import com.sensecode.navigo.data.remote.neo4j.model.CypherRequest
import com.sensecode.navigo.data.remote.neo4j.model.CypherResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Neo4j AuraDB REST Client
 *
 * Setup instructions:
 * 1. Create a free Neo4j AuraDB instance at https://console.neo4j.io
 * 2. Note the connection URI, username, and password
 * 3. Add these to local.properties:
 *    NEO4J_URI=your-auradb-uri.databases.neo4j.io
 *    NEO4J_USERNAME=neo4j
 *    NEO4J_PASSWORD=your-auradb-password
 * 4. Run the initialization Cypher queries in the AuraDB browser (see neo4j_init.cypher)
 */
@Singleton
class Neo4jClient @Inject constructor() {

    private val authHeader: String by lazy {
        val credentials = "${BuildConfig.NEO4J_USERNAME}:${BuildConfig.NEO4J_PASSWORD}"
        val encoded = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        "Basic $encoded"
    }

    private val baseUrl: String by lazy {
        val uri = BuildConfig.NEO4J_URI
        if (uri.isBlank()) "https://placeholder.databases.neo4j.io/"
        else if (uri.startsWith("http")) "$uri/"
        else "https://$uri/"
    }

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

    val apiService: Neo4jApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Neo4jApiService::class.java)
    }

    suspend fun executeCypher(cypher: String): Result<CypherResponse> {
        return try {
            val request = CypherRequest.of(cypher)
            val response = apiService.executeCypher(authHeader, request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && !body.hasErrors()) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.getErrorMessage() ?: "Empty response from Neo4j"))
                }
            } else {
                Result.failure(Exception("Neo4j API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
