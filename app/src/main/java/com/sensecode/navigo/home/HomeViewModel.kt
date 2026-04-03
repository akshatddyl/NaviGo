package com.sensecode.navigo.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensecode.navigo.audio.SpeechInputManager
import com.sensecode.navigo.audio.TtsManager
import com.sensecode.navigo.data.local.dao.EdgeDao
import com.sensecode.navigo.data.local.dao.LocationNodeDao
import com.sensecode.navigo.data.local.entity.EdgeEntity
import com.sensecode.navigo.data.local.entity.LocationNodeEntity
import com.sensecode.navigo.data.remote.firebase.FirebaseAuthService
import com.sensecode.navigo.data.repository.VenueRepository
import com.sensecode.navigo.domain.model.Venue
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val venueRepository: VenueRepository,
    private val nodeDao: LocationNodeDao,
    private val edgeDao: EdgeDao,
    private val speechInputManager: SpeechInputManager,
    private val ttsManager: TtsManager,
    private val authService: FirebaseAuthService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _localVenues = MutableStateFlow<List<Venue>>(emptyList())
    val localVenues: StateFlow<List<Venue>> = _localVenues.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _uploadResult = MutableSharedFlow<Result<String>>()
    val uploadResult: SharedFlow<Result<String>> = _uploadResult.asSharedFlow()

    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val prefs by lazy {
        context.getSharedPreferences("navigo_prefs", Context.MODE_PRIVATE)
    }

    init {
        val savedLang = prefs.getString("app_language", "en") ?: "en"
        _currentLanguage.value = savedLang
        applyLanguage(savedLang)
        loadLocalVenues()
    }

    private fun loadLocalVenues() {
        viewModelScope.launch {
            _isLoading.value = true
            seedDemoVenueIfNeeded()
            _localVenues.value = venueRepository.getLocalVenues()
            _isLoading.value = false
        }
    }

    fun refreshVenues() {
        loadLocalVenues()
    }

    fun uploadVenue(venue: Venue) {
        viewModelScope.launch {
            val user = authService.getCurrentUser()
            if (user == null) {
                _uploadResult.emit(Result.failure(Exception("Please login as a publisher to upload maps.")))
                return@launch
            }

            _isUploading.value = true
            // Append user UID to venue name if it's the demo venue to ensure unique ID/path if needed
            // but the repository handles the Firestore document mapping.
            val result = venueRepository.uploadVenueToMapShare(venue.venueId, user.uid)
            _isUploading.value = false
            
            if (result.isSuccess) {
                _uploadResult.emit(Result.success(venue.name))
            } else {
                _uploadResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Upload failed")))
            }
        }
    }

    fun toggleLanguage() {
        val newLang = if (_currentLanguage.value == "en") "hi" else "en"
        _currentLanguage.value = newLang
        prefs.edit().putString("app_language", newLang).apply()
        applyLanguage(newLang)
    }

    private fun applyLanguage(langCode: String) {
        val speechLocale = if (langCode == "hi") "hi-IN" else "en-US"
        speechInputManager.setLanguage(speechLocale)
        ttsManager.setLanguage(langCode)
    }

    private suspend fun seedDemoVenueIfNeeded() {
        try {
            val existingNodes = nodeDao.getNodesByVenue("demo_venue")
            if (existingNodes.isNotEmpty()) return
            val json = context.assets.open("demo_venue_data.json").bufferedReader().use { it.readText() }
            val demoData = Gson().fromJson(json, DemoVenueData::class.java)
            nodeDao.insertAllNodes(demoData.nodes.map { node ->
                LocationNodeEntity(node.id, node.name, node.floor, node.venueId, node.accessible, node.type, node.relativeX, node.relativeY)
            })
            edgeDao.insertAllEdges(demoData.edges.map { edge ->
                EdgeEntity(edge.fromNodeId, edge.toNodeId, edge.venueId, edge.distanceM, edge.directionDegrees, edge.directionLabel, edge.instruction, edge.hasStairs, edge.estimatedSeconds)
            })
        } catch (e: Exception) {
            android.util.Log.w("HomeViewModel", "Demo venue data not loaded: ${e.message}")
        }
    }
}

data class DemoVenueData(val nodes: List<DemoNode>, val edges: List<DemoEdge>)
data class DemoNode(val id: String, val name: String, val floor: Int, val venueId: String, val accessible: Boolean, val type: String, val relativeX: Float, val relativeY: Float)
data class DemoEdge(val fromNodeId: String, val toNodeId: String, val venueId: String, val distanceM: Float, val directionDegrees: Float, val directionLabel: String, val instruction: String, val hasStairs: Boolean, val estimatedSeconds: Int)
