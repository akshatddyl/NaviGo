package com.sensecode.navigo.data.remote.neo4j.model

import com.google.gson.annotations.SerializedName

data class CypherResponse(
    @SerializedName("results")
    val results: List<CypherResult>?,
    @SerializedName("errors")
    val errors: List<CypherError>?
) {
    fun getFirstRowValues(): List<Any>? {
        return results?.firstOrNull()?.data?.firstOrNull()?.row
    }

    fun getFirstRowAsMap(): Map<String, Any>? {
        val row = getFirstRowValues() ?: return null
        val columns = results?.firstOrNull()?.columns ?: return null
        if (row.size != columns.size) return null
        return columns.zip(row).toMap()
    }

    fun getAllRows(): List<List<Any>> {
        return results?.firstOrNull()?.data?.map { it.row } ?: emptyList()
    }

    fun hasErrors(): Boolean {
        return !errors.isNullOrEmpty()
    }

    fun getErrorMessage(): String {
        return errors?.firstOrNull()?.message ?: "Unknown Neo4j error"
    }
}

data class CypherResult(
    @SerializedName("columns")
    val columns: List<String>,
    @SerializedName("data")
    val data: List<CypherRow>
)

data class CypherRow(
    @SerializedName("row")
    val row: List<Any>,
    @SerializedName("meta")
    val meta: List<Any?>?
)

data class CypherError(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String
)
