package com.example.open_fashion.features.profile.presentation

import com.example.open_fashion.features.auth.domain.model.User

/**
 * Single source of truth for the Customer Profile Screen.
 */
data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val isEditSheetOpen: Boolean = false,

    // Edit form fields
    val editName: String = "",
    val editPhone: String = "",
    val nameError: String? = null,

    val generalError: String? = null,
    val updateSuccessMessage: String? = null
)

/**
 * User intents for the Profile Screen.
 */
sealed interface ProfileUiIntent {
    data object OnLoadProfile : ProfileUiIntent
    data object OnOpenEditSheet : ProfileUiIntent
    data object OnDismissEditSheet : ProfileUiIntent
    data class OnEditNameChanged(val name: String) : ProfileUiIntent
    data class OnEditPhoneChanged(val phone: String) : ProfileUiIntent
    data object OnSubmitProfileUpdate : ProfileUiIntent
    data object OnDismissMessage : ProfileUiIntent
}
