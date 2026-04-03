package com.sensecode.navigo.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

/**
 * StepCounterManager — Layered step detection with three fallback levels:
 *
 * 1. TYPE_STEP_DETECTOR (best) — Hardware sensor hub fires one event per real footstep.
 *    All filtering is done on-chip. No manual math needed.
 *
 * 2. TYPE_STEP_COUNTER (good) — Cumulative step count since boot.
 *    We track a baseline and compute session-relative steps.
 *
 * 3. Accelerometer peak detection (fallback) — Simple but effective algorithm
 *    for devices without dedicated step sensors. Detects acceleration peaks
 *    that match human walking patterns.
 *
 * 4. Simulated steps (emulator only) — For testing on emulators.
 */
@Singleton
class StepCounterManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val _stepFlow = MutableStateFlow(0)
    val stepFlow: StateFlow<Int> = _stepFlow.asStateFlow()

    private val _isSensorAvailable = MutableStateFlow(true)
    
    val stepsSinceLastReset: Int get() = _stepFlow.value

    private var isRunning = false

    // ── Emulator detection ──
    private val isEmulator = Build.FINGERPRINT.contains("generic") ||
            Build.FINGERPRINT.contains("emulator") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK")

    private var simulationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // ── TYPE_STEP_COUNTER baseline ──
    private var stepCounterBaseline: Float = -1f

    // ── Advanced Accelerometer fallback state ──
    private var useAccelerometerFallback = true
    private val gravity = FloatArray(3)
    private val linear_acceleration = FloatArray(3)
    private val ALPHA = 0.8f // Filter constant

    // Dynamic Thresholding
    private var lastPeakValue = 0f
    private var lastTroughValue = 0f
    private var isFindingPeak = true
    private var lastStepMilli: Long = 0

    // Constants for signal processing
    private val MIN_TIME_BETWEEN_STEPS_MS = 250L
    private val MAX_TIME_BETWEEN_STEPS_MS = 2000L
    private val MIN_AMPLITUDE_WALKING = 1.8f
    private val WINDOW_SIZE = 10
    private var historyWindow = FloatArray(WINDOW_SIZE)
    private var windowIndex = 0

    fun startCounting() {
        if (isRunning) return
        isRunning = true
        _stepFlow.value = 0
        useAccelerometerFallback = true
        stepCounterBaseline = -1f

        if (isEmulator) {
            Log.d("StepCounterManager", "Running on emulator — using simulated steps")
            startSimulatedSteps()
            return
        }

        // Try to register Step Counter
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
            Log.d("StepCounterManager", "✓ TYPE_STEP_COUNTER registered")
        } else {
            Log.e("StepCounterManager", "No Step Counter Sensor found! Trying fallback...")
        }

        // Always register Accelerometer as fallback/concurrent
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelSensor != null) {
            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME)
            Log.d("StepCounterManager", "✓ TYPE_ACCELEROMETER registered")
        }

        if (stepSensor == null && accelSensor == null) {
            Log.e("StepCounterManager", "No motion sensor available on this device!")
            _isSensorAvailable.value = false
        } else {
            _isSensorAvailable.value = true
        }
    }

    fun stopCounting() {
        if (!isRunning) return
        isRunning = false
        sensorManager.unregisterListener(this)
        simulationJob?.cancel()
        simulationJob = null
        Log.d("StepCounterManager", "Step counting stopped, listener unregistered")
    }

    fun resetCount() {
        _stepFlow.value = 0
        stepCounterBaseline = -1f
        lastStepMilli = 0L
    }

    private fun startSimulatedSteps() {
        simulationJob = scope.launch {
            while (isActive && isRunning) {
                delay(800L)
                _stepFlow.value = _stepFlow.value + 1
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isRunning || event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val totalStepsSinceBoot = event.values[0]
                if (stepCounterBaseline < 0) {
                    stepCounterBaseline = totalStepsSinceBoot
                }
                val hardwareSteps = (totalStepsSinceBoot - stepCounterBaseline).toInt()
                if (hardwareSteps > _stepFlow.value) {
                    _stepFlow.value = hardwareSteps
                    useAccelerometerFallback = false // If hardware works, shut off fallback
                }
            }

            Sensor.TYPE_ACCELEROMETER -> {
                if (useAccelerometerFallback) {
                    handleAccelerometerStep(event)
                }
            }
        }
    }

    /**
     * Dynamic Step Detection Algorithm (Peak-Valley Detection)
     * using Low-pass filter to isolate gravity.
     */
    private fun handleAccelerometerStep(event: SensorEvent) {
        // Apply low-pass filter to isolate gravity
        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0]
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1]
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2]

        // Calculate linear acceleration (remove gravity)
        linear_acceleration[0] = event.values[0] - gravity[0]
        linear_acceleration[1] = event.values[1] - gravity[1]
        linear_acceleration[2] = event.values[2] - gravity[2]

        // Get magnitude of the linear acceleration (how hard user is moving minus Earth's pull)
        val magnitude = sqrt(
            (linear_acceleration[0] * linear_acceleration[0] +
             linear_acceleration[1] * linear_acceleration[1] +
             linear_acceleration[2] * linear_acceleration[2]).toDouble()
        ).toFloat()

        // Keep a running window of recent magnitudes to calculate dynamic threshold
        historyWindow[windowIndex] = magnitude
        windowIndex = (windowIndex + 1) % WINDOW_SIZE
        val avgMagnitude = historyWindow.average().toFloat()

        val currentTime = System.currentTimeMillis()

        if (isFindingPeak && magnitude > avgMagnitude && magnitude > lastPeakValue) {
            lastPeakValue = magnitude
        } else if (!isFindingPeak && magnitude < avgMagnitude && magnitude < lastTroughValue) {
            lastTroughValue = magnitude
        }

        // Cross over average down: implies a peak was just formed
        if (isFindingPeak && magnitude < avgMagnitude) {
            isFindingPeak = false // Start looking for trough
            val amplitude = lastPeakValue - lastTroughValue

            // If amplitude is big enough to be a step, and time logic matches walking cadence
            if (amplitude > MIN_AMPLITUDE_WALKING) {
                val timeSinceLastStep = currentTime - lastStepMilli
                if (timeSinceLastStep in MIN_TIME_BETWEEN_STEPS_MS..MAX_TIME_BETWEEN_STEPS_MS) {
                    _stepFlow.value += 1
                    lastStepMilli = currentTime
                    Log.d("StepCounterManager", "Valid Physical Step via Accelerometer! Total=${_stepFlow.value}")
                } else if (timeSinceLastStep > MAX_TIME_BETWEEN_STEPS_MS) {
                    // User started walking again, count as first step of new cadence
                    lastStepMilli = currentTime
                }
            }
            lastPeakValue = 0f // Reset peak
        }

        // Cross over average up: implies a trough was just formed
        if (!isFindingPeak && magnitude > avgMagnitude) {
            isFindingPeak = true
            lastTroughValue = magnitude // Reset trough tracker to current
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed
    }
}
