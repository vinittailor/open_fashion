package com.example.open_fashion.features.auth.domain.repository

import com.example.open_fashion.core.network.NetworkResult
import com.example.open_fashion.features.auth.domain.model.AuthActionResult
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

    /**
     * Dispatches a password reset link/OTP to the registered email address.
     *
     * @param email The account email address.
     * @return [NetworkResult.Success] containing [AuthActionResult] or [NetworkResult.Error].
     */
    suspend fun forgotPassword(
        email: String
    ): NetworkResult<AuthActionResult>

    /**
     * Resets account password using a validated token or 6-digit OTP.
     *
     * @param token Reset token string or OTP code.
     * @param newPassword New password meeting complexity criteria.
     * @param email Optional email address when using OTP-based reset.
     * @return [NetworkResult.Success] containing [AuthActionResult] or [NetworkResult.Error].
     */
    suspend fun resetPassword(
        token: String,
        newPassword: String,
        email: String? = null
    ): NetworkResult<AuthActionResult>

    /**
     * Dispatches an email verification token/OTP for the currently authenticated user.
     *
     * @return [NetworkResult.Success] containing [AuthActionResult] or [NetworkResult.Error].
     */
    suspend fun sendEmailVerification(): NetworkResult<AuthActionResult>

    /**
     * Confirms customer email address ownership using token or OTP.
     *
     * @param token Verification token or 6-digit OTP code.
     * @param email Optional email address when verifying via OTP.
     * @return [NetworkResult.Success] containing [AuthActionResult] or [NetworkResult.Error].
     */
    suspend fun verifyEmail(
        token: String,
        email: String? = null
    ): NetworkResult<AuthActionResult>
}
