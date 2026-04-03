package com.sensecode.navigo.domain.model

sealed class NavigationState {
    object Idle : NavigationState()
    data class Navigating(val currentNodeIndex: Int, val stepsOnCurrentEdge: Int) : NavigationState()
    data class VerifyingTurn(val expectedHeading: Float, val stepsSinceInstruction: Int) : NavigationState()
    data class SoftWarning(val message: String) : NavigationState()
    data class Recalculating(val estimatedPosition: LocationNode) : NavigationState()
    data class Arrived(val destination: LocationNode) : NavigationState()
    data class Error(val message: String) : NavigationState()
}
