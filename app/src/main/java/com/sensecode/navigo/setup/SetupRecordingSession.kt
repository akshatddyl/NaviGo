package com.sensecode.navigo.setup

import com.sensecode.navigo.domain.algorithm.EdgeCalculator
import com.sensecode.navigo.sensors.CompassManager
import com.sensecode.navigo.sensors.StepCounterManager
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

data class RecordedNode(
    val id: String,
    val name: String,
    val type: String,
    val accessible: Boolean,
    val hasStairs: Boolean,
    val hasElevator: Boolean,
    val headingAtDrop: Float,
    val stepsFromPrevious: Int,
    val cumulativeX: Float,
    val cumulativeY: Float
)

data class CalculatedEdge(
    val fromNodeId: String,
    val toNodeId: String,
    val distanceM: Float,
    val directionDegrees: Float,
    val directionLabel: String,
    val instruction: String,
    val hasStairs: Boolean,
    val estimatedSeconds: Int
)

class SetupRecordingSession(
    private val stepManager: StepCounterManager,
    private val compassManager: CompassManager
) {
    private val recordedNodes = mutableListOf<RecordedNode>()
    private var sessionActive = false
    private var stepsAtLastDrop = 0

    fun startSession() {
        recordedNodes.clear()
        sessionActive = true
        stepManager.resetCount()
        stepManager.startCounting()
        compassManager.startTracking()
        stepsAtLastDrop = 0
    }

    fun dropNode(
        name: String,
        type: String,
        accessible: Boolean,
        hasStairs: Boolean,
        hasElevator: Boolean
    ): RecordedNode {
        val currentSteps = stepManager.stepsSinceLastReset
        val stepsFromPrevious = currentSteps - stepsAtLastDrop
        val heading = compassManager.currentHeading
        val distanceM = EdgeCalculator.stepsToDist(stepsFromPrevious)

        // Calculate cumulative position via dead reckoning
        val prevX = recordedNodes.lastOrNull()?.cumulativeX ?: 0f
        val prevY = recordedNodes.lastOrNull()?.cumulativeY ?: 0f

        val dx = sin(Math.toRadians(heading.toDouble())) * distanceM
        val dy = -cos(Math.toRadians(heading.toDouble())) * distanceM

        val cumulativeX = if (recordedNodes.isEmpty()) 0f else prevX + dx.toFloat()
        val cumulativeY = if (recordedNodes.isEmpty()) 0f else prevY + dy.toFloat()

        val node = RecordedNode(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            accessible = accessible,
            hasStairs = hasStairs,
            hasElevator = hasElevator,
            headingAtDrop = heading,
            stepsFromPrevious = stepsFromPrevious,
            cumulativeX = cumulativeX,
            cumulativeY = cumulativeY
        )

        recordedNodes.add(node)
        stepsAtLastDrop = currentSteps

        return node
    }

    fun stopSession(): List<RecordedNode> {
        sessionActive = false
        stepManager.stopCounting()
        compassManager.stopTracking()
        return recordedNodes.toList()
    }

    fun getCalculatedEdges(): List<CalculatedEdge> {
        val edges = mutableListOf<CalculatedEdge>()

        for (i in 0 until recordedNodes.size - 1) {
            val from = recordedNodes[i]
            val to = recordedNodes[i + 1]
            val distanceM = EdgeCalculator.stepsToDist(to.stepsFromPrevious)
            val directionLabel = EdgeCalculator.headingToDirectionLabel(to.headingAtDrop)

            val instruction = if (i == 0) {
                "Walk straight"
            } else {
                EdgeCalculator.headingDeltaToTurnInstruction(
                    recordedNodes[i - 1].headingAtDrop,
                    to.headingAtDrop
                )
            }

            edges.add(
                CalculatedEdge(
                    fromNodeId = from.id,
                    toNodeId = to.id,
                    distanceM = distanceM,
                    directionDegrees = to.headingAtDrop,
                    directionLabel = directionLabel,
                    instruction = instruction,
                    hasStairs = to.hasStairs,
                    estimatedSeconds = EdgeCalculator.distanceToEstimatedSeconds(distanceM)
                )
            )
        }

        return edges
    }

    fun getNodeCount(): Int = recordedNodes.size

    fun getLastNodeName(): String? = recordedNodes.lastOrNull()?.name

    fun getRecordedNodes(): List<RecordedNode> = recordedNodes.toList()

    fun isActive(): Boolean = sessionActive
}
