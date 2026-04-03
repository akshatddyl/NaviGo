package com.sensecode.navigo.di

import android.content.Context
import androidx.room.Room
import com.sensecode.navigo.data.local.NaviGoDatabase
import com.sensecode.navigo.data.local.dao.EdgeDao
import com.sensecode.navigo.data.local.dao.LocationNodeDao
import com.sensecode.navigo.data.local.dao.RouteLogDao
import com.sensecode.navigo.data.local.dao.VenueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): NaviGoDatabase {
        return Room.databaseBuilder(
            ctx,
            NaviGoDatabase::class.java,
            "navigo_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideNodeDao(db: NaviGoDatabase): LocationNodeDao = db.locationNodeDao()

    @Provides
    fun provideEdgeDao(db: NaviGoDatabase): EdgeDao = db.edgeDao()

    @Provides
    fun provideRouteLogDao(db: NaviGoDatabase): RouteLogDao = db.routeLogDao()

    @Provides
    fun provideVenueDao(db: NaviGoDatabase): VenueDao = db.venueDao()
}
