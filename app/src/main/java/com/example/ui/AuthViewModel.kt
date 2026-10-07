package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data object Unauthenticated : AuthUiState
    data class Authenticated(val user: FirebaseUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUserFlow: StateFlow<FirebaseUser?> = authRepository.authStateFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, authRepository.currentUser)

    init {
        viewModelScope.launch {
            authRepository.authStateFlow().collect { user ->
                if (user != null) {
                    _uiState.value = AuthUiState.Authenticated(user)
                } else {
                    _uiState.value = AuthUiState.Unauthenticated
                }
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(context)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Authenticated(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(
                    error.localizedMessage ?: "حدث خطأ أثناء تسجيل الدخول، يرجى المحاولة مرة أخرى."
                )
            }
        }
    }

    fun trySilentSignIn(context: Context) {
        viewModelScope.launch {
            val user = authRepository.trySilentSignIn(context)
            if (user != null) {
                _uiState.value = AuthUiState.Authenticated(user)
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState.Unauthenticated
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Unauthenticated
        }
    }
}
