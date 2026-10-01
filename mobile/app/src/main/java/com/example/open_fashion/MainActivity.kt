package com.example.open_fashion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.open_fashion.core.navigation.NavRoute
import com.example.open_fashion.core.storage.TokenManager
import com.example.open_fashion.features.auth.presentation.login.LoginScreen
import com.example.open_fashion.features.auth.presentation.register.RegisterScreen
import com.example.open_fashion.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Open_fashionTheme {
                val context = LocalContext.current
                val tokenManager = remember { TokenManager(context) }
                val navController = rememberNavController()

                val startDestination = if (tokenManager.isLoggedIn()) {
                    NavRoute.Home.route
                } else {
                    NavRoute.Login.route
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    // 1. Customer Login Screen
                    composable(NavRoute.Login.route) {
                        LoginScreen(
                            onNavigateToRegister = {
                                navController.navigate(NavRoute.Register.route)
                            },
                            onLoginSuccess = {
                                navController.navigate(NavRoute.Home.route) {
                                    popUpTo(NavRoute.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Customer Registration Screen
                    composable(NavRoute.Register.route) {
                        RegisterScreen(
                            onNavigateToLogin = {
                                navController.popBackStack()
                            },
                            onRegisterSuccess = {
                                navController.navigate(NavRoute.Login.route) {
                                    popUpTo(NavRoute.Register.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 3. Customer Home / Authenticated Placeholder
                    composable(NavRoute.Home.route) {
                        val user = tokenManager.getUser()
                        CustomerHomeScreen(
                            userName = user?.name ?: "Customer",
                            userEmail = user?.email ?: "",
                            onLogout = {
                                tokenManager.clearSession()
                                navController.navigate(NavRoute.Login.route) {
                                    popUpTo(NavRoute.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Luxury Authenticated Customer Home placeholder screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    userName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "OPEN FASHION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = "Sign Out",
                            tint = AccentGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = AccentGold,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome, $userName",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceLight
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "AUTHENTICATED SESSION ACTIVE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = AccentGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "JWT Access & Refresh tokens securely cached in TokenManager. Automatic session recovery is active.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = StatusError
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SIGN OUT")
            }
        }
    }
}