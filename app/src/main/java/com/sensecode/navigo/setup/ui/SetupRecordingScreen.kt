package com.sensecode.navigo.setup.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.sensecode.navigo.setup.SetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupRecordingScreen(
    onStopRecording: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val stepCount by viewModel.currentStepCount.collectAsStateWithLifecycle()
    val heading by viewModel.currentHeading.collectAsStateWithLifecycle()
    val recordedNodes by viewModel.recordedNodes.collectAsStateWithLifecycle()
    val isActive by viewModel.isSessionActive.collectAsStateWithLifecycle()

    var showNodeDialog by remember { mutableStateOf(false) }
    val view = LocalView.current

    LaunchedEffect(Unit) {
        if (!isActive) {
            viewModel.startRecording()
        }
    }

    // Announce when a new node is dropped
    LaunchedEffect(recordedNodes.size) {
        if (recordedNodes.isNotEmpty()) {
            val lastNode = recordedNodes.last()
            view.announceForAccessibility("Node dropped: ${lastNode.name}. Total nodes: ${recordedNodes.size}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recording", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Stop recording and go back" }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.stopRecording()
                            onStopRecording()
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .semantics { contentDescription = "Stop recording and review your nodes" }
                    ) {
                        Text("Stop", color = MaterialTheme.colorScheme.error, fontSize = 18.sp)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNodeDialog = true },
                icon = { Icon(Icons.Default.AddLocation, contentDescription = null) },
                text = { Text("Drop Node", fontSize = 18.sp) },
                containerColor = Color(0xFFFFD600),
                contentColor = Color(0xFF1A1A00),
                modifier = Modifier
                    .height(56.dp)
                    .semantics { contentDescription = "Drop a new node at your current location" }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Sensor dashboard
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Live sensor data: $stepCount steps, heading ${heading.toInt()} degrees, ${recordedNodes.size} nodes recorded" }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Live Sensors",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = "Steps", modifier = Modifier.size(28.dp))
                            Text(
                                "$stepCount",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.semantics {
                                    contentDescription = "$stepCount steps"
                                    liveRegion = LiveRegionMode.Polite
                                }
                            )
                            Text("Steps", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Explore, contentDescription = "Heading", modifier = Modifier.size(28.dp))
                            Text(
                                "${heading.toInt()}°",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.semantics {
                                    contentDescription = "Heading ${heading.toInt()} degrees"
                                    liveRegion = LiveRegionMode.Polite
                                }
                            )
                            Text("Heading", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Place, contentDescription = "Nodes", modifier = Modifier.size(28.dp))
                            Text(
                                "${recordedNodes.size}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.semantics {
                                    contentDescription = "${recordedNodes.size} nodes recorded"
                                    liveRegion = LiveRegionMode.Polite
                                }
                            )
                            Text("Nodes", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Recorded Nodes",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.semantics { contentDescription = "List of ${recordedNodes.size} recorded nodes" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (recordedNodes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No nodes yet. Walk to a location and tap 'Drop Node'.",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.semantics {
                            contentDescription = "No nodes recorded yet. Walk to a location and tap the Drop Node button."
                        }
                    )
                }
            } else {
                LazyColumn {
                    items(recordedNodes) { node ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .semantics {
                                    contentDescription = "Node: ${node.name}, type: ${node.type}, ${node.stepsFromPrevious} steps from previous node"
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (node.type) {
                                        "room" -> Icons.Default.MeetingRoom
                                        "junction" -> Icons.Default.CallSplit
                                        "entrance", "exit" -> Icons.Default.ExitToApp
                                        "staircase" -> Icons.Default.TrendingUp
                                        "elevator" -> Icons.Default.SwapVert
                                        else -> Icons.Default.Place
                                    },
                                    contentDescription = node.type,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(node.name, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                                    Text(
                                        "${node.type} • ${node.stepsFromPrevious} steps from prev",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNodeDialog) {
        NodeDropDialog(
            onDismiss = { showNodeDialog = false },
            onConfirm = { name, type, accessible, hasStairs, hasElevator ->
                viewModel.dropNode(name, type, accessible, hasStairs, hasElevator)
                showNodeDialog = false
            }
        )
    }
}
