package com.example.open_fashion.features.profile.domain.repository

import com.example.open_fashion.core.network.NetworkResult
import com.example.open_fashion.features.auth.domain.model.User

/**
 * Domain boundary contract for Customer Profile operations.
 */
interface UserRepository {

    /**
     * Fetches the current authenticated customer's profile.
     */
    suspend fun getProfile(): NetworkResult<User>

    /**
     * Updates customer name or phone number.
     */
    suspend fun updateProfile(
        name: String?,
        phoneNumber: String?
    ): NetworkResult<User>
}
