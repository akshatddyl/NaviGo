package com.sensecode.navigo.data.remote.gemini.model

import com.google.gson.annotations.SerializedName

data class GeminiResponse(
    @SerializedName("candidates")
    val candidates: List<GeminiCandidate>?
) {
    fun extractText(): String? {
        return candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
    }
}

data class GeminiCandidate(
    @SerializedName("content")
    val content: GeminiResponseContent?
)

data class GeminiResponseContent(
    @SerializedName("parts")
    val parts: List<GeminiResponsePart>?
)

data class GeminiResponsePart(
    @SerializedName("text")
    val text: String?
)
