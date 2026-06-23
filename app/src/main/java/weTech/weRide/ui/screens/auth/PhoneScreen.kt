package weTech.weRide.ui.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import weTech.weRide.ui.components.WeRideBackTopAppBar
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.components.WeRideScaffold
import weTech.weRide.ui.components.WeRideTextField
import weTech.weRide.ui.theme.WeRideTheme

/**
 * Phone Number Input Screen
 * Step 1 of 3 in registration flow
 */
@Composable
fun PhoneScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToVerification: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    WeRideScaffold(
        topBar = {
            WeRideBackTopAppBar(
                title = "Verificar teléfono",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Progress Indicator
            ProgressIndicator(currentStep = 1, totalSteps = 3)

            Spacer(modifier = Modifier.height(32.dp))

            // Instructions
            Text(
                text = "Ingresa tu número de teléfono",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Te enviaremos un código de verificación",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Country Code
            WeRideTextField(
                value = "+51",
                onValueChange = { },
                label = "Código de país",
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number
            WeRideTextField(
                value = phoneNumber,
                onValueChange = { newValue ->
                    // Only allow digits
                    if (newValue.all { it.isDigit() }) {
                        phoneNumber = newValue
                    }
                },
                label = "Número de teléfono",
                placeholder = "999 999 999",
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            WeRideButton(
                onClick = {
                    isLoading = true
                    // Validate phone number
                    if (phoneNumber.length == 9) {
                        onNavigateToVerification(phoneNumber)
                    }
                    isLoading = false
                },
                text = "Continuar",
                enabled = phoneNumber.length == 9,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Progress Indicator
 * Shows current step in the registration flow
 */
@Composable
fun ProgressIndicator(
    currentStep: Int,
    totalSteps: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { step ->
            val isCompleted = step < currentStep
            val isCurrent = step == currentStep - 1

            Box(
                modifier = Modifier
                    .then(
                        if (isCurrent) {
                            Modifier.padding(end = 8.dp)
                        } else {
                            Modifier
                        }
                    )
            ) {
                if (isCompleted || isCurrent) {
                    Text(
                        text = "●",
                        color = if (isCurrent) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        }
                    )
                } else {
                    Text(
                        text = "○",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhoneScreenPreview() {
    WeRideTheme {
        PhoneScreen(
            onNavigateBack = {},
            onNavigateToVerification = {}
        )
    }
}
