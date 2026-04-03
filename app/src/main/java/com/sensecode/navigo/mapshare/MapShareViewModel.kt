package com.sensecode.navigo.mapshare

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.sensecode.navigo.data.remote.firebase.FirestoreVenueService
import com.sensecode.navigo.data.repository.NavigationRepository
import com.sensecode.navigo.data.repository.VenueRepository
import com.sensecode.navigo.domain.model.Edge
import com.sensecode.navigo.domain.model.LocationNode
import com.sensecode.navigo.domain.model.Venue
import com.sensecode.navigo.domain.usecase.DownloadVenueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapShareViewModel @Inject constructor(
    private val venueRepository: VenueRepository,
    private val downloadVenueUseCase: DownloadVenueUseCase,
    private val navigationRepository: NavigationRepository,
    private val firestoreVenueService: FirestoreVenueService
) : ViewModel() {

    private val _venues = MutableStateFlow<List<Venue>>(emptyList())
    val venues: StateFlow<List<Venue>> = _venues.asStateFlow()

    // Local venues from Room DB (user-recorded venues)
    private val _localVenues = MutableStateFlow<List<Venue>>(emptyList())
    val localVenues: StateFlow<List<Venue>> = _localVenues.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _downloadingVenueIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadingVenueIds: StateFlow<Set<String>> = _downloadingVenueIds.asStateFlow()

    private val _downloadedVenueIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadedVenueIds: StateFlow<Set<String>> = _downloadedVenueIds.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Map preview state
    private val _previewNodes = MutableStateFlow<List<LocationNode>>(emptyList())
    val previewNodes: StateFlow<List<LocationNode>> = _previewNodes.asStateFlow()

    private val _previewEdges = MutableStateFlow<List<Edge>>(emptyList())
    val previewEdges: StateFlow<List<Edge>> = _previewEdges.asStateFlow()

    private val _previewVenueName = MutableStateFlow("")
    val previewVenueName: StateFlow<String> = _previewVenueName.asStateFlow()

    private var searchJob: Job? = null

    /** Real-time Firestore listener registration — removed in onCleared() */
    private var venueListenerRegistration: ListenerRegistration? = null

    init {
        loadLocalVenues()
        startRealtimeVenueListener()
    }

    private fun loadLocalVenues() {
        viewModelScope.launch {
            _localVenues.value = venueRepository.getLocalVenues()
        }
    }

    fun refreshLocalVenues() {
        loadLocalVenues()
    }

    /**
     * Start a real-time Firestore snapshot listener for published venues.
     * Automatically updates [_venues] whenever venues are added, modified, or removed
     * in the Firestore "venues" collection — no manual refresh needed.
     */
    private fun startRealtimeVenueListener() {
        _isLoading.value = true
        venueListenerRegistration = firestoreVenueService.addVenueListListener(
            onUpdate = { venueList ->
                _venues.value = venueList
                _isLoading.value = false
                Log.d(TAG, "Real-time venue update: ${venueList.size} venues")
            },
            onError = { exception ->
                _errorMessage.value = "Failed to load venues: ${exception.message}"
                _isLoading.value = false
                Log.e(TAG, "Firestore listener error", exception)
            }
        )
    }

    fun searchVenues(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            _isLoading.value = true
            if (query.isBlank()) {
                // When search is cleared, the real-time listener is already active
                // Just wait for it to provide data (it fires immediately)
                _isLoading.value = _venues.value.isEmpty()
            } else {
                val result = venueRepository.searchPublicVenues(query)
                result.fold(
                    onSuccess = { _venues.value = it },
                    onFailure = { _errorMessage.value = "Search failed: ${it.message}" }
                )
            }
            _isLoading.value = false
        }
    }

    fun downloadVenue(venueId: String) {
        viewModelScope.launch {
            _downloadingVenueIds.value = _downloadingVenueIds.value + venueId
            val result = downloadVenueUseCase(venueId)
            result.fold(
                onSuccess = {
                    _downloadedVenueIds.value = _downloadedVenueIds.value + venueId
                    // Auto-show map preview after download
                    val venue = _venues.value.find { it.venueId == venueId }
                    loadMapPreview(venueId, venue?.name ?: venueId)
                    // Refresh local venues list
                    loadLocalVenues()
                },
                onFailure = {
                    _errorMessage.value = "Download failed: ${it.message}"
                }
            )
            _downloadingVenueIds.value = _downloadingVenueIds.value - venueId
        }
    }

    fun loadMapPreview(venueId: String, venueName: String) {
        viewModelScope.launch {
            try {
                val nodes = navigationRepository.getVenueNodes(venueId)
                val edges = navigationRepository.getVenueEdges(venueId)
                _previewNodes.value = nodes
                _previewEdges.value = edges
                _previewVenueName.value = venueName
            } catch (e: Exception) {
                _errorMessage.value = "Could not load map preview: ${e.message}"
            }
        }
    }

    fun clearMapPreview() {
        _previewNodes.value = emptyList()
        _previewEdges.value = emptyList()
        _previewVenueName.value = ""
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // Remove Firestore listener to prevent memory leaks
        venueListenerRegistration?.remove()
        venueListenerRegistration = null
        Log.d(TAG, "Firestore venue listener removed")
    }

    companion object {
        private const val TAG = "MapShareViewModel"
    }
}
