package com.example.open_fashion.features.auth.data.repository

import android.util.Log
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
import com.example.open_fashion.core.storage.TokenManager
import com.example.open_fashion.features.auth.data.remote.dto.ForgotPasswordRequestDto
import com.example.open_fashion.features.auth.data.remote.dto.ResetPasswordRequestDto
import com.example.open_fashion.features.auth.data.remote.dto.VerifyEmailRequestDto
import com.example.open_fashion.features.auth.domain.model.AuthActionResult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

private const val TAG = "OpenFashionAuthRepo"

/**
 * Concrete implementation of [AuthRepository] managing remote API interactions.
 */
class AuthRepositoryImpl(
    private val authApiService: AuthApiService = ApiClient.create(),
    private val tokenManager: TokenManager? = null
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
            Log.e(TAG, "Registration Network I/O Error: ${e.message}", e)
            NetworkResult.Error(
                message = "Unable to connect to server. Please check your internet or emulator connection.",
                throwable = e
            )
        } catch (e: Exception) {
            Log.e(TAG, "Registration Unexpected Error: ${e.message}", e)
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
            Log.e(TAG, "Login Network I/O Error: ${e.message}", e)
            NetworkResult.Error(
                message = "Unable to connect to server. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            Log.e(TAG, "Login Unexpected Error: ${e.message}", e)
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
            Log.e(TAG, "Token Refresh Network I/O Error: ${e.message}", e)
            NetworkResult.Error(
                message = "Network connection failed during token refresh.",
                throwable = e
            )
        } catch (e: Exception) {
            Log.e(TAG, "Token Refresh Unexpected Error: ${e.message}", e)
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
            Log.w(TAG, "Logout API error (ignored for local cleanup): ${e.message}")
            // Best-effort logout: treat as success for local clearing
            NetworkResult.Success(Unit)
        }
    }

    override suspend fun forgotPassword(
        email: String
    ): NetworkResult<AuthActionResult> = withContext(Dispatchers.IO) {
        try {
            // 1. Create the strongly-typed request payload
            val request = ForgotPasswordRequestDto(email = email)

            // 2. Execute the HTTP POST call via Retrofit
            val response = authApiService.forgotPassword(request)

            // 3. Evaluate HTTP status code
            if (response.isSuccessful) {
                val body = response.body()
                NetworkResult.Success(
                    AuthActionResult(
                        message = body?.message ?: "Password reset instructions dispatched.",
                        devToken = body?.data?.devToken,
                        devOtp = body?.data?.devOtp
                    )
                )
            } else {
                parseErrorResponse(response, "Failed to request password reset.")
            }
        } catch (e: IOException) {
            Log.e(TAG, "ForgotPassword Network I/O Error: ${e.message}", e)
            NetworkResult.Error(
                message = "Unable to connect to server. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            Log.e(TAG, "ForgotPassword Unexpected Error: ${e.message}", e)
            NetworkResult.Error(
                message = e.localizedMessage ?: "An unexpected error occurred during password reset request.",
                throwable = e
            )
        }
    }

    override suspend fun resetPassword(
        token: String,
        newPassword: String,
        email: String?
    ): NetworkResult<AuthActionResult> = withContext(Dispatchers.IO) {
        try {
            // 1. Create the strongly-typed ResetPassword request payload
            val request = ResetPasswordRequestDto(
                token = token,
                newPassword = newPassword,
                email = email
            )

            // 2. Call Retrofit endpoint
            val response = authApiService.resetPassword(request)

            // 3. Check response status
            if (response.isSuccessful) {
                val body = response.body()
                NetworkResult.Success(
                    AuthActionResult(
                        message = body?.message ?: "Password reset successfully. Please sign in.",
                        devToken = body?.data?.devToken,
                        devOtp = body?.data?.devOtp
                    )
                )
            } else {
                parseErrorResponse(response, "Failed to reset password. Invalid or expired token.")
            }
        } catch (e: IOException) {
            Log.e(TAG, "ResetPassword Network I/O Error: ${e.message}", e)
            NetworkResult.Error(
                message = "Unable to connect to server. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            Log.e(TAG, "ResetPassword Unexpected Error: ${e.message}", e)
            NetworkResult.Error(
                message = e.localizedMessage ?: "An unexpected error occurred during password reset.",
                throwable = e
            )
        }
    }

    override suspend fun sendEmailVerification(): NetworkResult<AuthActionResult> = withContext(Dispatchers.IO) {
        try {
            // Header is automatically attached by AuthInterceptor in OkHttp!
            val response = authApiService.sendEmailVerification()

            if (response.isSuccessful) {
                val body = response.body()
                NetworkResult.Success(
                    AuthActionResult(
                        message = body?.message ?: "Verification email sent.",
                        devToken = body?.data?.devToken,
                        devOtp = body?.data?.devOtp
                    )
                )
            } else {
                parseErrorResponse(response, "Failed to send verification email.")
            }
        } catch (e: IOException) {
            Log.e(TAG, "SendEmailVerification Network I/O Error: ${e.message}", e)
            NetworkResult.Error(
                message = "Unable to connect to server. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            Log.e(TAG, "SendEmailVerification Unexpected Error: ${e.message}", e)
            NetworkResult.Error(
                message = e.localizedMessage ?: "An unexpected error occurred sending verification email.",
                throwable = e
            )
        }
    }

    override suspend fun verifyEmail(
        token: String,
        email: String?
    ): NetworkResult<AuthActionResult> = withContext(Dispatchers.IO) {
        try {
            // 1. Create the strongly-typed VerifyEmail request payload
            val request = VerifyEmailRequestDto(token = token, email = email)

            // 2. Execute POST /api/v1/auth/verify-email
            val response = authApiService.verifyEmail(request)

            // 3. Evaluate response
            if (response.isSuccessful) {
                val body = response.body()
                NetworkResult.Success(
                    AuthActionResult(
                        message = body?.message ?: "Email verified successfully.",
                        devToken = body?.data?.devToken,
                        devOtp = body?.data?.devOtp
                    )
                )
            } else {
                parseErrorResponse(response, "Failed to verify email. Invalid or expired token.")
            }
        } catch (e: IOException) {
            Log.e(TAG, "VerifyEmail Network I/O Error: ${e.message}", e)
            NetworkResult.Error(
                message = "Unable to connect to server. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            Log.e(TAG, "VerifyEmail Unexpected Error: ${e.message}", e)
            NetworkResult.Error(
                message = e.localizedMessage ?: "An unexpected error occurred verifying email.",
                throwable = e
            )
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
            Log.w(TAG, "Failed to parse error body: $errorJsonStr", e)
            defaultMessage
        }

        Log.e(TAG, "HTTP ${response.code()} Error: $parsedErrorMessage (Raw: $errorJsonStr)")

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
