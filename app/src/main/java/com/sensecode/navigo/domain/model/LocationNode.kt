package com.sensecode.navigo.domain.model

data class LocationNode(
    val id: String,
    val name: String,
    val floor: Int,
    val venueId: String,
    val accessible: Boolean,
    val type: String,
    val relativeX: Float = 0f,
    val relativeY: Float = 0f
)
