package weTech.weRide.ui.screens.reservation

import androidx.activity.compose.BackHandler
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
import weTech.weRide.ui.navigation.Screen
import weTech.weRide.ui.theme.*

/**
 * Reservation Screen with countdown timer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    navController: NavController,
    vehicleId: String,
    viewModel: ReservationViewModel = koinViewModel()
) {
    // Load vehicle and create booking on first composition
    LaunchedEffect(vehicleId) {
        if (vehicleId.isNotEmpty()) {
            viewModel.loadVehicleAndCreateBooking(vehicleId)
        }
    }

    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()
    val booking by viewModel.booking.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val remainingTime by viewModel.remainingTime.collectAsStateWithLifecycle()
    val distanceToVehicle by viewModel.distanceToVehicle.collectAsStateWithLifecycle()
    val walkingTime by viewModel.walkingTime.collectAsStateWithLifecycle()

    // Handle back press
    BackHandler(enabled = booking != null) {
        viewModel.cancelBooking("User pressed back")
        navController.navigateUp()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservar vehículo") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (booking != null) {
                            viewModel.cancelBooking("User pressed back")
                        }
                        navController.navigateUp()
                    }) {
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = EnergyGreen)
                            Text(
                                text = "Creando reserva...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = error ?: "Error desconocido",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Button(onClick = { navController.navigateUp() }) {
                                Text("Volver")
                            }
                        }
                    }
                }

                vehicle != null && booking != null -> {
                    ReservationContent(
                        vehicle = vehicle!!,
                        bookingId = booking!!.id ?: booking!!.bookingId,
                        remainingTime = remainingTime,
                        formattedTime = viewModel.getFormattedRemainingTime(),
                        isAboutToExpire = viewModel.isAboutToExpire(),
                        distanceToVehicle = distanceToVehicle,
                        walkingTime = walkingTime,
                        onNavigateToQRScanner = {
                            viewModel.stopTimer()
                            navController.navigate(
                                Screen.QRScanner.createRoute(booking!!.id ?: booking!!.bookingId)
                            )
                        },
                        onCancelReservation = {
                            viewModel.cancelBooking("User cancelled")
                            navController.navigateUp()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Reservation content
 */
@Composable
fun ReservationContent(
    vehicle: weTech.weRide.data.models.vehicles.VehicleResource,
    bookingId: Long,
    remainingTime: Int,
    formattedTime: String,
    isAboutToExpire: Boolean,
    distanceToVehicle: Int?,
    walkingTime: Int?,
    onNavigateToQRScanner: () -> Unit,
    onCancelReservation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Countdown timer card
        CountdownCard(
            remainingTime = remainingTime,
            formattedTime = formattedTime,
            isAboutToExpire = isAboutToExpire
        )

        // Vehicle info card
        VehicleInfoCard(vehicle = vehicle)

        // Distance and walking time
        if (distanceToVehicle != null || walkingTime != null) {
            DistanceInfoCard(
                distance = distanceToVehicle,
                walkingTime = walkingTime
            )
        }

        // Instructions
        InstructionsCard()

        // Action buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeRideButton(
                text = "Escanear código QR",
                onClick = onNavigateToQRScanner,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = onCancelReservation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar reserva")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Countdown timer card
 */
@Composable
fun CountdownCard(
    remainingTime: Int,
    formattedTime: String,
    isAboutToExpire: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = when {
        remainingTime <= 60 -> ErrorRed
        isAboutToExpire -> WarningOrange
        else -> EnergyGreen
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = cardColor
                )
                Text(
                    text = "Tiempo restante",
                    style = MaterialTheme.typography.titleMedium,
                    color = cardColor
                )
            }

            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = cardColor
            )

            if (isAboutToExpire) {
                Text(
                    text = "¡Tu reserva está por expirar!",
                    style = MaterialTheme.typography.labelMedium,
                    color = cardColor
                )
            }
        }
    }
}

/**
 * Vehicle info card
 */
@Composable
fun VehicleInfoCard(
    vehicle: weTech.weRide.data.models.vehicles.VehicleResource,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(getTypeColor(vehicle.type).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getTypeIcon(vehicle.type),
                    contentDescription = vehicle.type,
                    tint = getTypeColor(vehicle.type),
                    modifier = Modifier.size(32.dp)
                )
            }

            // Vehicle details
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${vehicle.getBatteryPercent()}% batería",
                        style = MaterialTheme.typography.bodySmall,
                        color = getBatteryColor(vehicle.battery)
                    )
                    Text(
                        text = "${vehicle.range} km rango",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Distance info card
 */
@Composable
fun DistanceInfoCard(
    distance: Int?,
    walkingTime: Int?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            distance?.let {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formatDistance(it),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Distancia",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            walkingTime?.let {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formatWalkingTime(it),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Caminando",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Instructions card
 */
@Composable
fun InstructionsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Instrucciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InstructionStep(
                    number = 1,
                    text = "Camina hacia el vehículo usando la guía de distancia"
                )
                InstructionStep(
                    number = 2,
                    text = "Cuando llegues, escanea el código QR del vehículo"
                )
                InstructionStep(
                    number = 3,
                    text = "Espera a que se desbloquee el vehículo"
                )
                InstructionStep(
                    number = 4,
                    text = "¡Disfruta tu viaje!"
                )
            }
        }
    }
}

/**
 * Instruction step
 */
@Composable
fun InstructionStep(
    number: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(EnergyGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = White,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Format distance for display
 */
private fun formatDistance(meters: Int): String {
    return when {
        meters < 1000 -> "$meters m"
        else -> "${meters / 1000}.${(meters % 1000) / 100} km"
    }
}

/**
 * Format walking time for display
 */
private fun formatWalkingTime(minutes: Int): String {
    return when {
        minutes < 60 -> "$minutes min"
        minutes >= 60 -> "${minutes / 60}h ${minutes % 60}min"
        else -> "${minutes}min"
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

/**
 * Get battery color
 */
private fun getBatteryColor(battery: Int): Color {
    return when {
        battery >= 50 -> SuccessGreen
        battery >= 20 -> WarningOrange
        else -> ErrorRed
    }
}
