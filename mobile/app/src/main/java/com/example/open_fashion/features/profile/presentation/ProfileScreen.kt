package com.example.open_fashion.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.open_fashion.core.storage.TokenManager
import com.example.open_fashion.features.profile.data.repository.UserRepositoryImpl
import com.example.open_fashion.ui.theme.*

/**
 * Stateful Root Composable for Customer Profile Screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
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
            tokenManager = tokenManager
        )
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MY PROFILE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Luxury Customer Avatar
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(AccentGold),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (!user?.name.isNullOrBlank()) user!!.name.first().uppercase() else "U",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCharcoal
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name & Role Pill
            Text(
                text = user?.name ?: "Customer",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = AccentGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = user?.role ?: "CUSTOMER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AccentGold
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Info Cards
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileInfoRow(
                        icon = Icons.Outlined.Email,
                        label = "Email Address",
                        value = user?.email ?: "—"
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = BorderLight)
                    ProfileInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = "Phone Number",
                        value = user?.phoneNumber ?: "Not provided"
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = BorderLight)
                    ProfileInfoRow(
                        icon = Icons.Outlined.VerifiedUser,
                        label = "Email Verified",
                        value = if (user?.isEmailVerified == true) "Verified" else "Pending Verification"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Action Buttons
            Button(
                onClick = { viewModel.onIntent(ProfileUiIntent.OnOpenEditSheet) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGold,
                    contentColor = PrimaryCharcoal
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EDIT PROFILE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusError),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("SIGN OUT")
            }
        }

        // 4. Edit Profile Bottom Sheet
        if (state.isEditSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onIntent(ProfileUiIntent.OnDismissEditSheet) },
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 36.dp)
                ) {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = state.editName,
                        onValueChange = { viewModel.onIntent(ProfileUiIntent.OnEditNameChanged(it)) },
                        label = { Text("Full Name") },
                        isError = state.nameError != null,
                        supportingText = { state.nameError?.let { Text(it, color = StatusError) } },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.editPhone,
                        onValueChange = { viewModel.onIntent(ProfileUiIntent.OnEditPhoneChanged(it)) },
                        label = { Text("Phone Number") },
                        placeholder = { Text("+1234567890") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.onIntent(ProfileUiIntent.OnSubmitProfileUpdate) },
                        enabled = !state.isUpdating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGold,
                            contentColor = PrimaryCharcoal
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (state.isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = PrimaryCharcoal,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("SAVE CHANGES", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentGold,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
        }
    }
}
