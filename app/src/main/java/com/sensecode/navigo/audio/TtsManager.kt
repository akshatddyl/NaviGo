package com.sensecode.navigo.audio

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.sensecode.navigo.NaviGoApplication
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class TtsManager @Inject constructor() {

    private val tts: TextToSpeech
        get() = NaviGoApplication.instance.textToSpeech

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private var currentDeferred: CompletableDeferred<Unit>? = null

    init {
        checkReady()
    }

    private fun checkReady() {
        _isReady.value = NaviGoApplication.instance.isTtsReady
    }

    /**
     * Switch TTS language. Supports "en" for English and "hi" for Hindi.
     */
    fun setLanguage(langCode: String) {
        checkReady()
        if (!_isReady.value) return

        val locale = when (langCode) {
            "hi" -> Locale("hi", "IN")
            else -> Locale.US
        }
        val result = tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(TAG, "TTS language $langCode not supported, falling back to English")
            tts.setLanguage(Locale.US)
        } else {
            Log.d(TAG, "TTS language set to: $langCode")
        }
    }

    suspend fun speak(text: String, flushQueue: Boolean = true) {
        checkReady()
        if (!_isReady.value) {
            // Wait a moment for TTS to initialize
            kotlinx.coroutines.delay(500)
            checkReady()
            if (!_isReady.value) return
        }

        suspendCoroutine<Unit> { continuation ->
            val utteranceId = UUID.randomUUID().toString()

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    continuation.resume(Unit)
                }

                @Deprecated("Deprecated in API")
                override fun onError(utteranceId: String?) {
                    continuation.resume(Unit)
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    continuation.resume(Unit)
                }
            })

            val queueMode = if (flushQueue) {
                TextToSpeech.QUEUE_FLUSH
            } else {
                TextToSpeech.QUEUE_ADD
            }

            tts.speak(text, queueMode, null, utteranceId)
        }
    }

    fun speakAsync(text: String, flushQueue: Boolean = true) {
        checkReady()
        if (!_isReady.value) return

        val queueMode = if (flushQueue) {
            TextToSpeech.QUEUE_FLUSH
        } else {
            TextToSpeech.QUEUE_ADD
        }

        tts.speak(text, queueMode, null, UUID.randomUUID().toString())
    }

    fun stop() {
        tts.stop()
    }

    companion object {
        private const val TAG = "TtsManager"
    }
}
