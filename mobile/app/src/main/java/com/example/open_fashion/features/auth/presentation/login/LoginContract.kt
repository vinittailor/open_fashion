package com.example.open_fashion.features.auth.presentation.login

import com.example.open_fashion.features.auth.domain.model.AuthSession

/**
 * Immutable single source of truth for the Customer Login Composable Screen.
 */
data class LoginUiState(
    // Form Inputs
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,

    // Validation Field Errors
    val emailError: String? = null,
    val passwordError: String? = null,

    // Network & Submission Status
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val authSession: AuthSession? = null
) {
    /**
     * Determines if the login button is clickable.
     */
    val isSubmitEnabled: Boolean
        get() = email.isNotBlank() &&
                password.isNotBlank() &&
                emailError == null &&
                passwordError == null &&
                !isLoading

    /**
     * Helper verifying if login completed successfully.
     */
    val isSuccess: Boolean
        get() = authSession != null
}

/**
 * Sealed hierarchy of all user interactions (Intents) emitted by the Login UI.
 */
sealed interface LoginUiIntent {
    data class OnEmailChanged(val email: String) : LoginUiIntent
    data class OnPasswordChanged(val password: String) : LoginUiIntent
    data object OnTogglePasswordVisibility : LoginUiIntent
    data object OnSubmitLogin : LoginUiIntent
    data object OnDismissError : LoginUiIntent
}
