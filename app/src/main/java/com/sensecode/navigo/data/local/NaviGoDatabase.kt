package com.sensecode.navigo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sensecode.navigo.data.local.dao.EdgeDao
import com.sensecode.navigo.data.local.dao.LocationNodeDao
import com.sensecode.navigo.data.local.dao.RouteLogDao
import com.sensecode.navigo.data.local.entity.EdgeEntity
import com.sensecode.navigo.data.local.entity.LocationNodeEntity
import com.sensecode.navigo.data.local.entity.RouteLogEntity

@Database(
    entities = [
        LocationNodeEntity::class,
        EdgeEntity::class,
        RouteLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NaviGoDatabase : RoomDatabase() {
    abstract fun locationNodeDao(): LocationNodeDao
    abstract fun edgeDao(): EdgeDao
    abstract fun routeLogDao(): RouteLogDao
}
