package com.example.open_fashion.features.profile.data.repository

import android.util.Log
import com.example.open_fashion.core.network.ApiClient
import com.example.open_fashion.core.network.NetworkResult
import com.example.open_fashion.features.auth.data.remote.dto.UserDto
import com.example.open_fashion.features.auth.domain.model.User
import com.example.open_fashion.features.profile.data.remote.UserApiService
import com.example.open_fashion.features.profile.data.remote.dto.UpdateProfileRequestDto
import com.example.open_fashion.features.profile.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

private const val TAG = "OpenFashionUserRepo"

/**
 * Concrete implementation of [UserRepository] communicating with backend /users endpoints.
 */
class UserRepositoryImpl(
    private val userApiService: UserApiService = ApiClient.create()
) : UserRepository {

    override suspend fun getProfile(): NetworkResult<User> = withContext(Dispatchers.IO) {
        try {
            val response = userApiService.getMe()

            if (response.isSuccessful) {
                val userDto = response.body()?.data?.user
                if (userDto != null) {
                    NetworkResult.Success(userDto.toDomain())
                } else {
                    NetworkResult.Error(message = "User profile retrieved successfully.")
                }
            } else {
                parseErrorResponse(response, "Failed to load user profile.")
            }
        } catch (e: IOException) {
            Log.e(TAG, "getProfile Network I/O Error: ${e.message}", e)
            NetworkResult.Error(
                message = "Unable to connect to server. Please check your internet connection.",
                throwable = e
            )
        } catch (e: Exception) {
            Log.e(TAG, "getProfile Unexpected Error: ${e.message}", e)
            NetworkResult.Error(
                message = e.localizedMessage ?: "Failed to retrieve user profile.",
                throwable = e
            )
        }
    }

    override suspend fun updateProfile(
        name: String?,
        phoneNumber: String?
    ): NetworkResult<User> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateProfileRequestDto(
                name = name,
                phoneNumber = phoneNumber
            )

            val response = userApiService.updateMe(request)

            if (response.isSuccessful) {
                val userDto = response.body()?.data?.user
                if (userDto != null) {
                    NetworkResult.Success(userDto.toDomain())
                } else {
                    NetworkResult.Error(message = "Profile updated successfully.")
                }
            } else {
                parseErrorResponse(response, "Failed to update profile.")
            }
        } catch (e: IOException) {
            Log.e(TAG, "updateProfile Network I/O Error: ${e.message}", e)
            NetworkResult.Error(
                message = "Unable to connect to server. Please check your internet connection.",
                throwable = e
            )
        } catch (e: Exception) {
            Log.e(TAG, "updateProfile Unexpected Error: ${e.message}", e)
            NetworkResult.Error(
                message = e.localizedMessage ?: "An unexpected error occurred during profile update.",
                throwable = e
            )
        }
    }

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
