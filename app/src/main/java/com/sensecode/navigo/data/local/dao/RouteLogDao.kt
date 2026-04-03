package com.sensecode.navigo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sensecode.navigo.data.local.entity.RouteLogEntity

@Dao
interface RouteLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RouteLogEntity)

    @Query("SELECT * FROM route_logs WHERE venueId = :venueId ORDER BY startTime DESC")
    suspend fun getLogsForVenue(venueId: String): List<RouteLogEntity>
}
