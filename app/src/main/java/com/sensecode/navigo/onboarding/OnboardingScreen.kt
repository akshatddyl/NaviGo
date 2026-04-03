package com.sensecode.navigo.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val focusRequester = remember { FocusRequester() }

    // Auto-focus for TalkBack
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        try { focusRequester.requestFocus() } catch (_: Exception) {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App title
            Text(
                text = "NaviGo",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD600),
                modifier = Modifier.semantics {
                    contentDescription = "Welcome to NaviGo"
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Indoor Navigation for Everyone",
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics {
                    contentDescription = "Indoor navigation for everyone"
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Question
            Text(
                text = "How will you use this app?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics {
                    contentDescription = "How will you use this app? Choose User or Helper."
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // USER button
            OnboardingRoleButton(
                label = "USER",
                description = "I need navigation assistance",
                icon = Icons.Default.Accessibility,
                contentDesc = "I am a User. I need indoor navigation help. Tap to select.",
                modifier = Modifier.focusRequester(focusRequester),
                onClick = {
                    viewModel.selectRole("user")
                    onComplete()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // HELPER button
            OnboardingRoleButton(
                label = "HELPER",
                description = "I help set up venue maps",
                icon = Icons.Default.People,
                contentDesc = "I am a Helper. I help set up venue maps for others. Tap to select.",
                onClick = {
                    viewModel.selectRole("helper")
                    onComplete()
                }
            )
        }
    }
}

@Composable
private fun OnboardingRoleButton(
    label: String,
    description: String,
    icon: ImageVector,
    contentDesc: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .semantics { contentDescription = contentDesc },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2A2A2A),
            contentColor = Color.White
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 2.dp,
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFFFFD600)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD600)
                )
                Text(
                    text = description,
                    fontSize = 16.sp,
                    color = Color(0xFFE0E0E0)
                )
            }
        }
    }
}
