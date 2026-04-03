package com.sensecode.navigo.domain.model

data class Route(
    val nodes: List<LocationNode>,
    val edges: List<Edge>,
    val totalDistanceM: Float,
    val estimatedMinutes: Int,
    val isAccessible: Boolean
)
