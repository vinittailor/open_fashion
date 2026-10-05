package com.example.open_fashion.features.profile.presentation

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.open_fashion.core.network.ApiClient
import com.example.open_fashion.core.storage.TokenManager
import com.example.open_fashion.features.auth.domain.model.User
import com.example.open_fashion.features.media.data.repository.FileRepositoryImpl
import com.example.open_fashion.features.profile.data.repository.UserRepositoryImpl
import com.example.open_fashion.ui.components.*
import com.example.open_fashion.ui.theme.*

/**
 * Stateful Root Composable for Customer Profile Screen.
 */
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val viewModel: ProfileViewModel = viewModel {
        ProfileViewModel(
            userRepository = UserRepositoryImpl(),
            fileRepository = FileRepositoryImpl(
                context = context.applicationContext,
                fileApiService = ApiClient.create()
            ),
            tokenManager = tokenManager
        )
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Android 13+ Modern Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            viewModel.onIntent(ProfileUiIntent.OnAvatarSelected(selectedUri))
        }
    }

    LaunchedEffect(state.updateSuccessMessage) {
        state.updateSuccessMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.onIntent(ProfileUiIntent.OnDismissMessage)
        }
    }

    LaunchedEffect(state.generalError) {
        state.generalError?.let { err ->
            snackbarHostState.showSnackbar(err)
            viewModel.onIntent(ProfileUiIntent.OnDismissMessage)
        }
    }

    ProfileContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
        onPickAvatar = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onNavigateBack = onNavigateBack,
        onLogout = onLogout
    )
}

/**
 * Stateless UI Content Composable (allows instant @Preview rendering).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    state: ProfileUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onIntent: (ProfileUiIntent) -> Unit = {},
    onPickAvatar: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MY PROFILE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val user = state.user
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Luxury Customer Avatar with Camera Badge & Coil Loading
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, AccentGold, CircleShape)
                        .background(AccentGoldLight)
                        .clickable(enabled = !state.isUploadingAvatar, onClick = onPickAvatar),
                    contentAlignment = Alignment.Center
                ) {
                    if (!state.avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(state.avatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "User Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = if (!user?.name.isNullOrBlank()) user!!.name.first().uppercase() else "U",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCharcoal
                            )
                        )
                    }

                    // Translucent upload progress overlay
                    if (state.isUploadingAvatar) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = AccentGold,
                                strokeWidth = 2.5.dp
                            )
                        }
                    }
                }

                // Architectural Camera Badge in bottom-right corner
                Surface(
                    shape = CircleShape,
                    color = AccentGold,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !state.isUploadingAvatar, onClick = onPickAvatar)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = "Change Avatar",
                            tint = PrimaryCharcoal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // User Name & Role Pill
            Text(
                text = user?.name ?: "Customer",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Reusable Luxury Role Badge
            LuxuryBadge(
                text = user?.role ?: "CUSTOMER",
                variant = BadgeVariant.GOLD
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Info Cards using Reusable LuxuryCard
            LuxuryCard {
                ProfileInfoRow(
                    icon = Icons.Outlined.Email,
                    label = "EMAIL ADDRESS",
                    value = user?.email ?: "—"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    thickness = 0.8.dp,
                    color = BorderLight
                )
                ProfileInfoRow(
                    icon = Icons.Outlined.Phone,
                    label = "PHONE NUMBER",
                    value = user?.phoneNumber ?: "Not provided"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    thickness = 0.8.dp,
                    color = BorderLight
                )
                ProfileInfoRow(
                    icon = Icons.Outlined.VerifiedUser,
                    label = "EMAIL VERIFICATION",
                    value = if (user?.isEmailVerified == true) "Verified" else "Pending Verification",
                    isVerified = user?.isEmailVerified == true
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 3. Action Buttons using Reusable LuxuryButton
            LuxuryButton(
                text = "EDIT PROFILE",
                variant = ButtonVariant.PRIMARY,
                icon = Icons.Outlined.Edit,
                onClick = { onIntent(ProfileUiIntent.OnOpenEditSheet) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            LuxuryButton(
                text = "SIGN OUT",
                variant = ButtonVariant.DESTRUCTIVE,
                icon = Icons.AutoMirrored.Outlined.Logout,
                onClick = onLogout
            )
        }

        // 4. Edit Profile Bottom Sheet with Reusable Luxury Components
        if (state.isEditSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { onIntent(ProfileUiIntent.OnDismissEditSheet) },
                containerColor = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 36.dp)
                ) {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    LuxuryTextField(
                        value = state.editName,
                        onValueChange = { onIntent(ProfileUiIntent.OnEditNameChanged(it)) },
                        label = "Full Name",
                        leadingIcon = Icons.Outlined.Person,
                        errorMessage = state.nameError
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LuxuryTextField(
                        value = state.editPhone,
                        onValueChange = { onIntent(ProfileUiIntent.OnEditPhoneChanged(it)) },
                        label = "Phone Number",
                        placeholder = "+1 (555) 000-0000",
                        leadingIcon = Icons.Outlined.Phone,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    LuxuryButton(
                        text = "SAVE CHANGES",
                        variant = ButtonVariant.PRIMARY,
                        isLoading = state.isUpdating,
                        onClick = { onIntent(ProfileUiIntent.OnSubmitProfileUpdate) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isVerified: Boolean? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentGoldDark,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                ),
                color = TextSecondaryLight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (isVerified == true) StatusSuccess else TextPrimaryLight
                )
            )
        }
    }
}

// ====================================================================
// Previews (Render instantly in Android Studio Preview tab)
// ====================================================================

@Preview(name = "Customer Profile — Light Theme", showBackground = true)
@Composable
private fun ProfileScreenPreviewLight() {
    Open_fashionTheme(darkTheme = false) {
        ProfileContent(
            state = ProfileUiState(
                user = User(
                    id = "usr_123",
                    name = "Serena Montgomery",
                    email = "serena@openfashion.luxury",
                    phoneNumber = "+1 (555) 019-2834",
                    role = "CUSTOMER",
                    isEmailVerified = true
                )
            )
        )
    }
}

@Preview(name = "Customer Profile — Dark Theme", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileScreenPreviewDark() {
    Open_fashionTheme(darkTheme = true) {
        ProfileContent(
            state = ProfileUiState(
                user = User(
                    id = "usr_123",
                    name = "Serena Montgomery",
                    email = "serena@openfashion.luxury",
                    phoneNumber = "+1 (555) 019-2834",
                    role = "CUSTOMER",
                    isEmailVerified = true
                )
            )
        )
    }
}

@Preview(name = "Customer Profile — Uploading State", showBackground = true)
@Composable
private fun ProfileScreenPreviewUploading() {
    Open_fashionTheme(darkTheme = false) {
        ProfileContent(
            state = ProfileUiState(
                user = User(
                    id = "usr_123",
                    name = "Serena Montgomery",
                    email = "serena@openfashion.luxury"
                ),
                isUploadingAvatar = true
            )
        )
    }
}
