package com.sensecode.navigo.data.local.entity

import androidx.room.Entity

@Entity(tableName = "edges", primaryKeys = ["fromNodeId", "toNodeId"])
data class EdgeEntity(
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
