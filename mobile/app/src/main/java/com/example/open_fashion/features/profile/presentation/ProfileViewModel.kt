package com.example.open_fashion.features.profile.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.open_fashion.core.network.NetworkResult
import com.example.open_fashion.core.storage.TokenManager
import com.example.open_fashion.features.auth.domain.model.AuthSession
import com.example.open_fashion.features.media.domain.repository.FileRepository
import com.example.open_fashion.features.profile.data.repository.UserRepositoryImpl
import com.example.open_fashion.features.profile.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "OpenFashionProfileVM"

/**
 * MVI ViewModel managing Customer Profile viewing, editing, and avatar uploads.
 */
class ProfileViewModel(
    private val userRepository: UserRepository = UserRepositoryImpl(),
    private val fileRepository: FileRepository? = null,
    private val tokenManager: TokenManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(user = tokenManager?.getUser())
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onIntent(intent: ProfileUiIntent) {
        when (intent) {
            is ProfileUiIntent.OnLoadProfile -> loadProfile()
            is ProfileUiIntent.OnOpenEditSheet -> openEditSheet()
            is ProfileUiIntent.OnDismissEditSheet -> _uiState.update { it.copy(isEditSheetOpen = false) }
            is ProfileUiIntent.OnEditNameChanged -> updateEditName(intent.name)
            is ProfileUiIntent.OnEditPhoneChanged -> updateEditPhone(intent.phone)
            is ProfileUiIntent.OnSubmitProfileUpdate -> submitProfileUpdate()
            is ProfileUiIntent.OnAvatarSelected -> uploadAvatar(intent.uri)
            is ProfileUiIntent.OnDismissMessage -> _uiState.update {
                it.copy(generalError = null, updateSuccessMessage = null)
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = userRepository.getProfile()
            when (result) {
                is NetworkResult.Success -> {
                    val freshUser = result.data
                    Log.i(TAG, "Profile loaded for user: ${freshUser?.email}")
                    if (freshUser != null && tokenManager != null) {
                        // Update cached user in TokenManager
                        val currentAccessToken = tokenManager.getAccessToken() ?: ""
                        val currentRefreshToken = tokenManager.getRefreshToken() ?: ""
                        tokenManager.saveSession(
                            AuthSession(
                                user = freshUser,
                                accessToken = currentAccessToken,
                                refreshToken = currentRefreshToken
                            )
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = freshUser,
                            generalError = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Log.e(TAG, "Failed to load profile: ${result.message} [Code: ${result.code}]")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun uploadAvatar(uri: Uri) {
        val repo = fileRepository ?: run {
            Log.e(TAG, "Cannot upload avatar: FileRepository is null")
            _uiState.update { it.copy(generalError = "Media upload service not initialized") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingAvatar = true, generalError = null) }

            val result = repo.uploadFile(uri)
            result.onSuccess { fileItem ->
                Log.i(TAG, "Avatar uploaded successfully: ${fileItem.url}")
                _uiState.update {
                    it.copy(
                        isUploadingAvatar = false,
                        avatarUrl = fileItem.url,
                        updateSuccessMessage = "Avatar uploaded successfully!"
                    )
                }
            }.onFailure { error ->
                Log.e(TAG, "Avatar upload failed: ${error.message}", error)
                _uiState.update {
                    it.copy(
                        isUploadingAvatar = false,
                        generalError = error.message ?: "Failed to upload avatar"
                    )
                }
            }
        }
    }

    private fun openEditSheet() {
        val currentUser = _uiState.value.user
        _uiState.update {
            it.copy(
                isEditSheetOpen = true,
                editName = currentUser?.name ?: "",
                editPhone = currentUser?.phoneNumber ?: "",
                nameError = null
            )
        }
    }

    private fun updateEditName(name: String) {
        val error = if (name.trim().length < 2) "Name must be at least 2 characters" else null
        _uiState.update { it.copy(editName = name, nameError = error) }
    }

    private fun updateEditPhone(phone: String) {
        _uiState.update { it.copy(editPhone = phone) }
    }

    private fun submitProfileUpdate() {
        val currentState = _uiState.value
        if (currentState.editName.trim().length < 2) {
            _uiState.update { it.copy(nameError = "Name must be at least 2 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, generalError = null) }

            val result = userRepository.updateProfile(
                name = currentState.editName.trim(),
                phoneNumber = currentState.editPhone.trim().ifEmpty { null }
            )

            when (result) {
                is NetworkResult.Success -> {
                    val updatedUser = result.data
                    Log.i(TAG, "Profile updated successfully for user: ${updatedUser?.email}")
                    if (updatedUser != null && tokenManager != null) {
                        val currentAccessToken = tokenManager.getAccessToken() ?: ""
                        val currentRefreshToken = tokenManager.getRefreshToken() ?: ""
                        tokenManager.saveSession(
                            AuthSession(
                                user = updatedUser,
                                accessToken = currentAccessToken,
                                refreshToken = currentRefreshToken
                            )
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            isEditSheetOpen = false,
                            user = updatedUser,
                            updateSuccessMessage = "Profile updated successfully!"
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Log.e(TAG, "Profile update failed: ${result.message} [Code: ${result.code}]")
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            generalError = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isUpdating = true) }
                }
            }
        }
    }
}
