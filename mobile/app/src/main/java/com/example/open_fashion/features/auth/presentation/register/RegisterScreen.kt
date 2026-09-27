package com.example.open_fashion.features.auth.presentation.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.open_fashion.ui.theme.*

/**
 * Stateful Root Entry Point for Customer Registration Screen.
 */
@Preview
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // React to success event
    LaunchedEffect(state.registeredUser) {
        if (state.registeredUser != null) {
            onRegisterSuccess()
        }
    }

    // React to general server error
    LaunchedEffect(state.generalError) {
        state.generalError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
            viewModel.onIntent(RegisterUiIntent.OnDismissError)
        }
    }

    RegisterScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
        onNavigateToLogin = onNavigateToLogin
    )
}

/**
 * Pure Stateless Composable rendering the luxury registration form.
 */

@Composable
fun RegisterScreenContent(
    state: RegisterUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (RegisterUiIntent) -> Unit,
    onNavigateToLogin: () -> Unit,
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
            Spacer(modifier = Modifier.height(16.dp))

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
                text = "CREATE YOUR ACCOUNT",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                ),
                color = TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(36.dp))

            // --- 1. Full Name Field ---
            OutlinedTextField(
                value = state.name,
                onValueChange = { onIntent(RegisterUiIntent.OnNameChanged(it)) },
                label = { Text("Full Name") },
                placeholder = { Text("Eleanor Vance") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                isError = state.nameError != null,
                supportingText = {
                    state.nameError?.let { Text(it, color = StatusError) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                colors = luxuryTextFieldColors(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- 2. Email Field ---
            OutlinedTextField(
                value = state.email,
                onValueChange = { onIntent(RegisterUiIntent.OnEmailChanged(it)) },
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

            Spacer(modifier = Modifier.height(8.dp))

            // --- 3. Password Field ---
            OutlinedTextField(
                value = state.password,
                onValueChange = { onIntent(RegisterUiIntent.OnPasswordChanged(it)) },
                label = { Text("Password") },
                placeholder = { Text("Min 8 chars, 1 upper, 1 lower, 1 symbol") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { onIntent(RegisterUiIntent.OnTogglePasswordVisibility) }) {
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

            Spacer(modifier = Modifier.height(8.dp))

            // --- 4. Phone Number (Optional) ---
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { onIntent(RegisterUiIntent.OnPhoneChanged(it)) },
                label = { Text("Phone Number (Optional)") },
                placeholder = { Text("+1234567890") },
                leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (state.isSubmitEnabled) onIntent(RegisterUiIntent.OnSubmitRegister)
                }),
                colors = luxuryTextFieldColors(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // --- 5. Submit Button (Luxury Champagne Accent) ---
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onIntent(RegisterUiIntent.OnSubmitRegister)
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
                        text = "CREATE ACCOUNT",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 6. Navigation Link to Sign In ---
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryLight
                )
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = AccentGold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
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
