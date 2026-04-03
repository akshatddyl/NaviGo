package com.sensecode.navigo.engine

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.sensecode.navigo.R
import com.sensecode.navigo.audio.TtsManager
import com.sensecode.navigo.data.local.dao.RouteLogDao
import com.sensecode.navigo.data.local.entity.RouteLogEntity
import com.sensecode.navigo.data.repository.NavigationRepository
import com.sensecode.navigo.domain.algorithm.EdgeCalculator
import com.sensecode.navigo.domain.model.LocationNode
import com.sensecode.navigo.domain.model.NavigationState
import com.sensecode.navigo.domain.model.NodeType
import com.sensecode.navigo.domain.model.Route
import com.sensecode.navigo.haptics.HapticManager
import com.sensecode.navigo.sensors.CompassManager
import com.sensecode.navigo.sensors.StepCounterManager
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Singleton
class NavigationEngine @Inject constructor(
    private val stepCounterManager: StepCounterManager,
    private val compassManager: CompassManager,
    private val ttsManager: TtsManager,
    private val hapticManager: HapticManager,
    private val navigationRepository: NavigationRepository,
    private val routeLogDao: RouteLogDao,
    @ApplicationContext private val appContext: Context
) {
    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Idle)
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    private val _currentNodeId = MutableStateFlow<String?>(null)
    val currentNodeId: StateFlow<String?> = _currentNodeId.asStateFlow()

    private val _routeProgress = MutableStateFlow(0f)
    val routeProgress: StateFlow<Float> = _routeProgress.asStateFlow()

    private val _deviationDetected = MutableStateFlow(false)
    val deviationDetected: StateFlow<Boolean> = _deviationDetected.asStateFlow()

    /** Current TalkBack-friendly instruction for the active node/edge */
    private val _currentInstruction = MutableStateFlow("")
    val currentInstruction: StateFlow<String> = _currentInstruction.asStateFlow()

    /** List of all step-by-step directions for the route */
    private val _directionSteps = MutableStateFlow<List<DirectionStep>>(emptyList())
    val directionSteps: StateFlow<List<DirectionStep>> = _directionSteps.asStateFlow()

    /** Current direction step index */
    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private var currentRoute: Route? = null
    private var currentEdgeIndex = 0
    private var stepsOnCurrentEdge = 0
    private var navigationJob: Job? = null
    private var navigationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var sessionStartTime = 0L
    private var deviationCount = 0
    private var lastStepCount = 0
    private var softWarningSteps = 0
    private var consecutiveWrongHeadings = 0

    // Compass-based deviation tracking during straight walking
    private var consecutiveDeviations = 0
    private var lastDeviationWarningTime = 0L

    // Turn verification timing
    private var turnVerifyStartTime = 0L

    // ══════════════════════════════════════════════
    // Locale-aware string helpers
    // ══════════════════════════════════════════════

    /** Create a context with the user's chosen locale for string resources */
    private fun localizedContext(): Context {
        val prefs = appContext.getSharedPreferences("navigo_prefs", Context.MODE_PRIVATE)
        val langCode = prefs.getString("app_language", "en") ?: "en"
        val locale = if (langCode == "hi") Locale("hi", "IN") else Locale("en", "US")
        val config = Configuration(appContext.resources.configuration)
        config.setLocale(locale)
        return appContext.createConfigurationContext(config)
    }

    private fun str(resId: Int): String = localizedContext().getString(resId)
    private fun str(resId: Int, vararg args: Any): String = localizedContext().getString(resId, *args)

    /** Check if currently in Hindi mode */
    private fun isHindi(): Boolean {
        val prefs = appContext.getSharedPreferences("navigo_prefs", Context.MODE_PRIVATE)
        return prefs.getString("app_language", "en") == "hi"
    }

    /**
     * Translate an edge instruction ("Walk straight", "Turn right") to Hindi if needed.
     */
    private fun translateInstruction(instruction: String): String {
        if (!isHindi()) return instruction
        val lower = instruction.lowercase().trim()
        return when {
            lower.contains("walk straight") || lower == "straight" -> "सीधे चलें"
            lower.contains("turn right") -> "दाएं मुड़ें"
            lower.contains("turn left") -> "बाएं मुड़ें"
            lower.contains("go straight") -> "सीधे जाएं"
            lower.contains("slight right") -> "हल्का दाएं मुड़ें"
            lower.contains("slight left") -> "हल्का बाएं मुड़ें"
            lower.contains("u-turn") || lower.contains("u turn") -> "यू-टर्न लें"
            lower.contains("take stairs") || lower.contains("climb") -> "सीढ़ी चढ़ें"
            lower.contains("take elevator") || lower.contains("take lift") -> "लिफ्ट लें"
            lower.contains("continue") -> "आगे बढ़ते रहें"
            else -> instruction
        }
    }

    /**
     * Translate a direction label ("north", "south", "east", "west") to Hindi if needed.
     */
    private fun translateDirection(directionLabel: String): String {
        if (!isHindi()) return directionLabel
        return when (directionLabel.lowercase().trim()) {
            "north" -> "उत्तर"
            "south" -> "दक्षिण"
            "east" -> "पूर्व"
            "west" -> "पश्चिम"
            "northeast", "north-east" -> "उत्तर-पूर्व"
            "northwest", "north-west" -> "उत्तर-पश्चिम"
            "southeast", "south-east" -> "दक्षिण-पूर्व"
            "southwest", "south-west" -> "दक्षिण-पश्चिम"
            "forward" -> "आगे"
            else -> directionLabel
        }
    }

    fun getCurrentRoute(): Route? = currentRoute

    suspend fun startNavigation(route: Route) {
        stopNavigation()

        currentRoute = route
        currentEdgeIndex = 0
        stepsOnCurrentEdge = 0
        deviationCount = 0
        sessionStartTime = System.currentTimeMillis()
        softWarningSteps = 0
        consecutiveWrongHeadings = 0
        consecutiveDeviations = 0
        lastDeviationWarningTime = 0L
        turnVerifyStartTime = 0L

        _currentNodeId.value = route.nodes.firstOrNull()?.id
        _routeProgress.value = 0f
        _deviationDetected.value = false
        _navigationState.value = NavigationState.Navigating(
            currentNodeIndex = 0,
            stepsOnCurrentEdge = 0
        )

        // Build direction steps list
        buildDirectionSteps(route)
        _currentStepIndex.value = 0

        stepCounterManager.resetCount()
        stepCounterManager.startCounting()
        compassManager.startTracking()

        // Announce start with locale-aware instruction
        val firstEdge = route.edges.firstOrNull()
        val startNode = route.nodes.firstOrNull()
        val destNode = route.nodes.lastOrNull()

        val startInstruction = buildString {
            append(str(R.string.nav_starting, startNode?.name ?: "", destNode?.name ?: ""))
            append(" ")
            append(str(R.string.nav_total_distance, route.totalDistanceM.toInt()))
            append(" ")
            // Add node-type-specific info for start node
            if (startNode != null) {
                val nodeType = NodeType.fromString(startNode.type)
                append(nodeType.talkBackInstruction(startNode.name) + " ")
            }
            if (firstEdge != null) {
                val instr = translateInstruction(firstEdge.instruction)
                val dir = translateDirection(firstEdge.directionLabel)
                append(str(R.string.nav_first_instruction, instr.lowercase(), dir))
            }
        }

        _currentInstruction.value = startInstruction
        ttsManager.speak(startInstruction)

        lastStepCount = stepCounterManager.stepsSinceLastReset

        // Start the navigation loop
        navigationJob = navigationScope.launch {
            navigationLoop()
        }
    }

    fun stopNavigation() {
        navigationJob?.cancel()
        navigationJob = null
        stepCounterManager.stopCounting()
        compassManager.stopTracking()
        _navigationState.value = NavigationState.Idle
        _deviationDetected.value = false
        _currentInstruction.value = ""
        _directionSteps.value = emptyList()
        currentRoute = null
    }

    private fun buildDirectionSteps(route: Route) {
        val steps = mutableListOf<DirectionStep>()
        for (i in route.edges.indices) {
            val edge = route.edges[i]
            val fromNode = route.nodes[i]
            val toNode = route.nodes.getOrNull(i + 1)
            val nodeType = toNode?.let { NodeType.fromString(it.type) }
            val nodeTypeHint = toNode?.let { nodeType?.talkBackInstruction(it.name) } ?: ""

            steps.add(
                DirectionStep(
                    index = i,
                    instruction = translateInstruction(edge.instruction),
                    directionLabel = translateDirection(edge.directionLabel),
                    distanceM = edge.distanceM,
                    fromNodeName = fromNode.name,
                    toNodeName = toNode?.name ?: str(R.string.nav_destination),
                    toNodeType = toNode?.type ?: "",
                    nodeTypeHint = nodeTypeHint,
                    isCompleted = false
                )
            )
        }
        _directionSteps.value = steps
    }

    private fun updateDirectionStepCompleted(index: Int) {
        val steps = _directionSteps.value.toMutableList()
        if (index in steps.indices) {
            steps[index] = steps[index].copy(isCompleted = true)
            _directionSteps.value = steps
            _currentStepIndex.value = (index + 1).coerceAtMost(steps.size - 1)
        }
    }

    private suspend fun navigationLoop() {
        while (currentCoroutineContext().isActive) {
            delay(200) // Check every 200ms

            val route = currentRoute ?: break
            val currentSteps = stepCounterManager.stepsSinceLastReset
            val newSteps = currentSteps - lastStepCount

            when (val state = _navigationState.value) {
                is NavigationState.Navigating -> {
                    if (newSteps > 0) {
                        lastStepCount = currentSteps
                        handleNavigatingState(route, newSteps)
                    } else {
                        checkHeadingWhileWalking(route)
                    }
                }
                is NavigationState.VerifyingTurn -> {
                    handleVerifyingTurnState(route, state, newSteps)
                    if (newSteps > 0) lastStepCount = currentSteps
                }
                is NavigationState.SoftWarning -> {
                    if (newSteps > 0) {
                        lastStepCount = currentSteps
                        handleSoftWarningState(route, newSteps)
                    } else {
                        checkHeadingCorrectionInWarning(route)
                    }
                }
                is NavigationState.Recalculating -> { }
                is NavigationState.Arrived -> { break }
                is NavigationState.Error -> { break }
                is NavigationState.Idle -> { break }
            }
        }
    }

    private suspend fun checkHeadingWhileWalking(route: Route) {
        if (currentEdgeIndex >= route.edges.size) return

        val currentEdge = route.edges[currentEdgeIndex]
        val currentHeading = compassManager.currentHeading
        val headingDiff = abs(EdgeCalculator.headingDifference(currentHeading, currentEdge.directionDegrees))

        val now = System.currentTimeMillis()

        if (headingDiff > HEADING_TOLERANCE_WALKING) {
            consecutiveDeviations++

            if (consecutiveDeviations >= 3 && (now - lastDeviationWarningTime) > DEVIATION_COOLDOWN_MS) {
                lastDeviationWarningTime = now
                _deviationDetected.value = true

                val expectedDir = translateDirection(
                    EdgeCalculator.headingToDirectionLabel(currentEdge.directionDegrees)
                )
                val correctionMsg = if (headingDiff > 90f) {
                    str(R.string.nav_wrong_way, expectedDir)
                } else {
                    str(R.string.nav_drifting, expectedDir)
                }
                _currentInstruction.value = correctionMsg
                ttsManager.speakAsync(correctionMsg)
                hapticManager.vibrateWarning()
            }
        } else {
            if (consecutiveDeviations >= 3 && _deviationDetected.value) {
                _deviationDetected.value = false
                ttsManager.speakAsync(str(R.string.nav_back_on_track), flushQueue = false)
            }
            consecutiveDeviations = 0
        }
    }

    private suspend fun handleNavigatingState(route: Route, newSteps: Int) {
        if (currentEdgeIndex >= route.edges.size) {
            handleArrival(route)
            return
        }

        stepsOnCurrentEdge += newSteps
        val currentEdge = route.edges[currentEdgeIndex]
        val expectedSteps = (currentEdge.distanceM / STRIDE_LENGTH_M).toInt()

        val currentHeading = compassManager.currentHeading
        val headingDiff = abs(EdgeCalculator.headingDifference(currentHeading, currentEdge.directionDegrees))

        if (headingDiff <= HEADING_TOLERANCE_WALKING) {
            consecutiveDeviations = 0
            _deviationDetected.value = false
        }

        // Update progress
        val edgeProgress = stepsOnCurrentEdge.toFloat() / expectedSteps.coerceAtLeast(1)
        _routeProgress.value = (currentEdgeIndex + edgeProgress.coerceIn(0f, 1f)) / route.edges.size

        // Pre-announce next turn when approaching waypoint
        val stepsRemaining = expectedSteps - stepsOnCurrentEdge
        if (stepsRemaining in 2..4 && currentEdgeIndex + 1 < route.edges.size) {
            val nextEdge = route.edges[currentEdgeIndex + 1]
            val instr = translateInstruction(nextEdge.instruction)
            ttsManager.speakAsync(
                str(R.string.nav_in_steps, stepsRemaining, instr.lowercase()),
                flushQueue = false
            )
        }

        // Check if we've reached the next node
        val arrivalThreshold = (expectedSteps * 0.8f).toInt().coerceAtLeast(1)
        if (stepsOnCurrentEdge >= arrivalThreshold) {
            updateDirectionStepCompleted(currentEdgeIndex)

            currentEdgeIndex++
            stepsOnCurrentEdge = 0
            consecutiveDeviations = 0

            if (currentEdgeIndex >= route.edges.size) {
                handleArrival(route)
                return
            }

            val nextNode = route.nodes[currentEdgeIndex]
            _currentNodeId.value = nextNode.id

            val nextEdge = route.edges[currentEdgeIndex]

            // Build locale-aware instruction
            val nodeType = NodeType.fromString(nextNode.type)
            val nodeTypeInstruction = nodeType.talkBackInstruction(nextNode.name)
            val instr = translateInstruction(nextEdge.instruction)
            val dir = translateDirection(nextEdge.directionLabel)
            val fullInstruction = "$instr. ${str(R.string.nav_heading, dir)} $nodeTypeInstruction"

            _currentInstruction.value = fullInstruction
            ttsManager.speak(fullInstruction)

            // Trigger haptic pattern
            when {
                nextEdge.instruction.contains("right", ignoreCase = true) -> hapticManager.vibrateRight()
                nextEdge.instruction.contains("left", ignoreCase = true) -> hapticManager.vibrateLeft()
            }

            // Enter time-based turn verification
            consecutiveWrongHeadings = 0
            turnVerifyStartTime = System.currentTimeMillis()
            _navigationState.value = NavigationState.VerifyingTurn(
                expectedHeading = nextEdge.directionDegrees,
                stepsSinceInstruction = 0
            )
        } else {
            _navigationState.value = NavigationState.Navigating(
                currentNodeIndex = currentEdgeIndex,
                stepsOnCurrentEdge = stepsOnCurrentEdge
            )
        }
    }

    private suspend fun handleVerifyingTurnState(
        route: Route,
        state: NavigationState.VerifyingTurn,
        newSteps: Int
    ) {
        stepsOnCurrentEdge += newSteps
        val elapsed = System.currentTimeMillis() - turnVerifyStartTime

        if (elapsed < TURN_GRACE_PERIOD_MS) {
            return
        }

        val currentHeading = compassManager.currentHeading
        val headingDiff = abs(EdgeCalculator.headingDifference(currentHeading, state.expectedHeading))

        if (headingDiff <= HEADING_TOLERANCE_TURN) {
            consecutiveWrongHeadings = 0
            _deviationDetected.value = false
            _navigationState.value = NavigationState.Navigating(
                currentNodeIndex = currentEdgeIndex,
                stepsOnCurrentEdge = stepsOnCurrentEdge
            )
        } else {
            consecutiveWrongHeadings++

            if (consecutiveWrongHeadings >= 2) {
                val expectedDir = translateDirection(
                    EdgeCalculator.headingToDirectionLabel(state.expectedHeading)
                )
                _deviationDetected.value = true
                hapticManager.vibrateWarning()
                val warningMsg = str(R.string.nav_missed_turn, expectedDir)
                _currentInstruction.value = warningMsg
                ttsManager.speak(warningMsg)
                softWarningSteps = 0
                _navigationState.value = NavigationState.SoftWarning(
                    message = warningMsg
                )
            }
        }
    }

    private suspend fun checkHeadingCorrectionInWarning(route: Route) {
        if (currentEdgeIndex >= route.edges.size) return

        val currentEdge = route.edges[currentEdgeIndex]
        val currentHeading = compassManager.currentHeading
        val headingDiff = abs(EdgeCalculator.headingDifference(currentHeading, currentEdge.directionDegrees))

        if (headingDiff <= HEADING_TOLERANCE_TURN) {
            _deviationDetected.value = false
            val msg = str(R.string.nav_back_on_track)
            _currentInstruction.value = msg
            ttsManager.speakAsync(msg)
            _navigationState.value = NavigationState.Navigating(
                currentNodeIndex = currentEdgeIndex,
                stepsOnCurrentEdge = stepsOnCurrentEdge
            )
        }
    }

    private suspend fun handleSoftWarningState(route: Route, newSteps: Int) {
        stepsOnCurrentEdge += newSteps
        softWarningSteps += newSteps

        if (currentEdgeIndex >= route.edges.size) return

        val currentEdge = route.edges[currentEdgeIndex]
        val currentHeading = compassManager.currentHeading
        val headingDiff = abs(EdgeCalculator.headingDifference(currentHeading, currentEdge.directionDegrees))

        if (headingDiff <= HEADING_TOLERANCE_TURN) {
            _deviationDetected.value = false
            val msg = str(R.string.nav_back_on_track)
            _currentInstruction.value = msg
            ttsManager.speakAsync(msg)
            _navigationState.value = NavigationState.Navigating(
                currentNodeIndex = currentEdgeIndex,
                stepsOnCurrentEdge = stepsOnCurrentEdge
            )
            return
        }

        if (softWarningSteps > 4) {
            deviationCount++
            hapticManager.vibrateDeviation()
            val msg = str(R.string.nav_off_route)
            _currentInstruction.value = msg
            ttsManager.speak(msg)

            val lastKnownNode = route.nodes.getOrNull(currentEdgeIndex) ?: route.nodes.last()
            val estimatedNode = estimateCurrentPosition(lastKnownNode, route)

            _navigationState.value = NavigationState.Recalculating(estimatedPosition = estimatedNode)

            // Attempt to reroute
            try {
                val destination = route.nodes.last()
                val closestNode = findClosestNode(estimatedNode, route)
                val newRouteResult = navigationRepository.getRoute(
                    startNodeId = closestNode.id,
                    destinationNodeId = destination.id,
                    venueId = destination.venueId,
                    accessibleOnly = route.isAccessible
                )

                newRouteResult.fold(
                    onSuccess = { newRoute ->
                        currentRoute = newRoute
                        currentEdgeIndex = 0
                        stepsOnCurrentEdge = 0
                        stepCounterManager.resetCount()
                        lastStepCount = 0
                        _currentNodeId.value = newRoute.nodes.firstOrNull()?.id
                        _deviationDetected.value = false

                        buildDirectionSteps(newRoute)
                        _currentStepIndex.value = 0

                        val firstEdge = newRoute.edges.firstOrNull()
                        val instr = translateInstruction(firstEdge?.instruction ?: "Walk straight")
                        val dir = translateDirection(firstEdge?.directionLabel ?: "forward")
                        val newMsg = str(R.string.nav_new_route, instr, dir)
                        _currentInstruction.value = newMsg
                        ttsManager.speak(newMsg)
                        _navigationState.value = NavigationState.Navigating(
                            currentNodeIndex = 0,
                            stepsOnCurrentEdge = 0
                        )
                    },
                    onFailure = {
                        val errMsg = str(R.string.nav_reroute_failed)
                        _currentInstruction.value = errMsg
                        ttsManager.speak(errMsg)
                        _navigationState.value = NavigationState.Error("Rerouting failed: ${it.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Rerouting failed", e)
                _navigationState.value = NavigationState.Error("Rerouting error: ${e.message}")
            }
        }
    }

    private suspend fun handleArrival(route: Route) {
        val destination = route.nodes.last()
        _currentNodeId.value = destination.id
        _routeProgress.value = 1f

        _directionSteps.value = _directionSteps.value.map { it.copy(isCompleted = true) }
        _navigationState.value = NavigationState.Arrived(destination)

        val nodeType = NodeType.fromString(destination.type)
        val nodeTypeHint = nodeType.talkBackInstruction(destination.name)
        val arrivalMsg = str(R.string.nav_arrived, destination.name) + " " + nodeTypeHint
        _currentInstruction.value = arrivalMsg

        hapticManager.vibrateArrived()
        ttsManager.speak(arrivalMsg)

        // Log the session
        try {
            val log = RouteLogEntity(
                sessionId = UUID.randomUUID().toString(),
                venueId = destination.venueId,
                startNodeId = route.nodes.first().id,
                destinationNodeId = destination.id,
                startTime = sessionStartTime,
                endTime = System.currentTimeMillis(),
                deviationCount = deviationCount,
                completedSuccessfully = true,
                routeNodeIds = Gson().toJson(route.nodes.map { it.id })
            )
            routeLogDao.insertLog(log)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log route session", e)
        }

        stepCounterManager.stopCounting()
        compassManager.stopTracking()
    }

    private fun estimateCurrentPosition(lastKnownNode: LocationNode, route: Route): LocationNode {
        val heading = compassManager.currentHeading
        val distance = EdgeCalculator.stepsToDist(stepsOnCurrentEdge)
        val dx = sin(Math.toRadians(heading.toDouble())) * distance
        val dy = -cos(Math.toRadians(heading.toDouble())) * distance

        return lastKnownNode.copy(
            relativeX = lastKnownNode.relativeX + dx.toFloat() * 0.01f,
            relativeY = lastKnownNode.relativeY + dy.toFloat() * 0.01f
        )
    }

    private fun findClosestNode(estimatedPos: LocationNode, route: Route): LocationNode {
        val allNodes = route.nodes
        return allNodes.minByOrNull { node ->
            val dx = node.relativeX - estimatedPos.relativeX
            val dy = node.relativeY - estimatedPos.relativeY
            dx * dx + dy * dy
        } ?: route.nodes.first()
    }

    companion object {
        private const val TAG = "NavigationEngine"
        private const val STRIDE_LENGTH_M = 0.7f
        private const val HEADING_TOLERANCE_WALKING = 45f
        private const val HEADING_TOLERANCE_TURN = 35f
        private const val TURN_GRACE_PERIOD_MS = 1000L
        private const val DEVIATION_COOLDOWN_MS = 5000L
    }
}

/**
 * Represents a single step in turn-by-turn directions
 */
data class DirectionStep(
    val index: Int,
    val instruction: String,
    val directionLabel: String,
    val distanceM: Float,
    val fromNodeName: String,
    val toNodeName: String,
    val toNodeType: String,
    val nodeTypeHint: String,
    val isCompleted: Boolean
)
