package com.example.open_fashion.features.auth.data.remote

import com.example.open_fashion.core.constants.ApiEndpoints
import com.example.open_fashion.features.auth.data.remote.dto.AuthResponseDto
import com.example.open_fashion.features.auth.data.remote.dto.LoginRequestDto
import com.example.open_fashion.features.auth.data.remote.dto.LoginResponseDto
import com.example.open_fashion.features.auth.data.remote.dto.RefreshTokenRequestDto
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
    @POST(ApiEndpoints.REGISTER)
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<AuthResponseDto>

    /**
     * Sends customer login credentials to backend.
     * Endpoint: POST /api/v1/auth/login
     */
    @POST(ApiEndpoints.LOGIN)
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>

    /**
     * Sends refresh token to rotate session and receive new access/refresh tokens.
     * Endpoint: POST /api/v1/auth/refresh
     */
    @POST(ApiEndpoints.REFRESH)
    suspend fun refreshToken(
        @Body request: RefreshTokenRequestDto
    ): Response<LoginResponseDto>

    /**
     * Informs the backend to revoke the active session in Redis.
     * Endpoint: POST /api/v1/auth/logout
     */
    @POST(ApiEndpoints.LOGOUT)
    suspend fun logout(
        @Body body: Map<String, String>? = null
    ): Response<Unit>
}
