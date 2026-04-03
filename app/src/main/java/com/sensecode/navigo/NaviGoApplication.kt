package com.sensecode.navigo

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class NaviGoApplication : Application(), TextToSpeech.OnInitListener {

    lateinit var textToSpeech: TextToSpeech
        private set

    var isTtsReady: Boolean = false
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Read saved language preference and set TTS locale accordingly
            val prefs = getSharedPreferences("navigo_prefs", Context.MODE_PRIVATE)
            val savedLang = prefs.getString("app_language", "en") ?: "en"

            val locale = if (savedLang == "hi") Locale("hi", "IN") else Locale.US
            val result = textToSpeech.setLanguage(locale)

            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED

            if (!isTtsReady) {
                // Fallback to English if preferred language not available
                Log.w(TAG, "TTS language $savedLang not supported, trying English")
                val fallbackResult = textToSpeech.setLanguage(Locale.US)
                isTtsReady = fallbackResult != TextToSpeech.LANG_MISSING_DATA &&
                        fallbackResult != TextToSpeech.LANG_NOT_SUPPORTED
            }

            if (!isTtsReady) {
                Log.e(TAG, "TTS initialization failed — no supported language")
            } else {
                Log.d(TAG, "TTS initialized with locale: ${textToSpeech.voice?.locale ?: locale}")
            }
        } else {
            Log.e(TAG, "TTS initialization failed with status: $status")
            isTtsReady = false
        }
    }

    override fun onTerminate() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onTerminate()
    }

    companion object {
        private const val TAG = "NaviGoApplication"
        lateinit var instance: NaviGoApplication
            private set
    }
}
