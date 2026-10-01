package com.example.open_fashion.features.auth.domain.repository

import com.example.open_fashion.core.network.NetworkResult
import com.example.open_fashion.features.auth.domain.model.AuthSession
import com.example.open_fashion.features.auth.domain.model.User

/**
 * Domain boundary contract for Open Fashion customer authentication & accounts.
 */
interface AuthRepository {

    /**
     * Executes customer registration against backend with sanitized inputs.
     *
     * @param name Full customer name.
     * @param email Validated email address.
     * @param password Password meeting enterprise complexity rules.
     * @param phoneNumber Optional E.164 phone string.
     * @return [NetworkResult.Success] with [User] or [NetworkResult.Error] with server feedback.
     */
    suspend fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String? = null
    ): NetworkResult<User>

    /**
     * Authenticates customer credentials against backend.
     *
     * @param email Customer's registered email address.
     * @param password Raw plaintext password.
     * @return [NetworkResult.Success] containing the [AuthSession] or [NetworkResult.Error].
     */
    suspend fun login(
        email: String,
        password: String
    ): NetworkResult<AuthSession>

    /**
     * Rotates access and refresh tokens using an active refresh token.
     *
     * @param refreshToken The valid refresh token stored in local secure storage.
     * @return [NetworkResult.Success] with updated [AuthSession] or [NetworkResult.Error].
     */
    suspend fun refreshToken(
        refreshToken: String
    ): NetworkResult<AuthSession>

    /**
     * Terminates the current session and invalidates the Redis whitelist entry.
     *
     * @return [NetworkResult.Success] on completion or [NetworkResult.Error].
     */
    suspend fun logout(): NetworkResult<Unit>
}
