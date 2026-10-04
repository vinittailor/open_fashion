package com.example.open_fashion.features.auth.presentation.forgotpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.open_fashion.core.constants.ValidationPatterns
import com.example.open_fashion.core.network.NetworkResult
import com.example.open_fashion.features.auth.data.repository.AuthRepositoryImpl
import com.example.open_fashion.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "OpenFashionForgotVM"

/**
 * MVI ViewModel orchestrating the 2-step customer password recovery flow.
 */
class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    /**
     * Single entry-point intent reducer handling all user interactions.
     */
    fun onIntent(intent: ForgotPasswordUiIntent) {
        when (intent) {
            is ForgotPasswordUiIntent.OnEmailChanged -> updateEmail(intent.email)
            is ForgotPasswordUiIntent.OnTokenOrOtpChanged -> updateTokenOrOtp(intent.tokenOrOtp)
            is ForgotPasswordUiIntent.OnNewPasswordChanged -> updateNewPassword(intent.password)
            is ForgotPasswordUiIntent.OnConfirmPasswordChanged -> updateConfirmPassword(intent.confirmPassword)
            is ForgotPasswordUiIntent.OnTogglePasswordVisibility -> togglePasswordVisibility()
            is ForgotPasswordUiIntent.OnSubmitEmail -> submitEmail()
            is ForgotPasswordUiIntent.OnSubmitReset -> submitReset()
            is ForgotPasswordUiIntent.OnSwitchToStep1 -> switchToStep1()
            is ForgotPasswordUiIntent.OnDismissError -> dismissError()
            is ForgotPasswordUiIntent.OnAutoFillDevOtp -> autoFillDevOtp()
        }
    }

    private fun updateEmail(email: String) {
        val error = when {
            email.isBlank() -> null
            !ValidationPatterns.isValidEmail(email) -> "Please provide a valid email address"
            else -> null
        }
        _uiState.update { it.copy(email = email, emailError = error, generalError = null) }
    }

    private fun updateTokenOrOtp(tokenOrOtp: String) {
        val error = when {
            tokenOrOtp.isBlank() -> null
            tokenOrOtp.trim().length < 4 -> "Please enter a valid OTP or reset token"
            else -> null
        }
        _uiState.update { it.copy(tokenOrOtp = tokenOrOtp, tokenOrOtpError = error, generalError = null) }
    }

    private fun updateNewPassword(password: String) {
        val error = when {
            password.isEmpty() -> null
            !ValidationPatterns.isValidPassword(password) -> "Must be at least 8 characters with uppercase, lowercase, number & special character"
            else -> null
        }
        _uiState.update {
            it.copy(
                newPassword = password,
                passwordError = error,
                confirmPasswordError = if (it.confirmPassword.isNotEmpty() && it.confirmPassword != password) "Passwords do not match" else null,
                generalError = null
            )
        }
    }

    private fun updateConfirmPassword(confirm: String) {
        val error = when {
            confirm.isEmpty() -> null
            confirm != _uiState.value.newPassword -> "Passwords do not match"
            else -> null
        }
        _uiState.update { it.copy(confirmPassword = confirm, confirmPasswordError = error, generalError = null) }
    }

    private fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun switchToStep1() {
        _uiState.update { it.copy(currentStep = ForgotPasswordStep.ENTER_EMAIL, generalError = null) }
    }

    private fun dismissError() {
        _uiState.update { it.copy(generalError = null) }
    }

    private fun autoFillDevOtp() {
        val otp = _uiState.value.devOtp
        if (!otp.isNullOrBlank()) {
            _uiState.update { it.copy(tokenOrOtp = otp, tokenOrOtpError = null) }
        }
    }

    /**
     * Step 1: Dispatches recovery request to backend.
     */
    private fun submitEmail() {
        val state = _uiState.value
        if (!ValidationPatterns.isValidEmail(state.email)) {
            _uiState.update { it.copy(emailError = "Valid email is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            Log.d(TAG, "Requesting password reset for email: ${state.email.trim().lowercase()}")

            val result = authRepository.forgotPassword(email = state.email.trim().lowercase())

            when (result) {
                is NetworkResult.Success -> {
                    val actionResult = result.data
                    Log.i(TAG, "Reset code dispatched. devOtp=${actionResult?.devOtp}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentStep = ForgotPasswordStep.RESET_PASSWORD,
                            devOtp = actionResult?.devOtp,
                            devToken = actionResult?.devToken,
                            tokenOrOtp = actionResult?.devOtp ?: "", // auto-fill OTP in dev mode for testing convenience
                            successMessage = actionResult?.message ?: "Reset code sent to your email."
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Log.e(TAG, "ForgotPassword failed: ${result.message}")
                    _uiState.update {
                        it.copy(isLoading = false, generalError = result.message)
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    /**
     * Step 2: Submits OTP/Token and new password to finalize reset.
     */
    private fun submitReset() {
        val state = _uiState.value

        val tokenErr = if (state.tokenOrOtp.isBlank()) "OTP / Token is required" else null
        val passErr = if (!ValidationPatterns.isValidPassword(state.newPassword)) "Password does not meet complexity rules" else null
        val matchErr = if (state.newPassword != state.confirmPassword) "Passwords do not match" else null

        if (tokenErr != null || passErr != null || matchErr != null) {
            _uiState.update {
                it.copy(
                    tokenOrOtpError = tokenErr,
                    passwordError = passErr,
                    confirmPasswordError = matchErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            Log.d(TAG, "Submitting password reset for: ${state.email}")

            val result = authRepository.resetPassword(
                token = state.tokenOrOtp.trim(),
                newPassword = state.newPassword,
                email = state.email.trim().lowercase()
            )

            when (result) {
                is NetworkResult.Success -> {
                    Log.i(TAG, "Password reset successful!")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isResetComplete = true,
                            successMessage = result.data?.message ?: "Password reset successfully. Please sign in."
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Log.e(TAG, "Password reset error: ${result.message}")
                    _uiState.update {
                        it.copy(isLoading = false, generalError = result.message)
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
