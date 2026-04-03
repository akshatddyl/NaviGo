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

    suspend fun saveVenueLocally(venue: Venue, nodes: List<LocationNode>, edges: List<Edge>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            nodeDao.deleteNodesByVenue(venue.venueId)
            edgeDao.deleteEdgesByVenue(venue.venueId)
            nodeDao.insertAllNodes(nodes.map { it.toEntity() })
            edgeDao.insertAllEdges(edges.map { it.toEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadVenueToMapShare(venue: Venue, nodes: List<LocationNode>, edges: List<Edge>): Result<Unit> = withContext(Dispatchers.IO) {
        firestoreVenueService.uploadVenue(venue, nodes, edges)
    }

    suspend fun uploadVenueToMapShare(venueId: String, publisherId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val nodes = nodeDao.getNodesByVenue(venueId)
            val edges = edgeDao.getEdgesForVenue(venueId)
            if (nodes.isEmpty()) return@withContext Result.failure(Exception("No local data found for this venue."))

            // Create a unique venue ID for MapShare to avoid Permission Denied when overwriting others' data
            // If it's the demo venue, we make it unique to the publisher
            val remoteVenueId = if (venueId == "demo_venue") "demo_${publisherId.take(5)}" else venueId

            val venue = Venue(
                venueId = remoteVenueId,
                name = venueId.replace("_", " ").replaceFirstChar { it.uppercase() },
                address = "Shared via NaviGo",
                orgName = "Community Contributor",
                floors = nodes.map { it.floor }.distinct().size,
                nodeCount = nodes.size,
                publisherId = publisherId
            )

            firestoreVenueService.uploadVenue(
                venue = venue,
                nodes = nodes.map { it.toDomain().copy(venueId = remoteVenueId) },
                edges = edges.map { it.toDomain().copy(venueId = remoteVenueId) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadVenueFromMapShare(venueId: String): Result<Unit> = withContext(Dispatchers.IO) {
        firestoreVenueService.downloadVenue(venueId).fold(
            onSuccess = { (venue, nodes, edges) -> saveVenueLocally(venue, nodes, edges) },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun getLocalVenues(): List<Venue> = withContext(Dispatchers.IO) {
        try {
            nodeDao.getDistinctVenueIds().map { venueId ->
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

    suspend fun searchPublicVenues(query: String) = firestoreVenueService.searchVenues(query)
    suspend fun getPublishedVenues() = firestoreVenueService.getPublishedVenues()
}
