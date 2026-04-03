package com.sensecode.navigo.navigation_ui.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
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
import com.sensecode.navigo.R
import com.sensecode.navigo.navigation_ui.ConversationStep
import com.sensecode.navigo.navigation_ui.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalizationFlowScreen(
    venueId: String,
    onNavigationReady: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val conversationStep by viewModel.conversationStep.collectAsStateWithLifecycle()
    val spokenText by viewModel.spokenText.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val startNode by viewModel.startNode.collectAsStateWithLifecycle()
    val destinationNode by viewModel.destinationNode.collectAsStateWithLifecycle()
    val route by viewModel.route.collectAsStateWithLifecycle()
    val partialResult by viewModel.partialSpeechResult.collectAsStateWithLifecycle()
    val isListeningActive by viewModel.isListeningActive.collectAsStateWithLifecycle()

    val view = LocalView.current
    val micFocusRequester = remember { FocusRequester() }

    // Mic pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    LaunchedEffect(venueId) {
        viewModel.startConversation(venueId)
    }

    // Auto-navigate when confirmed
    LaunchedEffect(conversationStep) {
        if (conversationStep == ConversationStep.NAVIGATING) {
            onNavigationReady()
        }
    }

    // Announce step changes for TalkBack
    LaunchedEffect(conversationStep) {
        val announcement = when (conversationStep) {
            ConversationStep.ASKING_FLOOR -> "Step 1 of 3. Which floor are you on? Tap the microphone to speak."
            ConversationStep.ASKING_START -> "Step 2 of 3. Where are you right now? Tap the microphone to speak."
            ConversationStep.ASKING_DESTINATION -> "Step 3 of 3. Where would you like to go? Tap the microphone to speak."
            ConversationStep.CONFIRMED -> "Route found! Starting navigation."
            ConversationStep.NAVIGATING -> "Navigation is starting."
        }
        view.announceForAccessibility(announcement)
    }

    // Auto-focus mic button
    LaunchedEffect(conversationStep) {
        if (conversationStep != ConversationStep.CONFIRMED && conversationStep != ConversationStep.NAVIGATING) {
            kotlinx.coroutines.delay(500)
            try { micFocusRequester.requestFocus() } catch (_: Exception) {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.finding_your_route),
                        fontSize = 22.sp,
                        modifier = Modifier.semantics { contentDescription = "Finding your route screen" }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.stopListening()
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Cancel and go back" }
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
            // Stage indicator
            val stageText = when (conversationStep) {
                ConversationStep.ASKING_FLOOR -> stringResource(R.string.step_1_floor)
                ConversationStep.ASKING_START -> stringResource(R.string.step_2_start)
                ConversationStep.ASKING_DESTINATION -> stringResource(R.string.step_3_destination)
                ConversationStep.CONFIRMED -> stringResource(R.string.route_found)
                ConversationStep.NAVIGATING -> stringResource(R.string.starting_navigation)
            }

            Text(
                text = stageText,
                fontSize = 18.sp,
                color = Color(0xFFFFD600),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.semantics {
                    contentDescription = stageText
                    liveRegion = LiveRegionMode.Polite
                }
            )

            // Progress indicator
            Spacer(modifier = Modifier.height(12.dp))
            val progress = when (conversationStep) {
                ConversationStep.ASKING_FLOOR -> 0.33f
                ConversationStep.ASKING_START -> 0.66f
                ConversationStep.ASKING_DESTINATION -> 0.9f
                ConversationStep.CONFIRMED, ConversationStep.NAVIGATING -> 1f
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .semantics { contentDescription = "Progress: step ${(progress * 3).toInt()} of 3" }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Question prompt
            val promptText = when (conversationStep) {
                ConversationStep.ASKING_FLOOR -> stringResource(R.string.which_floor)
                ConversationStep.ASKING_START -> stringResource(R.string.where_are_you)
                ConversationStep.ASKING_DESTINATION -> stringResource(R.string.where_to_go)
                ConversationStep.CONFIRMED -> stringResource(R.string.route_found)
                ConversationStep.NAVIGATING -> stringResource(R.string.starting_navigation)
            }

            Text(
                text = promptText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics {
                    contentDescription = promptText
                    liveRegion = LiveRegionMode.Polite
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Microphone button
            if (conversationStep != ConversationStep.CONFIRMED &&
                conversationStep != ConversationStep.NAVIGATING
            ) {
                IconButton(
                    onClick = { viewModel.startListening() },
                    modifier = Modifier
                        .size(100.dp)
                        .scale(micScale)
                        .focusRequester(micFocusRequester)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            CircleShape
                        )
                        .semantics {
                            contentDescription = "Tap to speak your answer. Microphone button."
                        }
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Microphone",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Listening indicator
                if (isListeningActive) {
                    Text(
                        stringResource(R.string.listening),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFD600),
                        modifier = Modifier.semantics {
                            contentDescription = "Listening for your speech"
                            liveRegion = LiveRegionMode.Polite
                        }
                    )
                } else {
                    Text(
                        stringResource(R.string.tap_to_speak),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.semantics { contentDescription = "Tap the microphone button above to speak your answer" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Live partial transcript while user is speaking
            if (isListeningActive && partialResult.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "Hearing: $partialResult"
                            liveRegion = LiveRegionMode.Polite
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.hearing),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            partialResult,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFFD600).copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Spoken text display (final result)
            if (!isListeningActive && spokenText.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "You said: $spokenText"
                            liveRegion = LiveRegionMode.Polite
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.i_heard),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            spokenText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Loading indicator
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.semantics {
                        contentDescription = "Searching for location. Please wait."
                        liveRegion = LiveRegionMode.Polite
                    }
                )
                Text(
                    stringResource(R.string.searching),
                    fontSize = 18.sp,
                    modifier = Modifier.semantics { contentDescription = "Searching for your location" }
                )
            }

            // Route confirmation
            if (conversationStep == ConversationStep.CONFIRMED && route != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "Route found from ${startNode?.name} to ${destinationNode?.name}. " +
                                    "${route?.totalDistanceM?.toInt()} meters, approximately ${route?.estimatedMinutes} minutes walk."
                            liveRegion = LiveRegionMode.Assertive
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.route_found),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFFFFD600)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("From: ${startNode?.name}", fontSize = 18.sp)
                        Text("To: ${destinationNode?.name}", fontSize = 18.sp)
                        Text("Distance: ${route?.totalDistanceM?.toInt()} meters", fontSize = 18.sp)
                        Text("Time: ~${route?.estimatedMinutes} min", fontSize = 18.sp)
                    }
                }
            }

            // Error message
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    modifier = Modifier.semantics {
                        contentDescription = "Error: $error"
                        liveRegion = LiveRegionMode.Assertive
                    }
                )
            }
        }
    }
}
