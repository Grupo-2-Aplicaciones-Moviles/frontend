package weTech.weRide.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import weTech.weRide.ui.components.WeRideBackTopAppBar
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.components.WeRideScaffold
import weTech.weRide.ui.theme.WeRideTheme

/**
 * Verification Code Screen
 * Step 2 of 3 in registration flow
 */
@Composable
fun VerificationScreen(
    phoneNumber: String,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToProfileSetup: () -> Unit
) {
    var code by remember { mutableStateOf(List(6) { "" }) }
    var isLoading by remember { mutableStateOf(false) }
    var resendTimer by remember { mutableStateOf(60) }
    val focusRequesters = List(6) { remember { FocusRequester() } }

    // Auto-focus first field
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    // Resend timer
    LaunchedEffect(Unit) {
        while (resendTimer > 0) {
            delay(1000)
            resendTimer--
        }
    }

    // Auto-submit when code is complete
    LaunchedEffect(code) {
        if (code.all { it.length == 1 }) {
            delay(300)
            isLoading = true
            // TODO: Verify code with backend
            // For now, auto-navigate
            onNavigateToProfileSetup()
        }
    }

    WeRideScaffold(
        topBar = {
            WeRideBackTopAppBar(
                title = "Verificar código",
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
            ProgressIndicator(currentStep = 2, totalSteps = 3)

            Spacer(modifier = Modifier.height(32.dp))

            // Instructions
            Text(
                text = "Ingresa el código de 6 dígitos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enviado a $phoneNumber",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Code Input Fields
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(6) { index ->
                    CodeInputField(
                        value = code[index],
                        onValueChange = { newValue ->
                            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                val newCode = code.toMutableList()
                                newCode[index] = newValue
                                code = newCode

                                // Auto-focus next field
                                if (newValue.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        onFocusChange = { focused ->
                            // Handle backspace
                            if (!focused && code[index].isEmpty() && index > 0) {
                                focusRequesters[index - 1].requestFocus()
                            }
                        },
                        focusRequester = focusRequesters[index],
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resend Button
            if (resendTimer > 0) {
                Text(
                    text = "Reenviar código en ${resendTimer}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                weTech.weRide.ui.components.WeRideTextButton(
                    onClick = {
                        resendTimer = 60
                        // TODO: Resend code
                    },
                    text = "Reenviar código"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Verify Button
            WeRideButton(
                onClick = {
                    isLoading = true
                    // TODO: Verify code with backend
                    onNavigateToProfileSetup()
                },
                text = "Verificar",
                enabled = code.all { it.length == 1 },
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Single code input field
 */
@Composable
private fun CodeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .height(56.dp)
            .border(
                width = 2.dp,
                color = if (isFocused) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                },
                shape = MaterialTheme.shapes.medium
            )
            .background(
                if (isFocused) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                } else {
                    Color.Transparent
                },
                shape = MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    onFocusChange(focusState.isFocused)
                },
            textStyle = MaterialTheme.typography.displaySmall.copy(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationScreenPreview() {
    WeRideTheme {
        VerificationScreen(
            phoneNumber = "+51 999 999 999",
            onNavigateBack = {},
            onNavigateToProfileSetup = {}
        )
    }
}
