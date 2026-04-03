package com.sensecode.navigo.domain.model

data class Edge(
    val fromNodeId: String,
    val toNodeId: String,
    val venueId: String,
    val distanceM: Float,
    val directionDegrees: Float,
    val directionLabel: String,
    val instruction: String,
    val hasStairs: Boolean,
    val estimatedSeconds: Int
)
