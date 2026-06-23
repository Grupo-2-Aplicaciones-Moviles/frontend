package weTech.weRide.ui.screens.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.navigation.Screen
import weTech.weRide.ui.theme.*

/**
 * Payment Screen
 * Handles payment for subscription plans
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    planId: String,
    viewModel: PaymentViewModel = koinViewModel()
) {
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsStateWithLifecycle()
    val cardNumber by viewModel.cardNumber.collectAsStateWithLifecycle()
    val cardHolder by viewModel.cardHolder.collectAsStateWithLifecycle()
    val expiryDate by viewModel.expiryDate.collectAsStateWithLifecycle()
    val cvv by viewModel.cvv.collectAsStateWithLifecycle()
    val yapePhone by viewModel.yapePhone.collectAsStateWithLifecycle()
    val plinPhone by viewModel.plinPhone.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isFormValid by viewModel.isFormValid.collectAsStateWithLifecycle()
    val isPaymentSuccessful by viewModel.isPaymentSuccessful.collectAsStateWithLifecycle()

    // Handle successful payment
    LaunchedEffect(isPaymentSuccessful) {
        if (isPaymentSuccessful) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Método de pago") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Plan summary
            PlanSummaryCard(planId = planId)

            // Payment method tabs
            PaymentMethodTabs(
                selectedMethod = selectedPaymentMethod,
                onMethodSelected = { viewModel.updatePaymentMethod(it) }
            )

            // Payment form
            when (selectedPaymentMethod) {
                PaymentMethod.CARD -> CardPaymentForm(
                    cardNumber = cardNumber,
                    cardHolder = cardHolder,
                    expiryDate = expiryDate,
                    cvv = cvv,
                    onCardNumberChange = { viewModel.updateCardNumber(it) },
                    onCardHolderChange = { viewModel.updateCardHolder(it) },
                    onExpiryDateChange = { viewModel.updateExpiryDate(it) },
                    onCvvChange = { viewModel.updateCvv(it) }
                )
                PaymentMethod.YAPE -> YapePaymentForm(
                    phoneNumber = yapePhone,
                    onPhoneChange = { viewModel.updateYapePhone(it) }
                )
                PaymentMethod.PLIN -> PlinPaymentForm(
                    phoneNumber = plinPhone,
                    onPhoneChange = { viewModel.updatePlinPhone(it) }
                )
            }

            // Error message
            error?.let { errorMessage ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorRed.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorRed
                        )
                    }
                }
            }

            // Submit button
            WeRideButton(
                text = if (isLoading) "Procesando..." else "Pagar ahora",
                onClick = { viewModel.submitPayment() },
                enabled = isFormValid && !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Plan summary card
 */
@Composable
fun PlanSummaryCard(
    planId: String,
    modifier: Modifier = Modifier
) {
    val planName = when (planId) {
        "basic" -> "Básico"
        "standard" -> "Estándar"
        "premium" -> "Premium"
        else -> "Plan seleccionado"
    }
    val planPrice = when (planId) {
        "basic" -> "S/ 9.90"
        "standard" -> "S/ 19.90"
        "premium" -> "S/ 39.90"
        else -> "S/ 0.00"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Plan $planName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Suscripción mensual",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = planPrice,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnergyGreen
            )
        }
    }
}

/**
 * Payment method tabs
 */
@Composable
fun PaymentMethodTabs(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PaymentMethod.entries.forEach { method ->
                PaymentMethodTab(
                    method = method,
                    isSelected = selectedMethod == method,
                    onClick = { onMethodSelected(method) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Individual payment method tab
 */
@Composable
fun PaymentMethodTab(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = method.displayName,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Card payment form
 */
@Composable
fun CardPaymentForm(
    cardNumber: String,
    cardHolder: String,
    expiryDate: String,
    cvv: String,
    onCardNumberChange: (String) -> Unit,
    onCardHolderChange: (String) -> Unit,
    onExpiryDateChange: (String) -> Unit,
    onCvvChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Datos de la tarjeta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = cardNumber,
                onValueChange = onCardNumberChange,
                label = { Text("Número de tarjeta") },
                placeholder = { Text("1234 5678 9012 3456") },
                leadingIcon = {
                    Icon(Icons.Default.CreditCard, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = cardHolder,
                onValueChange = onCardHolderChange,
                label = { Text("Titular de la tarjeta") },
                placeholder = { Text("JUAN PEREZ") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = onExpiryDateChange,
                    label = { Text("Vencimiento") },
                    placeholder = { Text("MM/YY") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = cvv,
                    onValueChange = onCvvChange,
                    label = { Text("CVV") },
                    placeholder = { Text("123") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

/**
 * Yape payment form
 */
@Composable
fun YapePaymentForm(
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF742284).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💜",
                    style = MaterialTheme.typography.displayLarge
                )
                Column {
                    Text(
                        text = "Pagar con Yape",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF742284)
                    )
                    Text(
                        text = "Escanea el QR o ingresa tu número",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                label = { Text("Número de teléfono Yape") },
                placeholder = { Text("999 999 999") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                prefix = {
                    Text(
                        text = "+51 ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            // QR placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Color(0xFF742284),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "QR Yape",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF742284)
                    )
                }
            }
        }
    }
}

/**
 * Plin payment form
 */
@Composable
fun PlinPaymentForm(
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1CC279).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🟢",
                    style = MaterialTheme.typography.displayLarge
                )
                Column {
                    Text(
                        text = "Pagar con Plin",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1CC279)
                    )
                    Text(
                        text = "Escanea el QR o ingresa tu número",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                label = { Text("Número de teléfono Plin") },
                placeholder = { Text("999 999 999") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                prefix = {
                    Text(
                        text = "+51 ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            // QR placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Color(0xFF1CC279),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "QR Plin",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF1CC279)
                    )
                }
            }
        }
    }
}
