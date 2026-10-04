package com.example.open_fashion.features.auth.presentation.forgotpassword

/**
 * Visual stages of the recovery flow.
 */
enum class ForgotPasswordStep {
    ENTER_EMAIL,    // Step 1: Customer enters registered email
    RESET_PASSWORD  // Step 2: Customer enters OTP code and new password
}

/**
 * Immutable single source of truth for the Forgot Password Composable Screen.
 */
data class ForgotPasswordUiState(
    val currentStep: ForgotPasswordStep = ForgotPasswordStep.ENTER_EMAIL,

    // Step 1: Email Input
    val email: String = "",
    val emailError: String? = null,

    // Step 2: OTP & New Password Inputs
    val tokenOrOtp: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val tokenOrOtpError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

    // Development Testing Helpers (Pre-filled when backend returns dev OTP/token)
    val devOtp: String? = null,
    val devToken: String? = null,

    // Network & Submission Status
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val generalError: String? = null,
    val isResetComplete: Boolean = false
) {
    /**
     * Determines if the "Send Reset Code" button is active.
     */
    val isStep1SubmitEnabled: Boolean
        get() = email.isNotBlank() && emailError == null && !isLoading

    /**
     * Determines if the "Set New Password" button is active.
     */
    val isStep2SubmitEnabled: Boolean
        get() = tokenOrOtp.isNotBlank() &&
                newPassword.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                tokenOrOtpError == null &&
                passwordError == null &&
                confirmPasswordError == null &&
                !isLoading
}

/**
 * Sealed hierarchy of all user interactions (Intents) emitted by the Forgot Password UI.
 */
sealed interface ForgotPasswordUiIntent {
    data class OnEmailChanged(val email: String) : ForgotPasswordUiIntent
    data class OnTokenOrOtpChanged(val tokenOrOtp: String) : ForgotPasswordUiIntent
    data class OnNewPasswordChanged(val password: String) : ForgotPasswordUiIntent
    data class OnConfirmPasswordChanged(val confirmPassword: String) : ForgotPasswordUiIntent
    data object OnTogglePasswordVisibility : ForgotPasswordUiIntent
    data object OnSubmitEmail : ForgotPasswordUiIntent
    data object OnSubmitReset : ForgotPasswordUiIntent
    data object OnSwitchToStep1 : ForgotPasswordUiIntent
    data object OnDismissError : ForgotPasswordUiIntent
    data object OnAutoFillDevOtp : ForgotPasswordUiIntent
}
