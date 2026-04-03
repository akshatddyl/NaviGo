package com.sensecode.navigo.navigation_ui.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sensecode.navigo.domain.model.NavigationState
import com.sensecode.navigo.engine.DirectionStep
import com.sensecode.navigo.navigation_ui.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveNavigationScreen(
    venueId: String,
    onNavigationComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val navigationState by viewModel.navigationState.collectAsStateWithLifecycle()
    val routeProgress by viewModel.routeProgress.collectAsStateWithLifecycle()
    val allNodes by viewModel.allVenueNodes.collectAsStateWithLifecycle()
    val allEdges by viewModel.allVenueEdges.collectAsStateWithLifecycle()
    val currentNodeId by viewModel.currentNodeId.collectAsStateWithLifecycle()
    val deviationDetected by viewModel.deviationDetected.collectAsStateWithLifecycle()
    val route by viewModel.route.collectAsStateWithLifecycle()
    val currentInstruction by viewModel.currentInstruction.collectAsStateWithLifecycle()
    val directionSteps by viewModel.directionSteps.collectAsStateWithLifecycle()
    val currentStepIndex by viewModel.currentStepIndex.collectAsStateWithLifecycle()

    val view = LocalView.current

    // Announce instruction changes via TalkBack
    LaunchedEffect(currentInstruction) {
        if (currentInstruction.isNotBlank()) {
            view.announceForAccessibility(currentInstruction)
        }
    }

    // Border color animation for deviation
    val borderColor by animateColorAsState(
        targetValue = if (deviationDetected) Color.Red else Color.Transparent,
        label = "deviation_border"
    )

    // Handle arrival
    LaunchedEffect(navigationState) {
        if (navigationState is NavigationState.Arrived) {
            kotlinx.coroutines.delay(3000)
            onNavigationComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Navigation",
                        modifier = Modifier.semantics {
                            contentDescription = "Active navigation screen"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.stopNavigation()
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Stop navigation and go back" }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top: Graph Map (50%)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f)
                    .semantics { contentDescription = "Navigation map view showing your route" }
            ) {
                val routeNodeIds = route?.nodes?.map { it.id } ?: emptyList()
                val currentIdx = routeNodeIds.indexOf(currentNodeId)
                val traversedIds = if (currentIdx > 0) routeNodeIds.take(currentIdx) else emptyList()

                GraphMapView(
                    nodes = allNodes,
                    edges = allEdges,
                    currentNodeId = currentNodeId,
                    routeNodeIds = routeNodeIds,
                    traversedNodeIds = traversedIds,
                    startNodeId = route?.nodes?.firstOrNull()?.id,
                    destinationNodeId = route?.nodes?.lastOrNull()?.id
                )
            }

            // Middle: Current instruction card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .then(
                        if (deviationDetected) Modifier.border(3.dp, borderColor, MaterialTheme.shapes.medium)
                        else Modifier
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (deviationDetected) Color(0xFF3D1010) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    when (val state = navigationState) {
                        is NavigationState.Navigating -> {
                            val currentEdge = route?.edges?.getOrNull(state.currentNodeIndex)
                            val expectedSteps = ((currentEdge?.distanceM ?: 0f) / 0.7f).toInt()

                            // Current instruction - large text with live region
                            Text(
                                text = currentInstruction.ifBlank {
                                    currentEdge?.instruction ?: "Walk straight"
                                },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD600),
                                modifier = Modifier.semantics {
                                    contentDescription = "Current instruction: ${currentInstruction.ifBlank { currentEdge?.instruction ?: "Walk straight" }}"
                                    liveRegion = LiveRegionMode.Polite
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Progress
                            LinearProgressIndicator(
                                progress = { routeProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .semantics {
                                        contentDescription = "Route progress: ${(routeProgress * 100).toInt()} percent"
                                        liveRegion = LiveRegionMode.Polite
                                    }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Steps: ${state.stepsOnCurrentEdge}/$expectedSteps",
                                    fontSize = 18.sp,
                                    modifier = Modifier.semantics {
                                        contentDescription = "Steps taken: ${state.stepsOnCurrentEdge} of $expectedSteps"
                                        liveRegion = LiveRegionMode.Polite
                                    }
                                )
                                route?.let {
                                    val remaining = it.totalDistanceM * (1 - routeProgress)
                                    val timeRemaining = (remaining / 0.9f / 60).toInt()
                                    Text(
                                        "~${timeRemaining + 1} min left",
                                        fontSize = 18.sp,
                                        modifier = Modifier.semantics {
                                            contentDescription = "About ${timeRemaining + 1} minutes remaining"
                                        }
                                    )
                                }
                            }
                        }

                        is NavigationState.VerifyingTurn -> {
                            Text(
                                "Verifying turn...",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD600),
                                modifier = Modifier.semantics {
                                    contentDescription = "Verifying your turn. Please complete your turn."
                                    liveRegion = LiveRegionMode.Polite
                                }
                            )
                            Text(
                                "Please complete your turn",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        is NavigationState.SoftWarning -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    state.message,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.semantics {
                                        contentDescription = "Warning: ${state.message}"
                                        liveRegion = LiveRegionMode.Assertive
                                    }
                                )
                            }
                        }

                        is NavigationState.Recalculating -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .semantics {
                                            contentDescription = "Recalculating your route"
                                        }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Recalculating route...",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.semantics {
                                        contentDescription = "Recalculating route"
                                        liveRegion = LiveRegionMode.Polite
                                    }
                                )
                            }
                        }

                        is NavigationState.Arrived -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Arrived",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Arrived!",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50),
                                        modifier = Modifier.semantics {
                                            contentDescription = "You have arrived at ${state.destination.name}"
                                            liveRegion = LiveRegionMode.Assertive
                                        }
                                    )
                                    Text(
                                        state.destination.name,
                                        fontSize = 22.sp,
                                        color = Color(0xFFFFD600)
                                    )
                                }
                            }
                        }

                        is NavigationState.Error -> {
                            Text(
                                "Navigation Error",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.semantics {
                                    contentDescription = "Navigation error: ${state.message}"
                                    liveRegion = LiveRegionMode.Assertive
                                }
                            )
                            Text(state.message, fontSize = 18.sp)
                        }

                        is NavigationState.Idle -> {
                            Text(
                                "Navigation stopped",
                                fontSize = 22.sp,
                                modifier = Modifier.semantics {
                                    contentDescription = "Navigation has stopped"
                                }
                            )
                        }
                    }
                }
            }

            // Bottom: Step-by-step direction list
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        "Directions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .semantics { contentDescription = "Step by step directions" }
                    )

                    if (directionSteps.isNotEmpty()) {
                        LazyColumn {
                            itemsIndexed(directionSteps) { index, step ->
                                DirectionStepItem(
                                    step = step,
                                    isCurrent = index == currentStepIndex &&
                                            navigationState is NavigationState.Navigating
                                )
                            }
                        }
                    }
                }
            }

            // Stop navigation button
            if (navigationState !is NavigationState.Arrived &&
                navigationState !is NavigationState.Idle
            ) {
                Button(
                    onClick = {
                        viewModel.stopNavigation()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(56.dp)
                        .semantics { contentDescription = "Stop navigation" }
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stop Navigation", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun DirectionStepItem(
    step: DirectionStep,
    isCurrent: Boolean
) {
    val bgColor = when {
        isCurrent -> Color(0xFF3D3400) // Highlighted current step
        step.isCompleted -> Color(0xFF1A2E1A) // Completed green tint
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .semantics {
                contentDescription = buildString {
                    append("Step ${step.index + 1}: ")
                    append("${step.instruction}, heading ${step.directionLabel}. ")
                    append("${step.distanceM.toInt()} meters from ${step.fromNodeName} to ${step.toNodeName}. ")
                    if (step.nodeTypeHint.isNotBlank()) append(step.nodeTypeHint)
                    if (step.isCompleted) append(" Completed.")
                    if (isCurrent) append(" Current step.")
                }
            },
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Step number / check icon
            if (step.isCompleted) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    "${step.index + 1}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrent) Color(0xFFFFD600) else Color(0xFF888888),
                    modifier = Modifier.width(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Direction icon
            val dirIcon = when {
                step.instruction.contains("right", ignoreCase = true) -> Icons.Default.KeyboardArrowRight
                step.instruction.contains("left", ignoreCase = true) -> Icons.Default.KeyboardArrowLeft
                else -> Icons.Default.ArrowUpward
            }
            Icon(
                dirIcon,
                contentDescription = null,
                tint = if (isCurrent) Color(0xFFFFD600) else Color(0xFFE0E0E0),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    step.instruction,
                    fontSize = 18.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) Color(0xFFFFD600) else Color.White
                )
                Text(
                    "${step.distanceM.toInt()}m → ${step.toNodeName}",
                    fontSize = 16.sp,
                    color = Color(0xFFAAAAAA)
                )
            }
        }
    }
}
