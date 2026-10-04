package com.example.open_fashion.features.auth.presentation.forgotpassword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.open_fashion.core.constants.AppStrings
import com.example.open_fashion.ui.theme.*

/**
 * Stateful Root Entry Point for Customer Password Recovery Screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit = {},
    onResetSuccess: () -> Unit = {}
) {
    val viewModel: ForgotPasswordViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // React to reset completion: show message and navigate back to login
    LaunchedEffect(state.isResetComplete) {
        if (state.isResetComplete) {
            snackbarHostState.showSnackbar(
                message = state.successMessage ?: "Password has been successfully reset!",
                duration = SnackbarDuration.Short
            )
            onResetSuccess()
        }
    }

    // React to server error messages
    LaunchedEffect(state.generalError) {
        state.generalError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
            viewModel.onIntent(ForgotPasswordUiIntent.OnDismissError)
        }
    }

    ForgotPasswordScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack
    )
}

/**
 * Pure Stateless Composable rendering the 2-step recovery UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreenContent(
    state: ForgotPasswordUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (ForgotPasswordUiIntent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Navigate Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Luxury Brand Header ---
            Text(
                text = "OPEN FASHION",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (state.currentStep == ForgotPasswordStep.ENTER_EMAIL) "PASSWORD RECOVERY" else "SET NEW PASSWORD",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                ),
                color = TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle Description
            Text(
                text = if (state.currentStep == ForgotPasswordStep.ENTER_EMAIL) {
                    "Enter your registered email address and we'll send you a recovery code to reset your password."
                } else {
                    "Enter the verification code sent to ${state.email} along with your new password."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- STEP 1: Enter Email ---
            if (state.currentStep == ForgotPasswordStep.ENTER_EMAIL) {
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onIntent(ForgotPasswordUiIntent.OnEmailChanged(it)) },
                    label = { Text("Email Address") },
                    placeholder = { Text("eleanor@openfashion.com") },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    isError = state.emailError != null,
                    supportingText = {
                        state.emailError?.let { Text(it, color = StatusError) }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        if (state.isStep1SubmitEnabled) onIntent(ForgotPasswordUiIntent.OnSubmitEmail)
                    }),
                    colors = luxuryTextFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onIntent(ForgotPasswordUiIntent.OnSubmitEmail)
                    },
                    enabled = state.isStep1SubmitEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGold,
                        contentColor = PrimaryCharcoal,
                        disabledContainerColor = AccentGoldLight.copy(alpha = 0.5f),
                        disabledContentColor = TextTertiaryLight
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = PrimaryCharcoal,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "SEND RESET CODE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        )
                    }
                }
            }

            // --- STEP 2: Enter OTP & New Password ---
            if (state.currentStep == ForgotPasswordStep.RESET_PASSWORD) {
                // Development Helper Chip (Click to auto-fill OTP in development mode)
                if (!state.devOtp.isNullOrBlank()) {
                    Surface(
                        color = AccentGoldLight.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onIntent(ForgotPasswordUiIntent.OnAutoFillDevOtp) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Dev OTP: ${state.devOtp} (Tap to fill)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = PrimaryCharcoal
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // 1. OTP / Token Input
                OutlinedTextField(
                    value = state.tokenOrOtp,
                    onValueChange = { onIntent(ForgotPasswordUiIntent.OnTokenOrOtpChanged(it)) },
                    label = { Text("Verification Code / OTP") },
                    placeholder = { Text("Enter 6-digit OTP") },
                    leadingIcon = { Icon(Icons.Outlined.Key, contentDescription = null) },
                    isError = state.tokenOrOtpError != null,
                    supportingText = {
                        state.tokenOrOtpError?.let { Text(it, color = StatusError) }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = FocusDirection.Down.let { ImeAction.Next }
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    colors = luxuryTextFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. New Password Input
                OutlinedTextField(
                    value = state.newPassword,
                    onValueChange = { onIntent(ForgotPasswordUiIntent.OnNewPasswordChanged(it)) },
                    label = { Text("New Password") },
                    placeholder = { Text("Min 8 chars with uppercase & symbol") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { onIntent(ForgotPasswordUiIntent.OnTogglePasswordVisibility) }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = state.passwordError != null,
                    supportingText = {
                        state.passwordError?.let { Text(it, color = StatusError) }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    colors = luxuryTextFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Confirm Password Input
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = { onIntent(ForgotPasswordUiIntent.OnConfirmPasswordChanged(it)) },
                    label = { Text("Confirm New Password") },
                    placeholder = { Text("Re-enter your new password") },
                    leadingIcon = { Icon(Icons.Outlined.LockReset, contentDescription = null) },
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = state.confirmPasswordError != null,
                    supportingText = {
                        state.confirmPasswordError?.let { Text(it, color = StatusError) }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        if (state.isStep2SubmitEnabled) onIntent(ForgotPasswordUiIntent.OnSubmitReset)
                    }),
                    colors = luxuryTextFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 4. Submit Reset Button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onIntent(ForgotPasswordUiIntent.OnSubmitReset)
                    },
                    enabled = state.isStep2SubmitEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGold,
                        contentColor = PrimaryCharcoal,
                        disabledContainerColor = AccentGoldLight.copy(alpha = 0.5f),
                        disabledContentColor = TextTertiaryLight
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = PrimaryCharcoal,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "SET NEW PASSWORD",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Link to go back to Step 1
                Text(
                    text = "Entered wrong email? Change email",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = AccentGold,
                    modifier = Modifier.clickable { onIntent(ForgotPasswordUiIntent.OnSwitchToStep1) }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Back to Login link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Remember your password? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryLight
                )
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = AccentGold,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun luxuryTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentGold,
    unfocusedBorderColor = BorderLight,
    focusedLabelColor = AccentGold,
    unfocusedLabelColor = TextSecondaryLight,
    focusedLeadingIconColor = AccentGold,
    unfocusedLeadingIconColor = TextTertiaryLight,
    errorBorderColor = StatusError,
    errorLabelColor = StatusError
)
