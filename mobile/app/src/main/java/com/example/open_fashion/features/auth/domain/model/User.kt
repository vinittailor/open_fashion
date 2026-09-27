package com.example.open_fashion.features.auth.domain.model

/**
 * Pure Domain representation of an authenticated Customer in Open Fashion.
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String = "CUSTOMER",
    val phoneNumber: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: String? = null
)
