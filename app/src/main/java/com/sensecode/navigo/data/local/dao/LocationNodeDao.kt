package com.sensecode.navigo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sensecode.navigo.data.local.entity.LocationNodeEntity

@Dao
interface LocationNodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: LocationNodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNodes(nodes: List<LocationNodeEntity>)

    @Query("SELECT * FROM location_nodes WHERE venueId = :venueId")
    suspend fun getNodesByVenue(venueId: String): List<LocationNodeEntity>

    @Query("SELECT * FROM location_nodes WHERE venueId = :venueId AND floor = :floor")
    suspend fun getNodesByFloor(venueId: String, floor: Int): List<LocationNodeEntity>

    @Query("SELECT * FROM location_nodes WHERE id = :id")
    suspend fun getNodeById(id: String): LocationNodeEntity?

    @Query("SELECT * FROM location_nodes WHERE venueId = :venueId AND name LIKE '%' || :query || '%'")
    suspend fun searchNodesByName(venueId: String, query: String): List<LocationNodeEntity>

    @Query("DELETE FROM location_nodes WHERE venueId = :venueId")
    suspend fun deleteNodesByVenue(venueId: String)

    @Query("SELECT DISTINCT venueId FROM location_nodes")
    suspend fun getDistinctVenueIds(): List<String>
}
