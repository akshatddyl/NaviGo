package com.sensecode.navigo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_nodes")
data class LocationNodeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val floor: Int,
    val venueId: String,
    val accessible: Boolean,
    val type: String,
    val relativeX: Float,
    val relativeY: Float
)
