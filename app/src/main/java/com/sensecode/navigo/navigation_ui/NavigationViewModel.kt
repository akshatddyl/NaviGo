package com.sensecode.navigo.navigation_ui

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensecode.navigo.R
import com.sensecode.navigo.audio.SpeechInputManager
import com.sensecode.navigo.audio.TtsManager
import com.sensecode.navigo.data.repository.GraphRAGRepository
import com.sensecode.navigo.data.repository.NavigationRepository
import com.sensecode.navigo.domain.model.Edge
import com.sensecode.navigo.domain.model.LocationNode
import com.sensecode.navigo.domain.model.NavigationState
import com.sensecode.navigo.domain.model.Route
import com.sensecode.navigo.engine.DirectionStep
import com.sensecode.navigo.engine.NavigationEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.speech.SpeechRecognizer
import com.sensecode.navigo.util.HindiNlpHelper
import java.util.Locale
import javax.inject.Inject

enum class ConversationStep {
    ASKING_FLOOR,
    ASKING_START,
    ASKING_DESTINATION,
    CONFIRMED,
    NAVIGATING
}

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val navigationEngine: NavigationEngine,
    private val navigationRepository: NavigationRepository,
    private val graphRAGRepository: GraphRAGRepository,
    private val ttsManager: TtsManager,
    private val speechInputManager: SpeechInputManager,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    /**
     * Create a locale-aware context that respects the user's language preference.
     * @ApplicationContext always uses the default locale, but we need Hindi strings
     * when the user has selected Hindi. This creates a context with the correct locale.
     */
    private fun localizedContext(): Context {
        val prefs = appContext.getSharedPreferences("navigo_prefs", Context.MODE_PRIVATE)
        val langCode = prefs.getString("app_language", "en") ?: "en"
        val locale = if (langCode == "hi") Locale("hi", "IN") else Locale("en", "US")
        val config = Configuration(appContext.resources.configuration)
        config.setLocale(locale)
        return appContext.createConfigurationContext(config)
    }

    /** Shortcut: get a string resource using the correct locale */
    private fun str(resId: Int): String = localizedContext().getString(resId)
    private fun str(resId: Int, vararg args: Any): String = localizedContext().getString(resId, *args)

    private val _conversationStep = MutableStateFlow(
        savedStateHandle.get<String>("conversation_step")?.let {
            try { ConversationStep.valueOf(it) } catch (_: Exception) { ConversationStep.ASKING_FLOOR }
        } ?: ConversationStep.ASKING_FLOOR
    )
    val conversationStep: StateFlow<ConversationStep> = _conversationStep.asStateFlow()

    private val _currentFloor = MutableStateFlow<Int?>(savedStateHandle.get<Int>("current_floor"))
    val currentFloor: StateFlow<Int?> = _currentFloor.asStateFlow()

    private val _startNode = MutableStateFlow<LocationNode?>(null)
    val startNode: StateFlow<LocationNode?> = _startNode.asStateFlow()

    private val _destinationNode = MutableStateFlow<LocationNode?>(null)
    val destinationNode: StateFlow<LocationNode?> = _destinationNode.asStateFlow()

    private val _selectedVenueId = MutableStateFlow<String?>(savedStateHandle.get<String>("venue_id"))
    val selectedVenueId: StateFlow<String?> = _selectedVenueId.asStateFlow()

    private val _route = MutableStateFlow<Route?>(null)
    val route: StateFlow<Route?> = _route.asStateFlow()

    val navigationState: StateFlow<NavigationState> = navigationEngine.navigationState
    val routeProgress: StateFlow<Float> = navigationEngine.routeProgress

    private val _allVenueNodes = MutableStateFlow<List<LocationNode>>(emptyList())
    val allVenueNodes: StateFlow<List<LocationNode>> = _allVenueNodes.asStateFlow()

    private val _allVenueEdges = MutableStateFlow<List<Edge>>(emptyList())
    val allVenueEdges: StateFlow<List<Edge>> = _allVenueEdges.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val currentNodeId: StateFlow<String?> = navigationEngine.currentNodeId
    val deviationDetected: StateFlow<Boolean> = navigationEngine.deviationDetected
    val currentInstruction: StateFlow<String> = navigationEngine.currentInstruction
    val directionSteps: StateFlow<List<DirectionStep>> = navigationEngine.directionSteps
    val currentStepIndex: StateFlow<Int> = navigationEngine.currentStepIndex
    val partialSpeechResult: StateFlow<String> = speechInputManager.partialResult
    val isListeningActive: StateFlow<Boolean> = speechInputManager.isListening

    /**
     * Use HindiNlpHelper to extract English keywords from Hindi speech.
     * Handles conversational phrases like "मुझे खाने की जगह बताओ" → "canteen"
     */
    private fun translateHindiToEnglish(spokenText: String): String {
        val nlpResult = HindiNlpHelper.process(spokenText)
        if (nlpResult.englishKeywords.isNotEmpty()) {
            val numberSuffix = nlpResult.extractedNumbers.firstOrNull()?.toString() ?: ""
            val keyword = nlpResult.englishKeywords.first()
            return if (numberSuffix.isNotEmpty()) "$keyword $numberSuffix" else keyword
        }
        return spokenText
    }

    fun startConversation(venueId: String) {
        _selectedVenueId.value = venueId
        savedStateHandle["venue_id"] = venueId
        _conversationStep.value = ConversationStep.ASKING_FLOOR
        savedStateHandle["conversation_step"] = ConversationStep.ASKING_FLOOR.name

        viewModelScope.launch {
            _allVenueNodes.value = navigationRepository.getVenueNodes(venueId)
            _allVenueEdges.value = navigationRepository.getVenueEdges(venueId)

            delay(500)
            speakThenListen(str(R.string.tts_which_floor))
        }
    }

    fun handleFloorResponse(spokenText: String) {
        _spokenText.value = spokenText
        val floor = parseFloorNumber(spokenText)
        _currentFloor.value = floor
        savedStateHandle["current_floor"] = floor

        viewModelScope.launch {
            ttsManager.speak(str(R.string.tts_heard_floor, floor))
            _conversationStep.value = ConversationStep.ASKING_START
            savedStateHandle["conversation_step"] = ConversationStep.ASKING_START.name
            delay(500)
            startListening()
        }
    }

    fun handleStartResponse(spokenText: String) {
        _spokenText.value = spokenText
        val venueId = _selectedVenueId.value ?: return
        val floor = _currentFloor.value ?: 0

        viewModelScope.launch {
            _isLoading.value = true
            // Translate Hindi to English, then try both translated and original
            val translated = translateHindiToEnglish(spokenText)
            val node = navigationRepository.findBestMatchingNode(venueId, floor, translated)
                ?: navigationRepository.findBestMatchingNode(venueId, floor, spokenText)

            if (node != null) {
                _startNode.value = node
                _isLoading.value = false
                ttsManager.speak(str(R.string.tts_found_start, node.name))
                _conversationStep.value = ConversationStep.ASKING_DESTINATION
                savedStateHandle["conversation_step"] = ConversationStep.ASKING_DESTINATION.name
                delay(500)
                startListening()
            } else {
                _isLoading.value = false
                ttsManager.speak(str(R.string.tts_not_found))
                startListening()
            }
        }
    }

    fun handleDestinationResponse(spokenText: String) {
        _spokenText.value = spokenText
        val venueId = _selectedVenueId.value ?: return
        val floor = _currentFloor.value ?: 0
        val start = _startNode.value ?: return

        viewModelScope.launch {
            _isLoading.value = true

            val translatedQuery = translateHindiToEnglish(spokenText)

            val nodeResult = graphRAGRepository.resolveNaturalLanguageQuery(
                userQuery = translatedQuery,
                venueId = venueId,
                currentFloor = floor
            )

            nodeResult.fold(
                onSuccess = { destNode ->
                    _destinationNode.value = destNode
                    computeAndStartRoute(start, destNode, venueId, spokenText)
                },
                onFailure = {
                    // Retry with original spoken text
                    if (translatedQuery != spokenText) {
                        val retryResult = graphRAGRepository.resolveNaturalLanguageQuery(
                            userQuery = spokenText,
                            venueId = venueId,
                            currentFloor = floor
                        )
                        retryResult.fold(
                            onSuccess = { destNode ->
                                _destinationNode.value = destNode
                                computeAndStartRoute(start, destNode, venueId, spokenText)
                            },
                            onFailure = { e ->
                                _isLoading.value = false
                                _errorMessage.value = e.message
                                ttsManager.speak(str(R.string.tts_not_found_dest))
                                startListening()
                            }
                        )
                    } else {
                        _isLoading.value = false
                        _errorMessage.value = it.message
                        ttsManager.speak(str(R.string.tts_not_found_dest))
                        startListening()
                    }
                }
            )
        }
    }

    private suspend fun computeAndStartRoute(
        start: LocationNode,
        destNode: LocationNode,
        venueId: String,
        originalSpokenText: String
    ) {
        val routeResult = navigationRepository.getRoute(
            startNodeId = start.id,
            destinationNodeId = destNode.id,
            venueId = venueId,
            accessibleOnly = originalSpokenText.contains("wheelchair", ignoreCase = true) ||
                    originalSpokenText.contains("accessible", ignoreCase = true) ||
                    originalSpokenText.contains("व्हीलचेयर", ignoreCase = true) ||
                    originalSpokenText.contains("सुलभ", ignoreCase = true)
        )

        routeResult.fold(
            onSuccess = { route ->
                _route.value = route
                _isLoading.value = false
                _conversationStep.value = ConversationStep.CONFIRMED
                savedStateHandle["conversation_step"] = ConversationStep.CONFIRMED.name

                ttsManager.speak(str(R.string.tts_route_found, destNode.name, route.estimatedMinutes))

                delay(2000)
                beginNavigation()
            },
            onFailure = { e ->
                _isLoading.value = false
                _errorMessage.value = e.message
                ttsManager.speak(str(R.string.tts_no_route, e.message ?: ""))
                startListening()
            }
        )
    }

    fun beginNavigation() {
        val route = _route.value ?: return
        _conversationStep.value = ConversationStep.NAVIGATING
        savedStateHandle["conversation_step"] = ConversationStep.NAVIGATING.name

        viewModelScope.launch {
            navigationEngine.startNavigation(route)
        }
    }

    fun stopNavigation() {
        navigationEngine.stopNavigation()
        _conversationStep.value = ConversationStep.ASKING_FLOOR
        savedStateHandle["conversation_step"] = ConversationStep.ASKING_FLOOR.name
    }

    /** Delegate floor parsing to HindiNlpHelper — handles Hindi + English */
    private fun parseFloorNumber(text: String): Int {
        return HindiNlpHelper.parseFloorNumber(text)
    }

    private fun speakThenListen(text: String) {
        viewModelScope.launch {
            ttsManager.speak(text)
            delay(1500)
            startListening()
        }
    }

    fun startListening() {
        ttsManager.stop()
        speechInputManager.clearPartialResult()
        _errorMessage.value = null
        speechInputManager.startListening(
            onResult = { result ->
                _spokenText.value = result
                when (_conversationStep.value) {
                    ConversationStep.ASKING_FLOOR -> handleFloorResponse(result)
                    ConversationStep.ASKING_START -> handleStartResponse(result)
                    ConversationStep.ASKING_DESTINATION -> handleDestinationResponse(result)
                    else -> {}
                }
            },
            onError = { errorCode ->
                val errorMsg = when (errorCode) {
                    SpeechRecognizer.ERROR_NO_MATCH -> str(R.string.error_no_match)
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> str(R.string.error_timeout)
                    SpeechRecognizer.ERROR_AUDIO -> str(R.string.error_audio)
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> str(R.string.error_permission)
                    SpeechRecognizer.ERROR_NETWORK,
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> str(R.string.error_network)
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> str(R.string.error_busy)
                    else -> str(R.string.error_generic)
                }
                _errorMessage.value = errorMsg
            }
        )
    }

    fun stopListening() {
        speechInputManager.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        navigationEngine.stopNavigation()
        speechInputManager.stopListening()
    }
}
