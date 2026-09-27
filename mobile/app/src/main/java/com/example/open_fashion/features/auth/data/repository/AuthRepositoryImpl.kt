package com.example.open_fashion.features.auth.data.repository

import com.example.open_fashion.core.network.ApiClient
import com.example.open_fashion.core.network.NetworkResult
import com.example.open_fashion.features.auth.data.remote.AuthApiService
import com.example.open_fashion.features.auth.data.remote.dto.RegisterRequestDto
import com.example.open_fashion.features.auth.data.remote.dto.UserDto
import com.example.open_fashion.features.auth.domain.model.User
import com.example.open_fashion.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
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
                // Parse backend error envelope: { success: false, error: { message: "..." } }
                val errorJsonStr = response.errorBody()?.string()
                val parsedErrorMessage = try {
                    if (!errorJsonStr.isNullOrBlank()) {
                        val json = JSONObject(errorJsonStr)
                        val errorObj = json.optJSONObject("error")
                        errorObj?.optString("message") ?: json.optString("message", "Registration failed.")
                    } else {
                        "Server returned HTTP ${response.code()}"
                    }
                } catch (e: Exception) {
                    "Registration failed with HTTP ${response.code()}"
                }

                NetworkResult.Error(
                    code = response.code().toString(),
                    message = parsedErrorMessage
                )
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
