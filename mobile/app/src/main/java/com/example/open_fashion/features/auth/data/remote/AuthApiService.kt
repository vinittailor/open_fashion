package com.example.open_fashion.features.auth.data.remote

import com.example.open_fashion.features.auth.data.remote.dto.AuthResponseDto
import com.example.open_fashion.features.auth.data.remote.dto.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Declarative Retrofit interface for Open Fashion Authentication endpoints.
 */
interface AuthApiService {

    /**
     * Sends customer registration payload to backend.
     * Endpoint: POST /api/v1/auth/register
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<AuthResponseDto>
}
