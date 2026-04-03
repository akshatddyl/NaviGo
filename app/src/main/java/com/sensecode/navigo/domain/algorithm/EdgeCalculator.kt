package com.sensecode.navigo.domain.algorithm

import kotlin.math.abs
import kotlin.math.roundToInt

object EdgeCalculator {

    fun stepsToDist(steps: Int, strideLengthM: Float = 0.7f): Float {
        return steps * strideLengthM
    }

    fun headingToDirectionLabel(degrees: Float): String {
        val normalized = normalizeHeading(degrees)
        return when {
            normalized < 22.5f || normalized >= 337.5f -> "north"
            normalized < 67.5f -> "north-east"
            normalized < 112.5f -> "east"
            normalized < 157.5f -> "south-east"
            normalized < 202.5f -> "south"
            normalized < 247.5f -> "south-west"
            normalized < 292.5f -> "west"
            else -> "north-west"
        }
    }

    fun headingDeltaToTurnInstruction(prevHeading: Float, currentHeading: Float): String {
        val delta = headingDifference(prevHeading, currentHeading)
        return when {
            delta > 60f -> "Turn right"
            delta > 30f -> "Bear right"
            delta < -60f -> "Turn left"
            delta < -30f -> "Bear left"
            else -> "Walk straight"
        }
    }

    fun distanceToEstimatedSeconds(distanceM: Float, walkingSpeedMs: Float = 0.9f): Int {
        return (distanceM / walkingSpeedMs).roundToInt()
    }

    fun normalizeHeading(degrees: Float): Float {
        var d = degrees % 360f
        if (d < 0f) d += 360f
        return d
    }

    fun headingDifference(a: Float, b: Float): Float {
        val na = normalizeHeading(a)
        val nb = normalizeHeading(b)
        var diff = nb - na
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        return diff
    }

    fun mirrorDirection(direction: String): String {
        return when (direction.lowercase()) {
            "north" -> "south"
            "south" -> "north"
            "east" -> "west"
            "west" -> "east"
            "north-east" -> "south-west"
            "south-west" -> "north-east"
            "north-west" -> "south-east"
            "south-east" -> "north-west"
            else -> direction
        }
    }

    fun mirrorInstruction(instruction: String): String {
        return when (instruction) {
            "Turn right" -> "Turn left"
            "Turn left" -> "Turn right"
            "Bear right" -> "Bear left"
            "Bear left" -> "Bear right"
            else -> instruction
        }
    }

    fun mirrorHeading(degrees: Float): Float {
        return normalizeHeading(degrees + 180f)
    }
}
