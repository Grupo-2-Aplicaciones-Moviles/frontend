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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.components.WeRideOutlinedButton
import weTech.weRide.ui.components.WeRideEmailField
import weTech.weRide.ui.components.WeRidePasswordField
import weTech.weRide.ui.theme.EnergyGreen
import weTech.weRide.ui.theme.WeRideTheme

/**
 * Authentication Screen
 * Handles both login and registration with tabs
 */
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onNavigateToPhone: () -> Unit
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
            0 -> LoginForm(onNavigateToPhone = onNavigateToPhone)
            1 -> RegisterForm(onNavigateToPhone = onNavigateToPhone)
        }
    }
}

/**
 * Login Form
 */
@Composable
private fun LoginForm(
    onNavigateToPhone: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeRideEmailField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "correo@ejemplo.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        WeRidePasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña"
        )

        Spacer(modifier = Modifier.height(24.dp))

        WeRideButton(
            onClick = {
                isLoading = true
                // TODO: Implement login logic
                // For now, navigate to phone screen
                onNavigateToPhone()
            },
            text = "Iniciar Sesión",
            isLoading = isLoading
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
    onNavigateToPhone: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeRideEmailField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "correo@ejemplo.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        WeRidePasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña"
        )

        Spacer(modifier = Modifier.height(16.dp))

        WeRidePasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirmar Contraseña"
        )

        Spacer(modifier = Modifier.height(24.dp))

        WeRideButton(
            onClick = {
                isLoading = true
                // TODO: Implement registration logic
                // For now, navigate to phone screen
                onNavigateToPhone()
            },
            text = "Registrarse",
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign Up Button
        WeRideOutlinedButton(
            onClick = { /* TODO: Implement Google Sign Up */ },
            text = "Registrarse con Google"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    WeRideTheme {
        AuthScreen(
            onNavigateToPhone = {}
        )
    }
}
