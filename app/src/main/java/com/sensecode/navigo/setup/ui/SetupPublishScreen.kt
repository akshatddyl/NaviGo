package com.sensecode.navigo.setup.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sensecode.navigo.setup.SetupUiState
import com.sensecode.navigo.setup.SetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPublishScreen(
    onComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publish to MapShare", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Go back" }
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uiState) {
                is SetupUiState.Publishing -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                            .semantics {
                                contentDescription = "Uploading venue to MapShare. Please wait."
                                liveRegion = LiveRegionMode.Polite
                            }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Uploading to MapShare...",
                        fontSize = 20.sp,
                        modifier = Modifier.semantics { contentDescription = "Uploading venue" }
                    )
                }
                is SetupUiState.Success -> {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Venue Saved Successfully!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD600),
                        modifier = Modifier.semantics {
                            contentDescription = "Venue saved successfully. You can now use it for navigation."
                            liveRegion = LiveRegionMode.Assertive
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Your venue has been saved locally and is ready for navigation.",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.semantics {
                            contentDescription = "Your venue has been saved locally and is ready for navigation."
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "Go to home screen" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD600),
                            contentColor = Color(0xFF1A1A00)
                        )
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Go Home", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                is SetupUiState.Error -> {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Upload Failed",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.semantics {
                            contentDescription = "Upload failed"
                            liveRegion = LiveRegionMode.Assertive
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        (uiState as SetupUiState.Error).message,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.semantics {
                            contentDescription = "Error: ${(uiState as SetupUiState.Error).message}"
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "Return to home screen" }
                    ) {
                        Text("Return Home", fontSize = 18.sp)
                    }
                }
                else -> {
                    // Default — show success for save-only flow
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Saved",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Venue Saved!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD600),
                        modifier = Modifier.semantics { contentDescription = "Venue saved successfully" }
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "Go to home screen" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD600),
                            contentColor = Color(0xFF1A1A00)
                        )
                    ) {
                        Text("Go Home", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
