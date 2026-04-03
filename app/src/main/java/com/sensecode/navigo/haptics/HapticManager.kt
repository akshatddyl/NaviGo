package com.sensecode.navigo.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /** One short pulse: 100ms — right turn */
    fun vibrateRight() {
        vibrate(longArrayOf(0, 100), intArrayOf(0, 255))
    }

    /** Two short pulses: 100ms, gap 100ms, 100ms — left turn */
    fun vibrateLeft() {
        vibrate(longArrayOf(0, 100, 100, 100), intArrayOf(0, 255, 0, 255))
    }

    /** Long continuous: 800ms — arrival */
    fun vibrateArrived() {
        vibrate(longArrayOf(0, 800), intArrayOf(0, 200))
    }

    /** Rapid burst: 3× short, 50ms gaps — deviation detected */
    fun vibrateDeviation() {
        vibrate(longArrayOf(0, 80, 50, 80, 50, 80), intArrayOf(0, 255, 0, 255, 0, 255))
    }

    /** Medium pulse: 300ms — warning */
    fun vibrateWarning() {
        vibrate(longArrayOf(0, 300), intArrayOf(0, 200))
    }

    private fun vibrate(timings: LongArray, amplitudes: IntArray) {
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(timings, -1)
        }
    }
}
