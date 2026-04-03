package com.sensecode.navigo.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensecode.navigo.data.repository.SetupRepository
import com.sensecode.navigo.data.repository.VenueRepository
import com.sensecode.navigo.data.repository.toDomain
import com.sensecode.navigo.domain.model.Edge
import com.sensecode.navigo.domain.model.LocationNode
import com.sensecode.navigo.domain.model.Venue
import com.sensecode.navigo.sensors.CompassManager
import com.sensecode.navigo.sensors.StepCounterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SetupUiState {
    object Idle : SetupUiState()
    object Recording : SetupUiState()
    object Review : SetupUiState()
    object Saving : SetupUiState()
    object Publishing : SetupUiState()
    data class Success(val venueId: String) : SetupUiState()
    data class Error(val message: String) : SetupUiState()
}

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val setupRepository: SetupRepository,
    private val venueRepository: VenueRepository,
    private val stepCounterManager: StepCounterManager,
    private val compassManager: CompassManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SetupUiState>(SetupUiState.Idle)
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    private val _recordedNodes = MutableStateFlow<List<RecordedNode>>(emptyList())
    val recordedNodes: StateFlow<List<RecordedNode>> = _recordedNodes.asStateFlow()

    val currentStepCount: StateFlow<Int> = stepCounterManager.stepFlow

    val currentHeading: StateFlow<Float> = compassManager.headingFlow

    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private var savedVenueId: String? = null

    private var session: SetupRecordingSession? = null

    fun startRecording() {
        session = SetupRecordingSession(stepCounterManager, compassManager)
        session?.startSession()
        _isSessionActive.value = true
        _uiState.value = SetupUiState.Recording
        _recordedNodes.value = emptyList()
    }

    fun dropNode(
        name: String,
        type: String,
        accessible: Boolean,
        hasStairs: Boolean = false,
        hasElevator: Boolean = false
    ) {
        val node = session?.dropNode(name, type, accessible, hasStairs, hasElevator) ?: return
        _recordedNodes.value = session?.getRecordedNodes() ?: emptyList()
    }

    fun stopRecording() {
        session?.let {
            _recordedNodes.value = it.getRecordedNodes()
        }
        _isSessionActive.value = false
        _uiState.value = SetupUiState.Review
    }

    fun saveSession(venueName: String, venueAddress: String, orgName: String, floor: Int) {
        val currentSession = session ?: return
        _uiState.value = SetupUiState.Saving

        viewModelScope.launch {
            val result = setupRepository.saveRecordingSession(
                session = currentSession,
                venueName = venueName,
                venueAddress = venueAddress,
                orgName = orgName,
                floor = floor
            )

            result.fold(
                onSuccess = { venueId ->
                    savedVenueId = venueId
                    _uiState.value = SetupUiState.Success(venueId)
                },
                onFailure = { e ->
                    _uiState.value = SetupUiState.Error(e.message ?: "Save failed")
                }
            )
        }
    }

    fun publishToMapShare(
        venue: Venue,
        nodes: List<LocationNode>,
        edges: List<Edge>
    ) {
        _uiState.value = SetupUiState.Publishing

        viewModelScope.launch {
            val result = venueRepository.uploadVenueToMapShare(venue, nodes, edges)
            result.fold(
                onSuccess = {
                    _uiState.value = SetupUiState.Success(venue.venueId)
                },
                onFailure = { e ->
                    _uiState.value = SetupUiState.Error(e.message ?: "Upload failed")
                }
            )
        }
    }

    fun getSavedVenueId(): String? = savedVenueId

    fun resetState() {
        _uiState.value = SetupUiState.Idle
    }
}
