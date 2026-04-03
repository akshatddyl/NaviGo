package com.sensecode.navigo.data.remote.neo4j

import com.sensecode.navigo.data.remote.neo4j.model.CypherRequest
import com.sensecode.navigo.data.remote.neo4j.model.CypherResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface Neo4jApiService {

    @POST("db/neo4j/tx/commit")
    suspend fun executeCypher(
        @Header("Authorization") auth: String,
        @Body request: CypherRequest
    ): Response<CypherResponse>
}
