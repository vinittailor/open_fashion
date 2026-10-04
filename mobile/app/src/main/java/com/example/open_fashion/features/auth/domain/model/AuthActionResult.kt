package com.example.open_fashion.features.auth.domain.model

/**
 * Pure Domain representation of an authentication action response (password reset, email verification).
 *
 * @property message Human-readable confirmation message from the backend.
 * @property devToken Generated raw SHA-256 token string (available only in development mode for easy testing).
 * @property devOtp Generated 6-digit numeric OTP (available only in development mode for easy testing).
 */
data class AuthActionResult(
    val message: String? = null,
    val devToken: String? = null,
    val devOtp: String? = null
)
