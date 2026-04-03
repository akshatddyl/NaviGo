package com.sensecode.navigo.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensecode.navigo.data.remote.firebase.FirebaseAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object LoggedIn : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: FirebaseAuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(
        if (authService.isLoggedIn()) AuthUiState.LoggedIn else AuthUiState.Idle
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _userEmail = MutableStateFlow(authService.getCurrentUser()?.email ?: "")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authService.signInWithEmail(email, password)
            result.fold(
                onSuccess = {
                    _userEmail.value = it.email ?: ""
                    _uiState.value = AuthUiState.LoggedIn
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Sign-in failed")
                }
            )
        }
    }

    fun createAccount(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authService.createAccount(email, password)
            result.fold(
                onSuccess = {
                    _userEmail.value = it.email ?: ""
                    _uiState.value = AuthUiState.LoggedIn
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Account creation failed")
                }
            )
        }
    }

    fun signOut() {
        authService.signOut()
        _userEmail.value = ""
        _uiState.value = AuthUiState.Idle
    }
}
