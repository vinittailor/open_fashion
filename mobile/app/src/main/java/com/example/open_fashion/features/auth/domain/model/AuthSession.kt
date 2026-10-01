package com.example.open_fashion.features.auth.domain.model

/**
 * Pure Domain representation of an authenticated customer session in Open Fashion.
 *
 * @property user The authenticated customer profile entity.
 * @property accessToken The short-lived JWT access token for API authorization.
 * @property refreshToken The long-lived refresh token stored in Redis session whitelist.
 * @property tokenType Authorization header scheme (default: "Bearer").
 * @property expiresIn Access token lifespan in seconds (default: 900 / 15 minutes).
 * @property refreshExpiresIn Refresh token lifespan in seconds (default: 604800 / 7 days).
 */
data class AuthSession(
    val user: User,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Int = 900,
    val refreshExpiresIn: Int = 604800
)
