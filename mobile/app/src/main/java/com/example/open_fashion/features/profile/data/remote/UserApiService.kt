package com.example.open_fashion.features.profile.data.remote

import com.example.open_fashion.core.constants.ApiEndpoints
import com.example.open_fashion.features.profile.data.remote.dto.ProfileResponseDto
import com.example.open_fashion.features.profile.data.remote.dto.UpdateProfileRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

/**
 * Retrofit interface for authenticated user profile endpoints.
 */
interface UserApiService {

    /**
     * Retrieves the authenticated user's profile.
     * Endpoint: GET /api/v1/users/me
     */
    @GET(ApiEndpoints.USERS_ME)
    suspend fun getMe(): Response<ProfileResponseDto>

    /**
     * Updates the authenticated user's profile information.
     * Endpoint: PATCH /api/v1/users/me
     */
    @PATCH(ApiEndpoints.USERS_ME)
    suspend fun updateMe(
        @Body request: UpdateProfileRequestDto
    ): Response<ProfileResponseDto>
}
