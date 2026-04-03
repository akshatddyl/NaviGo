package com.sensecode.navigo.di

import com.sensecode.navigo.data.remote.gemini.GeminiApiService
import com.sensecode.navigo.data.remote.gemini.GeminiClient
import com.sensecode.navigo.data.remote.neo4j.Neo4jApiService
import com.sensecode.navigo.data.remote.neo4j.Neo4jClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNeo4jClient(): Neo4jClient = Neo4jClient()

    @Provides
    @Singleton
    fun provideNeo4jApiService(client: Neo4jClient): Neo4jApiService = client.apiService

    @Provides
    @Singleton
    fun provideGeminiClient(): GeminiClient = GeminiClient()

    @Provides
    @Singleton
    fun provideGeminiApiService(client: GeminiClient): GeminiApiService = client.apiService
}
