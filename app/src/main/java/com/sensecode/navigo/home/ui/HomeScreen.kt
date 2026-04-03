package com.sensecode.navigo.home.ui

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
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
import com.sensecode.navigo.home.HomeViewModel
import com.sensecode.navigo.navigation_ui.ui.GraphMapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSetup: () -> Unit,
    onNavigateToMapShare: () -> Unit,
    onNavigateToPublisherLogin: () -> Unit,
    onNavigateToLocalization: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val localVenues by viewModel.localVenues.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val view = LocalView.current

    val context = LocalContext.current
    val activity = context as? android.app.Activity

    // Announce venue count when loaded
    LaunchedEffect(localVenues.size) {
        if (localVenues.isNotEmpty()) {
            view.announceForAccessibility("${localVenues.size} saved venues loaded")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshVenues()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.home_title),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD600),
                            modifier = Modifier.semantics { contentDescription = "NaviGo app home" }
                        )
                        Text(
                            stringResource(R.string.app_subtitle),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.semantics {
                                contentDescription = "Indoor navigation for everyone"
                            }
                        )
                    }
                },
                actions = {
                    // Language toggle button
                    FilledTonalButton(
                        onClick = {
                            viewModel.toggleLanguage()
                            // Recreate activity so attachBaseContext applies new locale
                            activity?.recreate()
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(40.dp)
                            .semantics {
                                contentDescription = if (currentLanguage == "en") {
                                    "Switch to Hindi. Currently English."
                                } else {
                                    "Switch to English. Currently Hindi."
                                }
                            },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFFFD600).copy(alpha = 0.2f),
                            contentColor = Color(0xFFFFD600)
                        )
                    ) {
                        Icon(
                            Icons.Default.Translate,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            // Show the language user will switch TO
                            text = stringResource(R.string.lang_toggle_label),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Action buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HomeActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AddLocation,
                        label = stringResource(R.string.setup_new_venue),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDesc = "Set up a new venue map. Opens venue recording mode.",
                        onClick = onNavigateToSetup
                    )

                    HomeActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Public,
                        label = stringResource(R.string.mapshare_browse),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDesc = "Browse and download public venue maps from MapShare.",
                        onClick = onNavigateToMapShare
                    )

                    HomeActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Login,
                        label = stringResource(R.string.publisher_login),
                        tint = MaterialTheme.colorScheme.tertiary,
                        contentDesc = "Login as a venue publisher to upload maps.",
                        onClick = onNavigateToPublisherLogin
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.saved_venues),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics {
                        contentDescription = "Saved venues section. ${localVenues.size} venues available."
                        liveRegion = LiveRegionMode.Polite
                    }
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.semantics { contentDescription = "Loading saved venues" }
                        )
                    }
                }
            } else if (localVenues.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .semantics {
                                contentDescription = "No venues saved yet. Download a public venue from MapShare, or set up your own using Setup Mode."
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.LocationOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                stringResource(R.string.no_venues_saved),
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.no_venues_hint),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            } else {
                items(localVenues) { venue ->
                    VenueCard(
                        venueName = venue.name,
                        venueId = venue.venueId,
                        floors = venue.floors,
                        nodeCount = venue.nodeCount,
                        onNavigate = { onNavigateToLocalization(venue.venueId) },
                        onShare = {
                            // Share venue info
                            val context = view.context
                            val shareText = "Check out \"${venue.name}\" on NaviGo! " +
                                    "${venue.floors} floor(s), ${venue.nodeCount} locations mapped for indoor navigation."
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Venue"))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeActionCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    contentDesc: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .height(110.dp)
            .semantics { contentDescription = contentDesc }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun VenueCard(
    venueName: String,
    venueId: String,
    floors: Int,
    nodeCount: Int,
    onNavigate: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Venue: $venueName. $floors floors, $nodeCount locations. Tap Navigate to start, or Share to send to others."
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        venueName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "$floors floor${if (floors != 1) "s" else ""} • $nodeCount locations",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigate,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .semantics { contentDescription = "Start navigation in $venueName" }
                ) {
                    Icon(
                        Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.navigate), fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier
                        .height(48.dp)
                        .semantics { contentDescription = "Share $venueName venue info" }
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.share), fontSize = 16.sp)
                }
            }
        }
    }
}
