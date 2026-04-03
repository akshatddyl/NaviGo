package com.sensecode.navigo.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechInputManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    /** Live partial transcript while the user is speaking */
    private val _partialResult = MutableStateFlow("")
    val partialResult: StateFlow<String> = _partialResult.asStateFlow()

    /**
     * Current speech recognition locale.
     * "en-US" for English, "hi-IN" for Hindi.
     * Changed via [setLanguage].
     */
    private var currentLocale: String = "en-US"

    private var retryCount = 0
    private var currentOnResult: ((String) -> Unit)? = null
    private var currentOnError: ((Int) -> Unit)? = null

    /**
     * Switch the speech recognizer language.
     * Call this when the user toggles language preference.
     */
    fun setLanguage(locale: String) {
        currentLocale = locale
        Log.d(TAG, "Speech language set to: $locale")
    }

    fun getLanguage(): String = currentLocale

    fun startListening(onResult: (String) -> Unit, onError: (Int) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "Speech recognition not available on this device")
            onError(SpeechRecognizer.ERROR_CLIENT)
            return
        }

        currentOnResult = onResult
        currentOnError = onError
        retryCount = 0
        _partialResult.value = ""

        createAndStartRecognizer()
    }

    private fun createAndStartRecognizer() {
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
                Log.d(TAG, "Ready for speech (locale: $currentLocale)")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "Speech started")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _isListening.value = false
                Log.d(TAG, "Speech ended")
            }

            override fun onError(error: Int) {
                _isListening.value = false
                Log.e(TAG, "Speech recognition error: $error (${getErrorName(error)})")

                // Auto-retry on timeout or no-match (up to 2 retries)
                val retriableErrors = setOf(
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
                    SpeechRecognizer.ERROR_NO_MATCH
                )
                if (error in retriableErrors && retryCount < 2) {
                    retryCount++
                    Log.d(TAG, "Retrying speech recognition (attempt $retryCount)")
                    createAndStartRecognizer()
                    return
                }

                currentOnError?.invoke(error)
            }

            override fun onResults(results: Bundle?) {
                _isListening.value = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidences = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                Log.d(TAG, "Results: $matches, confidences: ${confidences?.toList()}")

                // Pick the best non-blank result
                val bestResult = if (matches != null && confidences != null && matches.size == confidences.size) {
                    // Use confidence scores to pick the best result
                    matches.zip(confidences.toList())
                        .filter { it.first.isNotBlank() }
                        .maxByOrNull { it.second }
                        ?.first
                } else {
                    // Fall back to first non-blank result
                    matches?.firstOrNull { it.isNotBlank() }
                }

                if (bestResult != null) {
                    _partialResult.value = bestResult
                    currentOnResult?.invoke(bestResult)
                } else {
                    // No good match — retry once
                    if (retryCount < 2) {
                        retryCount++
                        Log.d(TAG, "No match found, retrying (attempt $retryCount)")
                        createAndStartRecognizer()
                    } else {
                        currentOnError?.invoke(SpeechRecognizer.ERROR_NO_MATCH)
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (!partial.isNullOrBlank()) {
                    _partialResult.value = partial
                    Log.d(TAG, "Partial result: $partial")
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            // Use the current locale (en-US or hi-IN)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLocale)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, currentLocale)
            // Force ONLY this language — prevent system from auto-detecting another
            putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayListOf<String>())
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
            // Request multiple results for better accuracy
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            // Enable partial results for live transcription feedback
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Extend timeouts for accessibility (visually impaired users need more time)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
        }

        speechRecognizer?.startListening(intent)
        _isListening.value = true
    }

    fun stopListening() {
        _isListening.value = false
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping listener: ${e.message}")
        }
        // Delay destroy to allow pending results to arrive
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            try {
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (e: Exception) {
                Log.w(TAG, "Error destroying recognizer: ${e.message}")
            }
        }, 500)
    }

    fun clearPartialResult() {
        _partialResult.value = ""
    }

    private fun getErrorName(error: Int): String = when (error) {
        SpeechRecognizer.ERROR_AUDIO -> "ERROR_AUDIO"
        SpeechRecognizer.ERROR_CLIENT -> "ERROR_CLIENT"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "ERROR_INSUFFICIENT_PERMISSIONS"
        SpeechRecognizer.ERROR_NETWORK -> "ERROR_NETWORK"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "ERROR_NETWORK_TIMEOUT"
        SpeechRecognizer.ERROR_NO_MATCH -> "ERROR_NO_MATCH"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "ERROR_RECOGNIZER_BUSY"
        SpeechRecognizer.ERROR_SERVER -> "ERROR_SERVER"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "ERROR_SPEECH_TIMEOUT"
        else -> "UNKNOWN($error)"
    }

    companion object {
        private const val TAG = "SpeechInputManager"
    }
}
