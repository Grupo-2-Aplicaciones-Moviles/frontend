package weTech.weRide.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    val price: Double,
    val duration: String,
    val features: List<String>,
    val isPopular: Boolean = false,
    val discountPercent: Int? = null
) {
    val formattedPrice: String
        get() = "S/ $price"

    val pricePerMonth: Double
        get() = when (duration) {
            "weekly" -> price * 4
            "monthly" -> price
            "yearly" -> price / 12
            else -> price
        }
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
                id = "basic",
                name = "Básico",
                price = 9.90,
                duration = "monthly",
                features = listOf(
                    "50 viajes gratis al mes",
                    "10% de descuento en viajes extra",
                    "Soporte por email",
                    "Sin prioridad en reservas"
                ),
                isPopular = false
            ),
            SubscriptionPlan(
                id = "standard",
                name = "Estándar",
                price = 19.90,
                duration = "monthly",
                features = listOf(
                    "100 viajes gratis al mes",
                    "15% de descuento en viajes extra",
                    "Soporte prioritario",
                    "Reservas con 30 min de anticipación",
                    "Seguro básico incluido"
                ),
                isPopular = true
            ),
            SubscriptionPlan(
                id = "premium",
                name = "Premium",
                price = 39.90,
                duration = "monthly",
                features = listOf(
                    "Viajes ilimitados",
                    "25% de descuento siempre",
                    "Soporte 24/7",
                    "Reservas instantáneas",
                    "Seguro completo incluido",
                    "Vehículos exclusivos",
                    "Sin tarifas de cancelación"
                ),
                isPopular = false,
                discountPercent = 20
            )
        )
    }

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
                    onSelectPlan = {
                        navController?.navigate(Screen.Payment.createRoute(plan.id))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
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
    onSelectPlan: () -> Unit,
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
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S/ ${plan.price}/mes",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
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

            // Discount badge
            plan.discountPercent?.let { discount ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WarningOrange.copy(alpha = 0.2f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Ahorra $discount%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                    }
                }
            }

            // Features
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                plan.features.forEach { feature ->
                    FeatureRow(feature = feature)
                }
            }

            // Select button
            WeRideButton(
                text = if (plan.isPopular) "Seleccionar plan" else "Ver detalles",
                onClick = onSelectPlan,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Feature row with checkmark
 */
@Composable
fun FeatureRow(
    feature: String,
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
            text = feature,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Compare plans banner
 */
@Composable
fun CompareBanner(
    onCompare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = InfoBlue.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Compare,
                    contentDescription = null,
                    tint = InfoBlue
                )
                Text(
                    text = "Comparar planes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}
