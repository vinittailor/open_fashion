package com.example.open_fashion.features.auth.domain.repository

import com.example.open_fashion.core.network.NetworkResult
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
}
