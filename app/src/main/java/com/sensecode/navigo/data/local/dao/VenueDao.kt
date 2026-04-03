package com.sensecode.navigo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sensecode.navigo.data.local.entity.VenueEntity

@Dao
interface VenueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenue(venue: VenueEntity)

    @Query("SELECT * FROM venues WHERE venueId = :venueId")
    suspend fun getVenueById(venueId: String): VenueEntity?

    @Query("SELECT * FROM venues")
    suspend fun getAllVenues(): List<VenueEntity>

    @Query("DELETE FROM venues WHERE venueId = :venueId")
    suspend fun deleteVenue(venueId: String)
}
