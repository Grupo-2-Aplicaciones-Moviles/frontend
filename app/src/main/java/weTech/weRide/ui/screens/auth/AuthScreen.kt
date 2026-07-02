package weTech.weRide.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.components.WeRideOutlinedButton
import weTech.weRide.ui.components.WeRidePasswordField
import weTech.weRide.ui.components.WeRideTextField
import weTech.weRide.ui.theme.EnergyGreen
import weTech.weRide.ui.theme.WeRideTheme

/**
 * Authentication Screen
 * Handles both login and registration with tabs
 */
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Iniciar Sesión", "Registrarse")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Logo
        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
            contentDescription = "WeRide Logo",
            modifier = Modifier.size(80.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(EnergyGreen)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = EnergyGreen
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Form Content
        when (selectedTab) {
            0 -> LoginForm(
                viewModel = viewModel,
                onNavigateToHome = onNavigateToHome
            )
            1 -> RegisterForm(
                viewModel = viewModel,
                onNavigateToHome = onNavigateToHome
            )
        }
    }
}

/**
 * Login Form
 */
@Composable
private fun LoginForm(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState = viewModel.loginState
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeRideTextField(
            value = username,
            onValueChange = { username = it },
            label = "Usuario",
            placeholder = "testuser1"
        )

        Spacer(modifier = Modifier.height(16.dp))

        WeRidePasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            imeAction = androidx.compose.ui.text.input.ImeAction.Done
        )

        if (loginState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = loginState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        WeRideButton(
            onClick = {
                coroutineScope.launch {
                    val success = viewModel.signIn(username, password)
                    if (success) {
                        onNavigateToHome()
                    }
                }
            },
            text = "Iniciar Sesión",
            enabled = username.isNotEmpty() && password.isNotEmpty() && loginState !is AuthState.Loading,
            isLoading = loginState is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign In Button
        WeRideOutlinedButton(
            onClick = { /* TODO: Implement Google Sign In */ },
            text = "Continuar con Google"
        )
    }
}

/**
 * Register Form
 */
@Composable
private fun RegisterForm(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswordError by remember { mutableStateOf(false) }

    val registerState = viewModel.registerState
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeRideTextField(
            value = username,
            onValueChange = { username = it },
            label = "Usuario",
            placeholder = "Elige un nombre de usuario"
        )

        Spacer(modifier = Modifier.height(16.dp))

        WeRidePasswordField(
            value = password,
            onValueChange = {
                password = it
                showPasswordError = false
            },
            label = "Contraseña"
        )

        Spacer(modifier = Modifier.height(16.dp))

        WeRidePasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirmar Contraseña"
        )

        if (showPasswordError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Las contraseñas no coinciden",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (registerState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = registerState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        WeRideButton(
            onClick = {
                if (password != confirmPassword) {
                    showPasswordError = true
                } else {
                    coroutineScope.launch {
                        val success = viewModel.signUp(username, password)
                        if (success) {
                            onNavigateToHome()
                        }
                    }
                }
            },
            text = "Registrarse",
            enabled = username.isNotEmpty() &&
                     password.isNotEmpty() &&
                     confirmPassword.isNotEmpty() &&
                     registerState !is AuthState.Loading,
            isLoading = registerState is AuthState.Loading
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    WeRideTheme {
        AuthScreen(
            onNavigateToHome = {}
        )
    }
}
