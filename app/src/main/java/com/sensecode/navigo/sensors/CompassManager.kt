package com.sensecode.navigo.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompassManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationVectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val _headingFlow = MutableStateFlow(0f)
    val headingFlow: StateFlow<Float> = _headingFlow.asStateFlow()

    val currentHeading: Float get() = _headingFlow.value

    private var isTracking = false
    private val alpha = 0.25f // Low-pass filter coefficient (higher = more responsive)
    private var filteredHeading = 0f
    private var isFirstReading = true

    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    fun startTracking() {
        if (isTracking) return
        isTracking = true
        isFirstReading = true

        if (rotationVectorSensor != null) {
            sensorManager.registerListener(
                this,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        } else {
            Log.w(TAG, "Rotation vector sensor not available on this device")
        }
    }

    fun stopTracking() {
        isTracking = false
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ROTATION_VECTOR) return

        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
        SensorManager.getOrientation(rotationMatrix, orientation)

        // Convert azimuth from radians to degrees
        var rawHeading = Math.toDegrees(orientation[0].toDouble()).toFloat()
        if (rawHeading < 0) rawHeading += 360f

        // Apply low-pass filter for smoothing
        if (isFirstReading) {
            filteredHeading = rawHeading
            isFirstReading = false
        } else {
            // Handle the 0/360 wraparound for smoothing
            var diff = rawHeading - filteredHeading
            if (diff > 180) diff -= 360
            if (diff < -180) diff += 360
            filteredHeading += alpha * diff
            if (filteredHeading < 0) filteredHeading += 360f
            if (filteredHeading >= 360f) filteredHeading -= 360f
        }

        _headingFlow.value = filteredHeading
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed
    }

    companion object {
        private const val TAG = "CompassManager"
    }
}
