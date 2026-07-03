package weTech.weRide.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.navigation.Screen
import weTech.weRide.ui.theme.*

/**
 * Subscription Plan Data
 */
data class SubscriptionPlan(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val duration: String,
    val pricePerMinute: Double,
    val discount: Int,
    val freeMinutes: Int,
    val benefits: List<String>,
    val isPopular: Boolean = false
) {
    val formattedPrice: String
        get() = "S/ $price/mes"

    val formattedPricePerMinute: String
        get() = "S/ $pricePerMinute/min"
}

/**
 * Plans Screen
 * Shows subscription plan options
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    val plans = remember {
        listOf(
            SubscriptionPlan(
                id = "normal",
                name = "Plan Normal",
                description = "Ideal para usuarios ocasionales que buscan una opción económica",
                price = 3.99,
                duration = "Mensual",
                pricePerMinute = 0.6,
                discount = 10,
                freeMinutes = 30,
                benefits = listOf(
                    "Acceso a scooters estándar",
                    "10% de descuento en cada viaje",
                    "Soporte básico al cliente",
                    "30 minutos gratis al mes"
                ),
                isPopular = false
            ),
            SubscriptionPlan(
                id = "student",
                name = "Plan Estudiante",
                description = "Perfecto para estudiantes que necesitan movilidad frecuente",
                price = 5.99,
                duration = "Mensual",
                pricePerMinute = 0.4,
                discount = 20,
                freeMinutes = 60,
                benefits = listOf(
                    "Acceso a scooters premium",
                    "20% de descuento en cada viaje",
                    "Soporte prioritario al cliente",
                    "Viajes ilimitados los fines de semana",
                    "60 minutos gratis al mes"
                ),
                isPopular = true
            ),
            SubscriptionPlan(
                id = "business",
                name = "Plan Business",
                description = "Solución completa para profesionales y empresas",
                price = 9.99,
                duration = "Mensual",
                pricePerMinute = 0.3,
                discount = 30,
                freeMinutes = 120,
                benefits = listOf(
                    "Acceso a todos los vehículos",
                    "30% de descuento en cada viaje",
                    "Soporte prioritario 24/7",
                    "Viajes ilimitados",
                    "120 minutos gratis al mes",
                    "Reportes mensuales",
                    "Facturación centralizada"
                ),
                isPopular = false
            )
        )
    }

    var selectedPlan by remember { mutableStateOf<String?>(null) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planes de suscripción") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            PlansHeader()

            // Plan cards
            plans.forEach { plan ->
                PlanCard(
                    plan = plan,
                    isSelected = selectedPlan == plan.id,
                    onSelectPlan = {
                        selectedPlan = plan.id
                    },
                    onPayClick = {
                        selectedPlan = plan.id
                        showPaymentDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Payment confirmation dialog
    if (showPaymentDialog) {
        PaymentConfirmationDialog(
            plan = plans.first { it.id == selectedPlan },
            onConfirm = {
                showPaymentDialog = false
                // TODO: Navigate to payment screen when ready
                // navController?.navigate(Screen.Payment.createRoute(selectedPlan ?: ""))
            },
            onDismiss = {
                showPaymentDialog = false
            }
        )
    }
}

/**
 * Plans header
 */
@Composable
fun PlansHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Elige tu plan ideal",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Cancela cuando quieras. Sin compromisos.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Plan card
 */
@Composable
fun PlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onSelectPlan: () -> Unit,
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (plan.isPopular) {
                EnergyGreen.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (plan.isPopular) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                EnergyGreen
            )
        } else if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with popular badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = plan.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (plan.isPopular) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EnergyGreen
                    ) {
                        Text(
                            text = "Popular",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.formattedPrice,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Plan details
            PlanDetailRow(label = "Duración", value = plan.duration)
            PlanDetailRow(label = "Precio por minuto", value = plan.formattedPricePerMinute)
            PlanDetailRow(
                label = "Descuento",
                value = "${plan.discount}%",
                valueColor = SuccessGreen
            )
            PlanDetailRow(label = "Minutos gratis al mes", value = "${plan.freeMinutes} min")

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Benefits header
            Text(
                text = "Beneficios:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            // Benefits list
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                plan.benefits.forEach { benefit ->
                    BenefitRow(benefit = benefit)
                }
            }

            // Pay button
            WeRideButton(
                text = "Pagar",
                onClick = onPayClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Plan detail row
 */
@Composable
fun PlanDetailRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

/**
 * Benefit row with checkmark
 */
@Composable
fun BenefitRow(
    benefit: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(SuccessGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = benefit,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Payment confirmation dialog
 */
@Composable
fun PaymentConfirmationDialog(
    plan: SubscriptionPlan,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Suscribirse a ${plan.name}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Se te cobrará ${plan.formattedPrice} mensualmente",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Incluye:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                plan.benefits.take(3).forEach { benefit ->
                    Text(
                        text = "• $benefit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (plan.benefits.size > 3) {
                    Text(
                        text = "• ...y ${plan.benefits.size - 3} beneficios más",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Continuar al pago")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
