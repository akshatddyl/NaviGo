package com.sensecode.navigo.data.repository

import com.sensecode.navigo.data.local.dao.EdgeDao
import com.sensecode.navigo.data.local.dao.LocationNodeDao
import com.sensecode.navigo.data.local.dao.VenueDao
import com.sensecode.navigo.data.local.entity.EdgeEntity
import com.sensecode.navigo.data.local.entity.LocationNodeEntity
import com.sensecode.navigo.data.local.entity.VenueEntity
import com.sensecode.navigo.domain.algorithm.EdgeCalculator
import com.sensecode.navigo.setup.SetupRecordingSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetupRepository @Inject constructor(
    private val nodeDao: LocationNodeDao,
    private val edgeDao: EdgeDao,
    private val venueDao: VenueDao
) {

    suspend fun saveRecordingSession(
        session: SetupRecordingSession,
        venueName: String,
        venueAddress: String,
        orgName: String,
        floor: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val venueId = "venue_${UUID.randomUUID().toString().take(8)}"
            val recordedNodes = session.stopSession()
            val calculatedEdges = session.getCalculatedEdges()

            if (recordedNodes.isEmpty()) {
                return@withContext Result.failure(Exception("No nodes recorded"))
            }

            // Save Venue Details
            val venueEntity = VenueEntity(
                venueId = venueId,
                name = venueName,
                address = venueAddress,
                orgName = orgName,
                floors = 1, // Assuming 1 for now or we can calculate if multiple floors were recorded
                nodeCount = recordedNodes.size
            )
            venueDao.insertVenue(venueEntity)

            // Normalize positions to [0.1, 0.9] range
            val xs = recordedNodes.map { it.cumulativeX }
            val ys = recordedNodes.map { it.cumulativeY }
            val minX = xs.min()
            val maxX = xs.max()
            val minY = ys.min()
            val maxY = ys.max()
            val rangeX = if (maxX - minX > 0.001f) maxX - minX else 1f
            val rangeY = if (maxY - minY > 0.001f) maxY - minY else 1f

            // Convert to LocationNodeEntities
            val nodeEntities = recordedNodes.map { node ->
                val normalizedX = 0.1f + 0.8f * ((node.cumulativeX - minX) / rangeX)
                val normalizedY = 0.1f + 0.8f * ((node.cumulativeY - minY) / rangeY)
                LocationNodeEntity(
                    id = node.id,
                    name = node.name,
                    floor = floor,
                    venueId = venueId,
                    accessible = node.accessible,
                    type = node.type,
                    relativeX = normalizedX,
                    relativeY = normalizedY
                )
            }

            // Convert to EdgeEntities (bidirectional)
            val edgeEntities = mutableListOf<EdgeEntity>()
            for (edge in calculatedEdges) {
                // Forward edge
                edgeEntities.add(
                    EdgeEntity(
                        fromNodeId = edge.fromNodeId,
                        toNodeId = edge.toNodeId,
                        venueId = venueId,
                        distanceM = edge.distanceM,
                        directionDegrees = edge.directionDegrees,
                        directionLabel = edge.directionLabel,
                        instruction = edge.instruction,
                        hasStairs = edge.hasStairs,
                        estimatedSeconds = edge.estimatedSeconds
                    )
                )

                // Reverse edge (mirrored)
                edgeEntities.add(
                    EdgeEntity(
                        fromNodeId = edge.toNodeId,
                        toNodeId = edge.fromNodeId,
                        venueId = venueId,
                        distanceM = edge.distanceM,
                        directionDegrees = EdgeCalculator.mirrorHeading(edge.directionDegrees),
                        directionLabel = EdgeCalculator.mirrorDirection(edge.directionLabel),
                        instruction = EdgeCalculator.mirrorInstruction(edge.instruction),
                        hasStairs = edge.hasStairs,
                        estimatedSeconds = edge.estimatedSeconds
                    )
                )
            }

            nodeDao.insertAllNodes(nodeEntities)
            edgeDao.insertAllEdges(edgeEntities)

            Result.success(venueId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
