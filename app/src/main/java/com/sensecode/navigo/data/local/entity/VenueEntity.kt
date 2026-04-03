package com.sensecode.navigo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "venues")
data class VenueEntity(
    @PrimaryKey val venueId: String,
    val name: String,
    val address: String,
    val orgName: String,
    val floors: Int,
    val nodeCount: Int,
    val publisherId: String = ""
)
