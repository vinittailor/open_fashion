package com.example.open_fashion.features.auth.data.repository

import com.example.open_fashion.core.network.ApiClient
import com.example.open_fashion.core.network.NetworkResult
import com.example.open_fashion.features.auth.data.remote.AuthApiService
import com.example.open_fashion.features.auth.data.remote.dto.LoginDataDto
import com.example.open_fashion.features.auth.data.remote.dto.LoginRequestDto
import com.example.open_fashion.features.auth.data.remote.dto.RefreshTokenRequestDto
import com.example.open_fashion.features.auth.data.remote.dto.RegisterRequestDto
import com.example.open_fashion.features.auth.data.remote.dto.UserDto
import com.example.open_fashion.features.auth.domain.model.AuthSession
import com.example.open_fashion.features.auth.domain.model.User
import com.example.open_fashion.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

/**
 * Concrete implementation of [AuthRepository] managing remote API interactions.
 */
class AuthRepositoryImpl(
    private val authApiService: AuthApiService = ApiClient.create()
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String?
    ): NetworkResult<User> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequestDto(
                name = name,
                email = email,
                password = password,
                phoneNumber = phoneNumber
            )

            val response = authApiService.register(request)

            if (response.isSuccessful) {
                val body = response.body()
                val userDto = body?.data?.user
                if (userDto != null) {
                    NetworkResult.Success(userDto.toDomain())
                } else {
                    NetworkResult.Error(message = body?.message ?: "Account registered successfully.")
                }
            } else {
                parseErrorResponse(response, "Registration failed.")
            }
        } catch (e: IOException) {
            NetworkResult.Error(
                message = "Unable to connect to server. Please check your internet or emulator connection.",
                throwable = e
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                message = e.localizedMessage ?: "An unexpected error occurred during registration.",
                throwable = e
            )
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): NetworkResult<AuthSession> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequestDto(
                email = email,
                password = password
            )

            val response = authApiService.login(request)

            if (response.isSuccessful) {
                val body = response.body()
                val loginData = body?.data
                if (loginData != null) {
                    NetworkResult.Success(loginData.toDomain())
                } else {
                    NetworkResult.Error(message = body?.message ?: "Login successful.")
                }
            } else {
                parseErrorResponse(response, "Invalid email or password.")
            }
        } catch (e: IOException) {
            NetworkResult.Error(
                message = "Unable to connect to server. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                message = e.localizedMessage ?: "An unexpected error occurred during login.",
                throwable = e
            )
        }
    }

    override suspend fun refreshToken(
        refreshToken: String
    ): NetworkResult<AuthSession> = withContext(Dispatchers.IO) {
        try {
            val request = RefreshTokenRequestDto(refreshToken = refreshToken)
            val response = authApiService.refreshToken(request)

            if (response.isSuccessful) {
                val loginData = response.body()?.data
                if (loginData != null) {
                    NetworkResult.Success(loginData.toDomain())
                } else {
                    NetworkResult.Error(message = "Session refreshed successfully.")
                }
            } else {
                parseErrorResponse(response, "Session expired. Please sign in again.")
            }
        } catch (e: IOException) {
            NetworkResult.Error(
                message = "Network connection failed during token refresh.",
                throwable = e
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                message = e.localizedMessage ?: "Token refresh failed.",
                throwable = e
            )
        }
    }

    override suspend fun logout(): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            authApiService.logout()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            // Best-effort logout: treat as success for local clearing
            NetworkResult.Success(Unit)
        }
    }

    /**
     * Helper to extract human-readable error messages from the backend JSON response envelope.
     */
    private fun <T> parseErrorResponse(response: Response<*>, defaultMessage: String): NetworkResult<T> {
        val errorJsonStr = response.errorBody()?.string()
        val parsedErrorMessage = try {
            if (!errorJsonStr.isNullOrBlank()) {
                val json = JSONObject(errorJsonStr)
                val errorObj = json.optJSONObject("error")
                errorObj?.optString("message") ?: json.optString("message", defaultMessage)
            } else {
                "Server returned HTTP ${response.code()}"
            }
        } catch (e: Exception) {
            defaultMessage
        }

        return NetworkResult.Error(
            code = response.code().toString(),
            message = parsedErrorMessage
        )
    }
}

/**
 * Mapper extension: Converts Network [UserDto] to Domain [User].
 */
private fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    role = role,
    phoneNumber = phoneNumber,
    isEmailVerified = isEmailVerified,
    createdAt = createdAt
)

/**
 * Mapper extension: Converts Network [LoginDataDto] to Domain [AuthSession].
 */
private fun LoginDataDto.toDomain(): AuthSession = AuthSession(
    user = user.toDomain(),
    accessToken = accessToken,
    refreshToken = refreshToken,
    tokenType = tokenType,
    expiresIn = expiresIn,
    refreshExpiresIn = refreshExpiresIn
)
