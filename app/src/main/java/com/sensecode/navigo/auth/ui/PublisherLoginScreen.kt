package com.sensecode.navigo.auth.ui

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sensecode.navigo.auth.AuthUiState
import com.sensecode.navigo.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublisherLoginScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isCreateMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publisher Login", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Go back to home" }
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
                is AuthUiState.LoggedIn -> {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Logged in",
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFFFFD600)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Signed in as",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        userEmail,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.semantics {
                            contentDescription = "Signed in as $userEmail"
                            liveRegion = LiveRegionMode.Polite
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedButton(
                        onClick = { viewModel.signOut() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "Sign out of publisher account" }
                    ) {
                        Text("Sign Out", fontSize = 18.sp)
                    }
                }

                else -> {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Login",
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFFFFD600)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        if (isCreateMode) "Create Publisher Account" else "Publisher Login",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.semantics {
                            contentDescription = if (isCreateMode) "Create publisher account form" else "Publisher login form"
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Publishers can upload venue maps to MapShare",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", fontSize = 16.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Enter your email address" },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", fontSize = 16.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Enter your password. Minimum 6 characters." },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState is AuthUiState.Error) {
                        Text(
                            (uiState as AuthUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 18.sp,
                            modifier = Modifier.semantics {
                                contentDescription = "Error: ${(uiState as AuthUiState.Error).message}"
                                liveRegion = LiveRegionMode.Assertive
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Button(
                        onClick = {
                            if (isCreateMode) viewModel.createAccount(email, password)
                            else viewModel.signIn(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics {
                                contentDescription = if (isCreateMode) "Create your publisher account" else "Sign in to your account"
                            },
                        enabled = email.isNotBlank() && password.length >= 6 && uiState !is AuthUiState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD600),
                            contentColor = Color(0xFF1A1A00)
                        )
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF1A1A00)
                            )
                        } else {
                            Text(
                                if (isCreateMode) "Create Account" else "Sign In",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { isCreateMode = !isCreateMode },
                        modifier = Modifier
                            .height(48.dp)
                            .semantics {
                                contentDescription = if (isCreateMode) "Switch to sign in mode" else "Switch to create account mode"
                            }
                    ) {
                        Text(
                            if (isCreateMode) "Already have an account? Sign In"
                            else "New publisher? Create Account",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
