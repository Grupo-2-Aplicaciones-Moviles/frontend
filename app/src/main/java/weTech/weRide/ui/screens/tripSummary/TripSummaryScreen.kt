package weTech.weRide.ui.screens.tripSummary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.components.WeRideCard
import weTech.weRide.ui.navigation.Screen
import weTech.weRide.ui.theme.*

/**
 * Trip Summary Screen
 * Shows trip completion details, cost breakdown, and navigation to rating
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSummaryScreen(
    navController: NavController,
    bookingId: Long,
    viewModel: TripSummaryViewModel = koinViewModel()
) {
    val booking by viewModel.booking.collectAsStateWithLifecycle()
    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()
    val tripStats by viewModel.tripStats.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    // Initialize trip summary on first composition
    LaunchedEffect(bookingId) {
        viewModel.initializeTripSummary(bookingId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen del viaje") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EnergyGreen)
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = error ?: "Error desconocido",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { navController.navigateUp() }) {
                                Text("Volver")
                            }
                        }
                    }
                }

                tripStats != null -> {
                    TripSummaryContent(
                        vehicle = vehicle,
                        tripStats = tripStats!!,
                        booking = booking,
                        onRateTrip = {
                            navController.navigate(
                                Screen.Rating.createRoute(bookingId)
                            ) {
                                popUpTo(Screen.Home.route) { saveState = true }
                            }
                        },
                        onGoHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Trip summary content
 */
@Composable
fun TripSummaryContent(
    vehicle: weTech.weRide.data.models.vehicles.VehicleResource?,
    tripStats: TripStats,
    booking: weTech.weRide.data.models.bookings.BookingResource?,
    onRateTrip: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Success header
        SuccessHeader()

        // Trip statistics cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TripStatCard(
                icon = Icons.Default.AccessTime,
                label = "Duración",
                value = tripStats.formattedDuration,
                modifier = Modifier.weight(1f)
            )
            TripStatCard(
                icon = Icons.Default.DirectionsCar,
                label = "Distancia",
                value = tripStats.formattedDistance,
                modifier = Modifier.weight(1f)
            )
        }

        // Average speed and cost
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TripStatCard(
                icon = Icons.Default.Speed,
                label = "Vel. promedio",
                value = tripStats.formattedAverageSpeed,
                modifier = Modifier.weight(1f)
            )
            TripStatCard(
                icon = Icons.Default.AttachMoney,
                label = "Total",
                value = tripStats.formattedFinalCost,
                valueColor = EnergyGreen,
                modifier = Modifier.weight(1f)
            )
        }

        // Vehicle info card
        vehicle?.let {
            VehicleSummaryCard(vehicle = it)
        }

        // Cost breakdown
        CostBreakdownCard(tripStats = tripStats)

        // Payment status
        PaymentStatusCard(
            paymentMethod = tripStats.paymentMethodDisplayName,
            paymentStatus = tripStats.paymentStatusDisplayName,
            isPaid = tripStats.isPaid
        )

        // Action buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeRideButton(
                text = "Calificar viaje",
                onClick = onRateTrip,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = onGoHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir al inicio")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Success header
 */
@Composable
fun SuccessHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SuccessGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(48.dp)
            )
        }
        Text(
            text = "¡Viaje completado!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Gracias por usar WeRide",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Trip statistic card
 */
@Composable
fun TripStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    WeRideCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = valueColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

/**
 * Vehicle summary card
 */
@Composable
fun VehicleSummaryCard(
    vehicle: weTech.weRide.data.models.vehicles.VehicleResource,
    modifier: Modifier = Modifier
) {
    WeRideCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(getTypeColor(vehicle.type).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getTypeIcon(vehicle.type),
                    contentDescription = vehicle.type,
                    tint = getTypeColor(vehicle.type),
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${vehicle.brand} ${vehicle.model}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Placa: ${vehicle.licensePlate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Cost breakdown card
 */
@Composable
fun CostBreakdownCard(
    tripStats: TripStats,
    modifier: Modifier = Modifier
) {
    WeRideCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Desglose de costos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CostRow(
                    label = "Costo base (${tripStats.durationMinutes} min × S/ 0.50)",
                    value = tripStats.formattedBaseCost
                )

                if (tripStats.discount > 0) {
                    CostRow(
                        label = "Descuento",
                        value = "-${tripStats.formattedDiscount}",
                        valueColor = SuccessGreen
                    )
                }

                HorizontalDivider()

                CostRow(
                    label = "Total",
                    value = tripStats.formattedFinalCost,
                    labelStyle = MaterialTheme.typography.titleSmall,
                    valueStyle = MaterialTheme.typography.titleSmall,
                    valueColor = EnergyGreen
                )
            }
        }
    }
}

/**
 * Cost row
 */
@Composable
fun CostRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    labelStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    valueStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = labelStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = valueStyle,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

/**
 * Payment status card
 */
@Composable
fun PaymentStatusCard(
    paymentMethod: String,
    paymentStatus: String,
    isPaid: Boolean,
    modifier: Modifier = Modifier
) {
    WeRideCard(
        modifier = modifier,
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
                    text = "Método de pago",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = paymentMethod,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.Pending,
                    contentDescription = null,
                    tint = if (isPaid) SuccessGreen else WarningOrange
                )
                Text(
                    text = paymentStatus,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isPaid) SuccessGreen else WarningOrange
                )
            }
        }
    }
}

/**
 * Get type icon
 */
private fun getTypeIcon(type: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type.lowercase()) {
        "scooter" -> Icons.Default.ElectricScooter
        "bike" -> Icons.Default.DirectionsBike
        "motorcycle" -> Icons.Default.TwoWheeler
        else -> Icons.Default.ElectricScooter
    }
}

/**
 * Get type color
 */
private fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "scooter" -> ScooterGreen
        "bike" -> BikeBlue
        "motorcycle" -> MotorcycleOrange
        else -> EnergyGreen
    }
}
