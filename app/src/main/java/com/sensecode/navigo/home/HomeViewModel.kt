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
import com.sensecode.navigo.data.repository.VenueRepository
import com.sensecode.navigo.domain.model.Venue
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _localVenues = MutableStateFlow<List<Venue>>(emptyList())
    val localVenues: StateFlow<List<Venue>> = _localVenues.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Current language code: "en" or "hi" */
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val prefs by lazy {
        context.getSharedPreferences("navigo_prefs", Context.MODE_PRIVATE)
    }

    init {
        // Restore saved language preference
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

    /**
     * Toggle between English and Hindi.
     * Persists preference and updates speech recognizer locale + TTS language.
     * The calling Composable must call activity.recreate() afterwards
     * since @ApplicationContext cannot be cast to Activity.
     */
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

            // Load demo data from assets
            val json = context.assets.open("demo_venue_data.json").bufferedReader().use { it.readText() }
            val gson = Gson()

            val demoData = gson.fromJson(json, DemoVenueData::class.java)

            val nodeEntities = demoData.nodes.map { node ->
                LocationNodeEntity(
                    id = node.id,
                    name = node.name,
                    floor = node.floor,
                    venueId = node.venueId,
                    accessible = node.accessible,
                    type = node.type,
                    relativeX = node.relativeX,
                    relativeY = node.relativeY
                )
            }

            val edgeEntities = demoData.edges.map { edge ->
                EdgeEntity(
                    fromNodeId = edge.fromNodeId,
                    toNodeId = edge.toNodeId,
                    venueId = edge.venueId,
                    distanceM = edge.distanceM,
                    directionDegrees = edge.directionDegrees,
                    directionLabel = edge.directionLabel,
                    instruction = edge.instruction,
                    hasStairs = edge.hasStairs,
                    estimatedSeconds = edge.estimatedSeconds
                )
            }

            nodeDao.insertAllNodes(nodeEntities)
            edgeDao.insertAllEdges(edgeEntities)
        } catch (e: Exception) {
            // Demo data not available — graceful degradation
            android.util.Log.w("HomeViewModel", "Demo venue data not loaded: ${e.message}")
        }
    }
}

data class DemoVenueData(
    val nodes: List<DemoNode>,
    val edges: List<DemoEdge>
)

data class DemoNode(
    val id: String,
    val name: String,
    val floor: Int,
    val venueId: String,
    val accessible: Boolean,
    val type: String,
    val relativeX: Float,
    val relativeY: Float
)

data class DemoEdge(
    val fromNodeId: String,
    val toNodeId: String,
    val venueId: String,
    val distanceM: Float,
    val directionDegrees: Float,
    val directionLabel: String,
    val instruction: String,
    val hasStairs: Boolean,
    val estimatedSeconds: Int
)
