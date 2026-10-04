package com.example.open_fashion.core.network

import com.example.open_fashion.core.storage.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor: Dynamically injects JWT Bearer Access Token into outgoing HTTP requests.
 */
class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // Skip adding Authorization header on public authentication endpoints
        val isPublicEndpoint = path.contains("auth/login") ||
                path.contains("auth/register") ||
                path.contains("auth/refresh") ||
                path.contains("auth/forgot-password") ||
                path.contains("auth/reset-password") ||
                path.contains("auth/verify-email")

        val token = tokenManager.getAccessToken()

        val request = if (!isPublicEndpoint && !token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
