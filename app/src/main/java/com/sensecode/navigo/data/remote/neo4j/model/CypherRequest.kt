package com.sensecode.navigo.data.remote.neo4j.model

import com.google.gson.annotations.SerializedName

data class CypherRequest(
    @SerializedName("statements")
    val statements: List<CypherStatement>
) {
    companion object {
        fun of(cypher: String): CypherRequest {
            return CypherRequest(
                statements = listOf(CypherStatement(statement = cypher))
            )
        }
    }
}

data class CypherStatement(
    @SerializedName("statement")
    val statement: String
)
