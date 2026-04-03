package com.sensecode.navigo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_logs")
data class RouteLogEntity(
    @PrimaryKey val sessionId: String,
    val venueId: String,
    val startNodeId: String,
    val destinationNodeId: String,
    val startTime: Long,
    val endTime: Long,
    val deviationCount: Int,
    val completedSuccessfully: Boolean,
    val routeNodeIds: String
)
