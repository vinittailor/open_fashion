package com.example.open_fashion.features.auth.presentation.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.open_fashion.core.network.NetworkResult
import com.example.open_fashion.features.auth.data.repository.AuthRepositoryImpl
import com.example.open_fashion.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "OpenFashionRegisterVM"

/**
 * MVI ViewModel managing the Customer Registration screen state and business flows.
 */
class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        private val PASSWORD_REGEX = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=\\[\\]{}|;:,.<>]).+\$")
    }

    /**
     * Single intent processor for all user actions emitted from the Composable UI.
     */
    fun onIntent(intent: RegisterUiIntent) {
        when (intent) {
            is RegisterUiIntent.OnNameChanged -> updateName(intent.name)
            is RegisterUiIntent.OnEmailChanged -> updateEmail(intent.email)
            is RegisterUiIntent.OnPasswordChanged -> updatePassword(intent.password)
            is RegisterUiIntent.OnPhoneChanged -> updatePhone(intent.phone)
            is RegisterUiIntent.OnTogglePasswordVisibility -> togglePasswordVisibility()
            is RegisterUiIntent.OnSubmitRegister -> submitRegistration()
            is RegisterUiIntent.OnDismissError -> dismissError()
        }
    }

    private fun updateName(name: String) {
        val error = when {
            name.isBlank() -> null // Don't show error while user is first typing
            name.trim().length < 2 -> "Full name must be at least 2 characters"
            name.length > 100 -> "Full name cannot exceed 100 characters"
            else -> null
        }
        _uiState.update { it.copy(name = name, nameError = error, generalError = null) }
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
            password.length < 8 -> "Password must be at least 8 characters long"
            password.length > 72 -> "Password cannot exceed 72 characters"
            !PASSWORD_REGEX.matches(password) -> "Must include uppercase, lowercase, number & special character"
            else -> null
        }
        _uiState.update { it.copy(password = password, passwordError = error, generalError = null) }
    }

    private fun updatePhone(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone, generalError = null) }
    }

    private fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun dismissError() {
        _uiState.update { it.copy(generalError = null) }
    }

    private fun submitRegistration() {
        val currentState = _uiState.value

        // Validate all fields before network dispatch
        val nameErr = if (currentState.name.trim().length < 2) "Full name is required (min 2 chars)" else null
        val emailErr = if (!EMAIL_REGEX.matches(currentState.email.trim())) "Valid email is required" else null
        val passErr = if (!PASSWORD_REGEX.matches(currentState.password) || currentState.password.length < 8) {
            "Strong password is required (min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 symbol)"
        } else null

        if (nameErr != null || emailErr != null || passErr != null) {
            _uiState.update {
                it.copy(nameError = nameErr, emailError = emailErr, passwordError = passErr)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            Log.d(TAG, "Attempting registration for email: ${currentState.email.trim().lowercase()}")

            val result = authRepository.register(
                name = currentState.name.trim(),
                email = currentState.email.trim().lowercase(),
                password = currentState.password,
                phoneNumber = currentState.phoneNumber.trim().ifEmpty { null }
            )

            when (result) {
                is NetworkResult.Success -> {
                    val user = result.data
                    Log.i(TAG, "Registration successful: ${user?.email} (ID: ${user?.id})")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registeredUser = user,
                            generalError = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Log.e(TAG, "Registration failed: ${result.message} [Code: ${result.code}]")
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
