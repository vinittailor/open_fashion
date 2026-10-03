package com.example.open_fashion.features.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Registration HTTP Request Payload sent to POST /api/v1/auth/register.
 */
@Serializable
data class RegisterRequestDto(
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("phoneNumber") val phoneNumber: String? = null
)

/**
 * Login HTTP Request Payload sent to POST /api/v1/auth/login.
 */
@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

/**
 * Refresh Token HTTP Request Payload sent to POST /api/v1/auth/refresh.
 */
@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refreshToken") val refreshToken: String
)

/**
 * Serialized representation of a User record received from the backend.
 */
@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("role") val role: String = "CUSTOMER",
    @SerialName("phoneNumber") val phoneNumber: String? = null,
    @SerialName("isEmailVerified") val isEmailVerified: Boolean = false,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
)

/**
 * Nested container for registration response data.
 */
@Serializable
data class AuthDataDto(
    @SerialName("user") val user: UserDto
)

/**
 * Nested container for login and token refresh response data.
 */
@Serializable
data class LoginDataDto(
    @SerialName("user") val user: UserDto,
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("tokenType") val tokenType: String = "Bearer",
    @SerialName("expiresIn") val expiresIn: Int = 900,
    @SerialName("refreshExpiresIn") val refreshExpiresIn: Int = 604800
)

/**
 * Root ApiResponse Envelope for user registration.
 */
@Serializable
data class AuthResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: AuthDataDto? = null
)

/**
 * Root ApiResponse Envelope for user login and token rotation.
 */
@Serializable
data class LoginResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: LoginDataDto? = null
)

/**
 * Request payload for POST /api/v1/auth/forgot-password.
 */
@Serializable
data class ForgotPasswordRequestDto(
    @SerialName("email") val email: String
)

/**
 * Request payload for POST /api/v1/auth/reset-password.
 */
@Serializable
data class ResetPasswordRequestDto(
    @SerialName("token") val token: String,
    @SerialName("newPassword") val newPassword: String,
    @SerialName("email") val email: String? = null
)

/**
 * Request payload for POST /api/v1/auth/verify-email.
 */
@Serializable
data class VerifyEmailRequestDto(
    @SerialName("token") val token: String,
    @SerialName("email") val email: String? = null
)

/**
 * Nested container for action responses containing dev tokens and OTPs.
 */
@Serializable
data class ActionDataDto(
    @SerialName("devToken") val devToken: String? = null,
    @SerialName("devOtp") val devOtp: String? = null
)

/**
 * Root ApiResponse envelope for forgot-password, reset-password, and send-verification.
 */
@Serializable
data class ActionResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: ActionDataDto? = null
)
