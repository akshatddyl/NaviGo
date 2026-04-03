package com.sensecode.navigo.data.remote.gemini.model

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    @SerializedName("system_instruction")
    val systemInstruction: GeminiContent? = null,
    @SerializedName("contents")
    val contents: List<GeminiContent>
)

data class GeminiContent(
    @SerializedName("role")
    val role: String? = null,
    @SerializedName("parts")
    val parts: List<GeminiPart>
)

data class GeminiPart(
    @SerializedName("text")
    val text: String
)
