package com.sensecode.navigo.di

import com.sensecode.navigo.data.repository.GraphRAGRepository
import com.sensecode.navigo.data.repository.NavigationRepository
import com.sensecode.navigo.data.repository.SetupRepository
import com.sensecode.navigo.data.repository.VenueRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    // All repositories use constructor injection via @Inject,
    // so Hilt can provide them automatically.
    // This module exists for any future bindings or interface abstractions.

    // If needed, bind interfaces to implementations here:
    // @Binds abstract fun bindNavigationRepository(impl: NavigationRepositoryImpl): NavigationRepository
}
