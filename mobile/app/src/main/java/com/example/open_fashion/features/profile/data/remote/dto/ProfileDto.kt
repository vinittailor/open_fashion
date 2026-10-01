package com.example.open_fashion.features.profile.data.remote.dto

import com.example.open_fashion.features.auth.data.remote.dto.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Payload for updating user profile: PATCH /api/v1/users/me
 */
@Serializable
data class UpdateProfileRequestDto(
    @SerialName("name") val name: String? = null,
    @SerialName("phoneNumber") val phoneNumber: String? = null
)

@Serializable
data class ProfileDataDto(
    @SerialName("user") val user: UserDto
)

@Serializable
data class ProfileResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: ProfileDataDto? = null
)
