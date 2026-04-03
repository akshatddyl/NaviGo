package com.sensecode.navigo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sensecode.navigo.data.local.entity.EdgeEntity

@Dao
interface EdgeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEdge(edge: EdgeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEdges(edges: List<EdgeEntity>)

    @Query("SELECT * FROM edges WHERE venueId = :venueId")
    suspend fun getEdgesForVenue(venueId: String): List<EdgeEntity>

    @Query("SELECT * FROM edges WHERE fromNodeId = :fromNodeId")
    suspend fun getEdgesFromNode(fromNodeId: String): List<EdgeEntity>

    @Query("DELETE FROM edges WHERE venueId = :venueId")
    suspend fun deleteEdgesByVenue(venueId: String)
}
