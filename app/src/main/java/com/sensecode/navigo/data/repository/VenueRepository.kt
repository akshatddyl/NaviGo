package com.sensecode.navigo.data.repository

import com.sensecode.navigo.data.local.dao.EdgeDao
import com.sensecode.navigo.data.local.dao.LocationNodeDao
import com.sensecode.navigo.data.remote.firebase.FirestoreVenueService
import com.sensecode.navigo.domain.model.Edge
import com.sensecode.navigo.domain.model.LocationNode
import com.sensecode.navigo.domain.model.Venue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VenueRepository @Inject constructor(
    private val nodeDao: LocationNodeDao,
    private val edgeDao: EdgeDao,
    private val firestoreVenueService: FirestoreVenueService
) {

    suspend fun saveVenueLocally(
        venue: Venue,
        nodes: List<LocationNode>,
        edges: List<Edge>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Clear existing data for this venue
            nodeDao.deleteNodesByVenue(venue.venueId)
            edgeDao.deleteEdgesByVenue(venue.venueId)

            // Insert all nodes
            nodeDao.insertAllNodes(nodes.map { it.toEntity() })

            // Insert all edges
            edgeDao.insertAllEdges(edges.map { it.toEntity() })

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadVenueToMapShare(venueId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val nodes = nodeDao.getNodesByVenue(venueId)
            val edges = edgeDao.getEdgesForVenue(venueId)

            if (nodes.isEmpty()) {
                return@withContext Result.failure(Exception("No data found for venue $venueId"))
            }

            val firstNode = nodes.first()
            val venue = Venue(
                venueId = venueId,
                name = venueId, // Will be overwritten by caller with actual metadata
                address = "",
                orgName = "",
                floors = nodes.map { it.floor }.distinct().size,
                nodeCount = nodes.size
            )

            firestoreVenueService.uploadVenue(
                venue = venue,
                nodes = nodes.map { it.toDomain() },
                edges = edges.map { it.toDomain() }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadVenueToMapShare(
        venue: Venue,
        nodes: List<LocationNode>,
        edges: List<Edge>
    ): Result<Unit> {
        return firestoreVenueService.uploadVenue(venue, nodes, edges)
    }

    suspend fun downloadVenueFromMapShare(venueId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = firestoreVenueService.downloadVenue(venueId)
            result.fold(
                onSuccess = { (venue, nodes, edges) ->
                    saveVenueLocally(venue, nodes, edges)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLocalVenues(): List<Venue> = withContext(Dispatchers.IO) {
        try {
            val venueIds = nodeDao.getDistinctVenueIds()
            venueIds.map { venueId ->
                val nodes = nodeDao.getNodesByVenue(venueId)
                Venue(
                    venueId = venueId,
                    name = venueId.replace("_", " ").replaceFirstChar { it.uppercase() },
                    address = "",
                    orgName = "",
                    floors = nodes.map { it.floor }.distinct().size,
                    nodeCount = nodes.size
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteLocalVenue(venueId: String) = withContext(Dispatchers.IO) {
        nodeDao.deleteNodesByVenue(venueId)
        edgeDao.deleteEdgesByVenue(venueId)
    }

    suspend fun searchPublicVenues(query: String): Result<List<Venue>> {
        return firestoreVenueService.searchVenues(query)
    }

    suspend fun getPublishedVenues(): Result<List<Venue>> {
        return firestoreVenueService.getPublishedVenues()
    }
}
