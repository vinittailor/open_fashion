package com.example.open_fashion.core.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.open_fashion.features.auth.domain.model.AuthSession
import com.example.open_fashion.features.auth.domain.model.User

/**
 * Manages local persistence of JWT authentication credentials and active user session.
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "open_fashion_auth_prefs"
        private const val KEY_ACCESS_TOKEN = "key_access_token"
        private const val KEY_REFRESH_TOKEN = "key_refresh_token"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_USER_ROLE = "key_user_role"
        private const val KEY_USER_PHONE = "key_user_phone"
        private const val KEY_USER_VERIFIED = "key_user_verified"
    }

    /**
     * Persists the authenticated [AuthSession] tokens and customer profile to local storage.
     */
    fun saveSession(session: AuthSession) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, session.accessToken)
            putString(KEY_REFRESH_TOKEN, session.refreshToken)
            putString(KEY_USER_ID, session.user.id)
            putString(KEY_USER_NAME, session.user.name)
            putString(KEY_USER_EMAIL, session.user.email)
            putString(KEY_USER_ROLE, session.user.role)
            putString(KEY_USER_PHONE, session.user.phoneNumber)
            putBoolean(KEY_USER_VERIFIED, session.user.isEmailVerified)
            apply()
        }
    }

    /**
     * Retrieves the stored JWT access token.
     */
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    /**
     * Retrieves the stored refresh token for session renewal.
     */
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    /**
     * Reconstructs the cached [User] entity from local preferences.
     */
    fun getUser(): User? {
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val name = prefs.getString(KEY_USER_NAME, "") ?: ""
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val role = prefs.getString(KEY_USER_ROLE, "CUSTOMER") ?: "CUSTOMER"
        val phone = prefs.getString(KEY_USER_PHONE, null)
        val verified = prefs.getBoolean(KEY_USER_VERIFIED, false)

        return User(
            id = id,
            name = name,
            email = email,
            role = role,
            phoneNumber = phone,
            isEmailVerified = verified
        )
    }

    /**
     * Returns true if valid authentication tokens are present in local storage.
     */
    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    /**
     * Clears all session tokens and user data upon logout.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
