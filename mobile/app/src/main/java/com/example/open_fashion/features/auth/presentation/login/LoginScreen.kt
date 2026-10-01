package com.example.open_fashion.features.auth.presentation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.open_fashion.core.storage.TokenManager
import com.example.open_fashion.features.auth.data.repository.AuthRepositoryImpl
import com.example.open_fashion.ui.theme.*

/**
 * Stateful Root Entry Point for Customer Login Screen.
 */
@Preview
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val viewModel: LoginViewModel = viewModel {
        LoginViewModel(
            authRepository = AuthRepositoryImpl(),
            tokenManager = tokenManager
        )
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // React to successful login
    LaunchedEffect(state.authSession) {
        if (state.authSession != null) {
            onLoginSuccess()
        }
    }

    // React to server error messages
    LaunchedEffect(state.generalError) {
        state.generalError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
            viewModel.onIntent(LoginUiIntent.OnDismissError)
        }
    }

    LoginScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
        onNavigateToRegister = onNavigateToRegister
    )
}

/**
 * Pure Stateless Composable rendering the luxury customer login form.
 */
@Composable
fun LoginScreenContent(
    state: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (LoginUiIntent) -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

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
                text = "SIGN IN TO YOUR ACCOUNT",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                ),
                color = TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(44.dp))

            // --- 1. Email Field ---
            OutlinedTextField(
                value = state.email,
                onValueChange = { onIntent(LoginUiIntent.OnEmailChanged(it)) },
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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                colors = luxuryTextFieldColors(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- 2. Password Field ---
            OutlinedTextField(
                value = state.password,
                onValueChange = { onIntent(LoginUiIntent.OnPasswordChanged(it)) },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { onIntent(LoginUiIntent.OnTogglePasswordVisibility) }) {
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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (state.isSubmitEnabled) onIntent(LoginUiIntent.OnSubmitLogin)
                }),
                colors = luxuryTextFieldColors(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // --- 3. Submit Button (Luxury Champagne Accent) ---
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onIntent(LoginUiIntent.OnSubmitLogin)
                },
                enabled = state.isSubmitEnabled,
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
                        text = "SIGN IN",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- 4. Navigation Link to Register ---
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryLight
                )
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = AccentGold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Custom luxury color palette for OutlinedTextField inputs.
 */
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
