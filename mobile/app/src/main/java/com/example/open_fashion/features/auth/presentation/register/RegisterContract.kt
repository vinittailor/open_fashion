package com.example.open_fashion.features.auth.presentation.register

import com.example.open_fashion.features.auth.domain.model.User

/**
 * Immutable single source of truth for the Registration Composable Screen.
 */
data class RegisterUiState(
    // Form Inputs
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val isPasswordVisible: Boolean = false,

    // Validation Field Errors
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,

    // Network & Submission Status
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val registeredUser: User? = null
) {
    /**
     * Determines if the form is eligible for submission.
     */
    val isSubmitEnabled: Boolean
        get() = name.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                nameError == null &&
                emailError == null &&
                passwordError == null &&
                !isLoading

    /**
     * Helper to verify if registration completed successfully.
     */
    val isSuccess: Boolean
        get() = registeredUser != null
}

/**
 * Sealed hierarchy of all user interactions (Intents) emitted by the UI.
 */
sealed interface RegisterUiIntent {
    data class OnNameChanged(val name: String) : RegisterUiIntent
    data class OnEmailChanged(val email: String) : RegisterUiIntent
    data class OnPasswordChanged(val password: String) : RegisterUiIntent
    data class OnPhoneChanged(val phone: String) : RegisterUiIntent
    data object OnTogglePasswordVisibility : RegisterUiIntent
    data object OnSubmitRegister : RegisterUiIntent
    data object OnDismissError : RegisterUiIntent
}
