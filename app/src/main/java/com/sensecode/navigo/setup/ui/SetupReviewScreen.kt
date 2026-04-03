package com.sensecode.navigo.setup.ui

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sensecode.navigo.setup.SetupUiState
import com.sensecode.navigo.setup.SetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupReviewScreen(
    onSaveAndPublish: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val recordedNodes by viewModel.recordedNodes.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var venueName by remember { mutableStateOf("") }
    var venueAddress by remember { mutableStateOf("") }
    var orgName by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("0") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review & Save", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Go back to recording" }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    "Recorded ${recordedNodes.size} Nodes",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics {
                        contentDescription = "Recorded ${recordedNodes.size} nodes. Review them below."
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(recordedNodes) { index, node ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .semantics {
                            contentDescription = "Node ${index + 1}: ${node.name}, type ${node.type}, ${if (node.accessible) "accessible" else "not accessible"}"
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${index + 1}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD600),
                            modifier = Modifier.width(32.dp)
                        )
                        Column {
                            Text(node.name, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                            Text(
                                "${node.type} • ${if (node.accessible) "Accessible" else "Not accessible"}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Venue Details",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { contentDescription = "Venue details form. Fill in the details below." }
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = venueName,
                    onValueChange = { venueName = it },
                    label = { Text("Venue Name", fontSize = 16.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Enter venue name" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = venueAddress,
                    onValueChange = { venueAddress = it },
                    label = { Text("Address", fontSize = 16.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Enter venue address" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = orgName,
                    onValueChange = { orgName = it },
                    label = { Text("Organization Name", fontSize = 16.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Enter organization name" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = floor,
                    onValueChange = { floor = it },
                    label = { Text("Floor Number", fontSize = 16.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Enter floor number" }
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (uiState) {
                    is SetupUiState.Saving -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.semantics { contentDescription = "Saving venue, please wait" }
                            )
                        }
                    }
                    is SetupUiState.Error -> {
                        Text(
                            (uiState as SetupUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 18.sp,
                            modifier = Modifier.semantics { contentDescription = "Error: ${(uiState as SetupUiState.Error).message}" }
                        )
                    }
                    is SetupUiState.Success -> {
                        LaunchedEffect(Unit) {
                            onSaveAndPublish()
                        }
                    }
                    else -> {}
                }

                Button(
                    onClick = {
                        viewModel.saveSession(
                            venueName = venueName,
                            venueAddress = venueAddress,
                            orgName = orgName,
                            floor = floor.toIntOrNull() ?: 0
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .semantics { contentDescription = "Save venue locally with ${recordedNodes.size} nodes" },
                    enabled = venueName.isNotBlank() && recordedNodes.size >= 2,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD600),
                        contentColor = Color(0xFF1A1A00)
                    )
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Venue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
