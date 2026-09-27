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
 * Root ApiResponse Envelope matching backend ApiResponse utility.
 */
@Serializable
data class AuthResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: AuthDataDto? = null
)
