package com.sensecode.navigo.mapshare.ui

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
import com.sensecode.navigo.mapshare.MapShareViewModel
import com.sensecode.navigo.navigation_ui.ui.GraphMapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapShareScreen(
    onNavigateBack: () -> Unit,
    viewModel: MapShareViewModel = hiltViewModel()
) {
    val venues by viewModel.venues.collectAsStateWithLifecycle()
    val localVenues by viewModel.localVenues.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val downloadingIds by viewModel.downloadingVenueIds.collectAsStateWithLifecycle()
    val downloadedIds by viewModel.downloadedVenueIds.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    // Map preview state
    val previewNodes by viewModel.previewNodes.collectAsStateWithLifecycle()
    val previewEdges by viewModel.previewEdges.collectAsStateWithLifecycle()
    val previewVenueName by viewModel.previewVenueName.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    val view = LocalView.current
    val context = LocalContext.current

    // Refresh local venues when screen appears
    LaunchedEffect(Unit) {
        viewModel.refreshLocalVenues()
    }

    // Auto-load first local venue map preview
    LaunchedEffect(localVenues) {
        if (localVenues.isNotEmpty() && previewNodes.isEmpty()) {
            val first = localVenues.first()
            viewModel.loadMapPreview(first.venueId, first.name)
        }
    }

    // Announce download completion
    LaunchedEffect(downloadedIds.size) {
        if (downloadedIds.isNotEmpty()) {
            view.announceForAccessibility("Venue downloaded successfully")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mapshare_title), fontSize = 22.sp) },
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
        },
        snackbarHost = {
            errorMessage?.let { error ->
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Error: $error"
                        liveRegion = LiveRegionMode.Assertive
                    }
                ) { Text(error, fontSize = 16.sp) }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchVenues(it)
                },
                placeholder = { Text(stringResource(R.string.search_venues), fontSize = 18.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .semantics { contentDescription = "Search for public venues. Type a venue name." },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                viewModel.searchVenues("")
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .semantics { contentDescription = "Clear search" }
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                }
            )

            // Map preview section (shown after download or local view)
            if (previewNodes.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .semantics {
                            contentDescription = "Map preview of $previewVenueName showing ${previewNodes.size} locations"
                        }
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${stringResource(R.string.map_preview)}: $previewVenueName",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD600),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearMapPreview() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .semantics { contentDescription = "Close map preview" }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        GraphMapView(
                            nodes = previewNodes,
                            edges = previewEdges,
                            currentNodeId = null,
                            routeNodeIds = emptyList(),
                            traversedNodeIds = emptyList(),
                            startNodeId = null,
                            destinationNodeId = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Main scrollable content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── My Local Venues Section ──
                if (localVenues.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.my_local_venues),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.semantics {
                                contentDescription = "${localVenues.size} locally saved venues"
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Venues you recorded and saved on this device",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(localVenues) { venue ->
                        LocalVenueCard(
                            venueName = venue.name,
                            venueId = venue.venueId,
                            floors = venue.floors,
                            nodeCount = venue.nodeCount,
                            onViewMap = { viewModel.loadMapPreview(venue.venueId, venue.name) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // ── Public Venues Section ──
                item {
                    Text(
                        stringResource(R.string.public_venues),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.semantics {
                            contentDescription = "Public venues available for download"
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
                                modifier = Modifier.semantics { contentDescription = "Loading public venues" }
                            )
                        }
                    }
                } else if (venues.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.SearchOff,
                                    contentDescription = "No venues found",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    stringResource(R.string.no_public_venues),
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.semantics {
                                        contentDescription = "No public venues found. Organizations can add venues using Setup Mode."
                                    }
                                )
                            }
                        }
                    }
                } else {
                    items(venues) { venue ->
                        VenueDownloadCard(
                            venueName = venue.name,
                            orgName = venue.orgName,
                            floors = venue.floors,
                            nodeCount = venue.nodeCount,
                            isDownloading = venue.venueId in downloadingIds,
                            isDownloaded = venue.venueId in downloadedIds,
                            onDownload = { viewModel.downloadVenue(venue.venueId) },
                            onViewMap = { viewModel.loadMapPreview(venue.venueId, venue.name) },
                            onShare = {
                                val shareText = "Check out \"${venue.name}\" on NaviGo! " +
                                        "An indoor venue map with ${venue.nodeCount} locations across ${venue.floors} floor(s). " +
                                        "Download NaviGo for accessible indoor navigation."
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
}

@Composable
private fun LocalVenueCard(
    venueName: String,
    venueId: String,
    floors: Int,
    nodeCount: Int,
    onViewMap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Local venue: $venueName. $floors floors, $nodeCount locations. Tap View Map to preview."
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
                    tint = Color(0xFFFFD600),
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
                    Text(
                        "Saved locally",
                        fontSize = 14.sp,
                        color = Color(0xFFFFD600)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onViewMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .semantics { contentDescription = "View map preview of $venueName" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD600),
                    contentColor = Color(0xFF1A1A00)
                )
            ) {
                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.view_map), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
