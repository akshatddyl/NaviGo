package com.sensecode.navigo.domain.model

data class Venue(
    val venueId: String,
    val name: String,
    val address: String,
    val orgName: String,
    val floors: Int,
    val nodeCount: Int = 0,
    val publisherId: String = ""
)
