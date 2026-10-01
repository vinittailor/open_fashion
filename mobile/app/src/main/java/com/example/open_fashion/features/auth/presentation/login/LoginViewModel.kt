package com.example.open_fashion.features.auth.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.open_fashion.core.network.NetworkResult
import com.example.open_fashion.core.storage.TokenManager
import com.example.open_fashion.features.auth.data.repository.AuthRepositoryImpl
import com.example.open_fashion.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "OpenFashionLoginVM"

/**
 * MVI ViewModel managing the Customer Login screen state and business flows.
 */
class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val tokenManager: TokenManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
    }

    /**
     * Single intent processor for all user actions emitted from the Composable UI.
     */
    fun onIntent(intent: LoginUiIntent) {
        when (intent) {
            is LoginUiIntent.OnEmailChanged -> updateEmail(intent.email)
            is LoginUiIntent.OnPasswordChanged -> updatePassword(intent.password)
            is LoginUiIntent.OnTogglePasswordVisibility -> togglePasswordVisibility()
            is LoginUiIntent.OnSubmitLogin -> submitLogin()
            is LoginUiIntent.OnDismissError -> dismissError()
        }
    }

    private fun updateEmail(email: String) {
        val error = when {
            email.isBlank() -> null
            !EMAIL_REGEX.matches(email.trim()) -> "Please provide a valid email address"
            else -> null
        }
        _uiState.update { it.copy(email = email, emailError = error, generalError = null) }
    }

    private fun updatePassword(password: String) {
        val error = when {
            password.isEmpty() -> null
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        _uiState.update { it.copy(password = password, passwordError = error, generalError = null) }
    }

    private fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun dismissError() {
        _uiState.update { it.copy(generalError = null) }
    }

    private fun submitLogin() {
        val currentState = _uiState.value

        // Validate fields before network dispatch
        val emailErr = if (!EMAIL_REGEX.matches(currentState.email.trim())) "Valid email is required" else null
        val passErr = if (currentState.password.isEmpty()) "Password cannot be empty" else null

        if (emailErr != null || passErr != null) {
            _uiState.update {
                it.copy(emailError = emailErr, passwordError = passErr)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            Log.d(TAG, "Attempting login for email: ${currentState.email.trim().lowercase()}")

            val result = authRepository.login(
                email = currentState.email.trim().lowercase(),
                password = currentState.password
            )

            when (result) {
                is NetworkResult.Success -> {
                    val session = result.data
                    Log.i(TAG, "Login successful for user: ${session?.user?.email} (Role: ${session?.user?.role})")
                    if (session != null) {
                        tokenManager?.saveSession(session)
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            authSession = session,
                            generalError = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Log.e(TAG, "Login failed: ${result.message} [Code: ${result.code}]")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
