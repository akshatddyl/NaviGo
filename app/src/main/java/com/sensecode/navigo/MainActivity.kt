package com.sensecode.navigo

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.sensecode.navigo.auth.ui.PublisherLoginScreen
import com.sensecode.navigo.home.ui.HomeScreen
import com.sensecode.navigo.mapshare.ui.MapShareScreen
import com.sensecode.navigo.navigation_ui.ui.ActiveNavigationScreen
import com.sensecode.navigo.navigation_ui.ui.LocalizationFlowScreen
import com.sensecode.navigo.onboarding.OnboardingScreen
import com.sensecode.navigo.navigation_ui.NavigationViewModel
import com.sensecode.navigo.setup.SetupViewModel
import com.sensecode.navigo.setup.ui.SetupHomeScreen
import com.sensecode.navigo.setup.ui.SetupPublishScreen
import com.sensecode.navigo.setup.ui.SetupRecordingScreen
import com.sensecode.navigo.setup.ui.SetupReviewScreen
import com.sensecode.navigo.ui.theme.NaviGoTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Apply saved language locale BEFORE the activity creates its views
        val prefs = newBase.getSharedPreferences("navigo_prefs", Context.MODE_PRIVATE)
        val langCode = prefs.getString("app_language", "en") ?: "en"
        val locale = if (langCode == "hi") Locale("hi", "IN") else Locale("en", "US")

        val config = Configuration(newBase.resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NaviGoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RequestPermissionsAndNavigate()
                }
            }
        }
    }

    /**
     * Called from HomeViewModel.toggleLanguage() to recreate the activity
     * so the new locale takes effect on all string resources.
     */
    companion object {
        var instance: MainActivity? = null
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionsAndNavigate() {
    val permissions = mutableListOf(
        Manifest.permission.RECORD_AUDIO
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.BODY_SENSORS)
    }

    val permissionsState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    NaviGoNavHost()
}

@Composable
fun NaviGoNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Check if onboarding is completed
    val prefs = context.getSharedPreferences("navigo_prefs", Context.MODE_PRIVATE)
    val hasCompletedOnboarding = remember {
        mutableStateOf(prefs.getBoolean("onboarding_completed", false))
    }

    val startDestination = if (hasCompletedOnboarding.value) "home" else "onboarding"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding screen — shown only on first launch
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    hasCompletedOnboarding.value = true
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToSetup = { navController.navigate("setup_flow") },
                onNavigateToMapShare = { navController.navigate("mapshare") },
                onNavigateToPublisherLogin = { navController.navigate("publisher_login") },
                onNavigateToLocalization = { venueId ->
                    navController.navigate("nav_flow/$venueId")
                }
            )
        }

        // ── Nested navigation graph for the entire Setup flow ──
        // All screens inside share the same SetupViewModel instance,
        // so recorded nodes persist across recording → review → publish.
        navigation(
            startDestination = "setup_home",
            route = "setup_flow"
        ) {
            composable("setup_home") {
                SetupHomeScreen(
                    onStartMapping = { navController.navigate("setup_recording") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("setup_recording") { backStackEntry ->
                // Scope ViewModel to the parent "setup_flow" nav graph
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("setup_flow")
                }
                val sharedViewModel: SetupViewModel = hiltViewModel(parentEntry)

                SetupRecordingScreen(
                    onStopRecording = { navController.navigate("setup_review") },
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = sharedViewModel
                )
            }

            composable("setup_review") { backStackEntry ->
                // Scope ViewModel to the parent "setup_flow" nav graph
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("setup_flow")
                }
                val sharedViewModel: SetupViewModel = hiltViewModel(parentEntry)

                SetupReviewScreen(
                    onSaveAndPublish = { navController.navigate("setup_publish") },
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = sharedViewModel
                )
            }

            composable("setup_publish") { backStackEntry ->
                // Scope ViewModel to the parent "setup_flow" nav graph
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("setup_flow")
                }
                val sharedViewModel: SetupViewModel = hiltViewModel(parentEntry)

                SetupPublishScreen(
                    onComplete = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = sharedViewModel
                )
            }
        }

        // ── Nested navigation graph for the Navigation flow ──
        // Localization and ActiveNavigation share the same NavigationViewModel,
        // so route data persists across localization → active navigation.
        navigation(
            startDestination = "localization_screen/{venueId}",
            route = "nav_flow/{venueId}",
            arguments = listOf(navArgument("venueId") { type = NavType.StringType })
        ) {
            composable(
                route = "localization_screen/{venueId}",
                arguments = listOf(navArgument("venueId") { type = NavType.StringType })
            ) { backStackEntry ->
                val venueId = backStackEntry.arguments?.getString("venueId") ?: ""
                // Scope ViewModel to the parent "nav_flow/{venueId}" nav graph
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("nav_flow/{venueId}")
                }
                val sharedViewModel: NavigationViewModel = hiltViewModel(parentEntry)

                LocalizationFlowScreen(
                    venueId = venueId,
                    onNavigationReady = {
                        navController.navigate("active_navigation_screen/$venueId")
                    },
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = sharedViewModel
                )
            }

            composable(
                route = "active_navigation_screen/{venueId}",
                arguments = listOf(navArgument("venueId") { type = NavType.StringType })
            ) { backStackEntry ->
                val venueId = backStackEntry.arguments?.getString("venueId") ?: ""
                // Scope ViewModel to the parent "nav_flow/{venueId}" nav graph
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("nav_flow/{venueId}")
                }
                val sharedViewModel: NavigationViewModel = hiltViewModel(parentEntry)

                ActiveNavigationScreen(
                    venueId = venueId,
                    onNavigationComplete = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = sharedViewModel
                )
            }
        }

        composable("mapshare") {
            MapShareScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("publisher_login") {
            PublisherLoginScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
