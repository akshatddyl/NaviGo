package com.sensecode.navigo.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("navigo_prefs", Context.MODE_PRIVATE)

    private val _hasCompletedOnboarding = MutableStateFlow(prefs.getBoolean(KEY_ONBOARDING_DONE, false))
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    private val _userRole = MutableStateFlow(prefs.getString(KEY_USER_ROLE, null))
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    fun selectRole(role: String) {
        viewModelScope.launch {
            prefs.edit()
                .putString(KEY_USER_ROLE, role)
                .putBoolean(KEY_ONBOARDING_DONE, true)
                .apply()
            _userRole.value = role
            _hasCompletedOnboarding.value = true
        }
    }

    fun resetOnboarding() {
        prefs.edit()
            .remove(KEY_USER_ROLE)
            .putBoolean(KEY_ONBOARDING_DONE, false)
            .apply()
        _userRole.value = null
        _hasCompletedOnboarding.value = false
    }

    companion object {
        private const val KEY_ONBOARDING_DONE = "onboarding_completed"
        private const val KEY_USER_ROLE = "user_role"
    }
}
