package com.sensecode.navigo.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.sensecode.navigo.domain.model.Edge
import com.sensecode.navigo.domain.model.LocationNode
import com.sensecode.navigo.domain.model.Venue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FirestoreVenueService — Raw Firestore calls for venue CRUD
 *
 * Firestore Schema:
 * venues/                            (collection)
 *   {venueId}/                       (document: Venue metadata)
 *     name: String
 *     address: String
 *     orgName: String
 *     floors: Int
 *     nodeCount: Int
 *     publishedAt: Timestamp
 *     publisherId: String (Firebase Auth UID)
 *
 *     nodes/                         (subcollection)
 *       {nodeId}/                    (document)
 *         ... all LocationNode fields
 *
 *     edges/                         (subcollection)
 *       {fromNodeId}_{toNodeId}/     (document)
 *         ... all Edge fields
 *
 * Security Rules:
 * - Anyone can read venues collection (public map browsing)
 * - Only authenticated users can write to venues
 * - A user can only modify documents where publisherId == request.auth.uid
 */
@Singleton
class FirestoreVenueService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    suspend fun uploadVenue(
        venue: Venue,
        nodes: List<LocationNode>,
        edges: List<Edge>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val venueRef = firestore.collection("venues").document(venue.venueId)

            // Upload venue metadata
            val venueData = hashMapOf(
                "name" to venue.name,
                "address" to venue.address,
                "orgName" to venue.orgName,
                "floors" to venue.floors,
                "nodeCount" to nodes.size,
                "publishedAt" to com.google.firebase.Timestamp.now(),
                "publisherId" to venue.publisherId
            )
            venueRef.set(venueData).await()

            // Upload nodes as subcollection
            val batch = firestore.batch()
            for (node in nodes) {
                val nodeRef = venueRef.collection("nodes").document(node.id)
                val nodeData = hashMapOf(
                    "id" to node.id,
                    "name" to node.name,
                    "floor" to node.floor,
                    "venueId" to node.venueId,
                    "accessible" to node.accessible,
                    "type" to node.type,
                    "relativeX" to node.relativeX,
                    "relativeY" to node.relativeY
                )
                batch.set(nodeRef, nodeData)
            }
            batch.commit().await()

            // Upload edges (may need multiple batches for large graphs)
            val edgeBatches = edges.chunked(400) // Firestore batch limit is 500
            for (edgeChunk in edgeBatches) {
                val edgeBatch = firestore.batch()
                for (edge in edgeChunk) {
                    val edgeRef = venueRef.collection("edges")
                        .document("${edge.fromNodeId}_${edge.toNodeId}")
                    val edgeData = hashMapOf(
                        "fromNodeId" to edge.fromNodeId,
                        "toNodeId" to edge.toNodeId,
                        "venueId" to edge.venueId,
                        "distanceM" to edge.distanceM,
                        "directionDegrees" to edge.directionDegrees,
                        "directionLabel" to edge.directionLabel,
                        "instruction" to edge.instruction,
                        "hasStairs" to edge.hasStairs,
                        "estimatedSeconds" to edge.estimatedSeconds
                    )
                    edgeBatch.set(edgeRef, edgeData)
                }
                edgeBatch.commit().await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadVenue(venueId: String): Result<Triple<Venue, List<LocationNode>, List<Edge>>> =
        withContext(Dispatchers.IO) {
            try {
                val venueRef = firestore.collection("venues").document(venueId)
                val venueDoc = venueRef.get().await()

                if (!venueDoc.exists()) {
                    return@withContext Result.failure(Exception("Venue not found"))
                }

                val venue = Venue(
                    venueId = venueId,
                    name = venueDoc.getString("name") ?: "",
                    address = venueDoc.getString("address") ?: "",
                    orgName = venueDoc.getString("orgName") ?: "",
                    floors = venueDoc.getLong("floors")?.toInt() ?: 1,
                    nodeCount = venueDoc.getLong("nodeCount")?.toInt() ?: 0,
                    publisherId = venueDoc.getString("publisherId") ?: ""
                )

                // Download nodes
                val nodesSnapshot = venueRef.collection("nodes").get().await()
                val nodes = nodesSnapshot.documents.map { doc ->
                    LocationNode(
                        id = doc.getString("id") ?: doc.id,
                        name = doc.getString("name") ?: "",
                        floor = doc.getLong("floor")?.toInt() ?: 0,
                        venueId = doc.getString("venueId") ?: venueId,
                        accessible = doc.getBoolean("accessible") ?: true,
                        type = doc.getString("type") ?: "room",
                        relativeX = doc.getDouble("relativeX")?.toFloat() ?: 0f,
                        relativeY = doc.getDouble("relativeY")?.toFloat() ?: 0f
                    )
                }

                // Download edges
                val edgesSnapshot = venueRef.collection("edges").get().await()
                val edges = edgesSnapshot.documents.map { doc ->
                    Edge(
                        fromNodeId = doc.getString("fromNodeId") ?: "",
                        toNodeId = doc.getString("toNodeId") ?: "",
                        venueId = doc.getString("venueId") ?: venueId,
                        distanceM = doc.getDouble("distanceM")?.toFloat() ?: 0f,
                        directionDegrees = doc.getDouble("directionDegrees")?.toFloat() ?: 0f,
                        directionLabel = doc.getString("directionLabel") ?: "",
                        instruction = doc.getString("instruction") ?: "",
                        hasStairs = doc.getBoolean("hasStairs") ?: false,
                        estimatedSeconds = doc.getLong("estimatedSeconds")?.toInt() ?: 0
                    )
                }

                Result.success(Triple(venue, nodes, edges))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun searchVenues(query: String): Result<List<Venue>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("venues")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .await()

            val venues = snapshot.documents.map { doc ->
                Venue(
                    venueId = doc.id,
                    name = doc.getString("name") ?: "",
                    address = doc.getString("address") ?: "",
                    orgName = doc.getString("orgName") ?: "",
                    floors = doc.getLong("floors")?.toInt() ?: 1,
                    nodeCount = doc.getLong("nodeCount")?.toInt() ?: 0,
                    publisherId = doc.getString("publisherId") ?: ""
                )
            }
            Result.success(venues)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPublishedVenues(): Result<List<Venue>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("venues")
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val venues = snapshot.documents.map { doc ->
                Venue(
                    venueId = doc.id,
                    name = doc.getString("name") ?: "",
                    address = doc.getString("address") ?: "",
                    orgName = doc.getString("orgName") ?: "",
                    floors = doc.getLong("floors")?.toInt() ?: 1,
                    nodeCount = doc.getLong("nodeCount")?.toInt() ?: 0,
                    publisherId = doc.getString("publisherId") ?: ""
                )
            }
            Result.success(venues)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Attach a real-time Firestore snapshot listener on the "venues" collection.
     * Fires [onUpdate] every time a venue is added, modified, or deleted.
     * Returns a [ListenerRegistration] — caller MUST call .remove() when done (e.g. in onCleared).
     */
    fun addVenueListListener(
        onUpdate: (List<Venue>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore.collection("venues")
            .orderBy("publishedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val venues = snapshot.documents.mapNotNull { doc ->
                    try {
                        Venue(
                            venueId = doc.id,
                            name = doc.getString("name") ?: "",
                            address = doc.getString("address") ?: "",
                            orgName = doc.getString("orgName") ?: "",
                            floors = doc.getLong("floors")?.toInt() ?: 1,
                            nodeCount = doc.getLong("nodeCount")?.toInt() ?: 0,
                            publisherId = doc.getString("publisherId") ?: ""
                        )
                    } catch (e: Exception) {
                        null // Skip malformed documents
                    }
                }
                onUpdate(venues)
            }
    }
}
