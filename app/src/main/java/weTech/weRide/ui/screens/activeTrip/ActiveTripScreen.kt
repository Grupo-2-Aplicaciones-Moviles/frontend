package weTech.weRide.ui.screens.activeTrip

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
import androidx.compose.ui.graphics.vector.ImageVector
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
 * Active Trip Screen
 * Shows real-time trip statistics, map, and controls
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTripScreen(
    navController: NavController,
    bookingId: Long,
    viewModel: ActiveTripViewModel = koinViewModel()
) {
    val tripState by viewModel.tripState.collectAsStateWithLifecycle()
    val booking by viewModel.booking.collectAsStateWithLifecycle()
    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val showProblemDialog by viewModel.showProblemDialog.collectAsStateWithLifecycle()
    val selectedProblemCategory by viewModel.selectedProblemCategory.collectAsStateWithLifecycle()
    val problemDescription by viewModel.problemDescription.collectAsStateWithLifecycle()
    val isSubmittingProblem by viewModel.isSubmittingProblem.collectAsStateWithLifecycle()

    // Initialize trip on first composition
    LaunchedEffect(bookingId) {
        viewModel.initializeTrip(bookingId)
    }

    // Handle back press - confirm before exiting
    var showExitDialog by remember { mutableStateOf(false) }
    BackHandler(enabled = true) {
        if (tripState is TripState.Active) {
            showExitDialog = true
        } else {
            navController.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Viaje en curso") },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = { viewModel.showProblemReportDialog() }) {
                        Icon(Icons.Default.ReportProblem, contentDescription = "Reportar problema")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && tripState is TripState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EnergyGreen)
                    }
                }

                error != null && tripState !is TripState.Active -> {
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

                tripState is TripState.Active -> {
                    ActiveTripContent(
                        tripState = tripState as TripState.Active,
                        booking = booking,
                        vehicle = vehicle,
                        onEndTrip = {
                            viewModel.endTrip()
                            navController.navigate(
                                Screen.TripSummary.createRoute(bookingId)
                            ) {
                                popUpTo(Screen.Home.route) { saveState = true }
                            }
                        },
                        onReportProblem = { viewModel.showProblemReportDialog() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Problem report dialog
        if (showProblemDialog) {
            ProblemReportDialog(
                selectedCategory = selectedProblemCategory,
                description = problemDescription,
                isSubmitting = isSubmittingProblem,
                onCategorySelected = { viewModel.selectProblemCategory(it) },
                onDescriptionChange = { viewModel.updateProblemDescription(it) },
                onSubmit = { viewModel.submitProblem() },
                onDismiss = { viewModel.hideProblemReportDialog() }
            )
        }

        // Exit confirmation dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("¿Salir del viaje?") },
                text = { Text("El viaje sigue en curso. ¿Seguro que deseas salir?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            navController.navigateUp()
                        }
                    ) {
                        Text("Salir", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Continuar viaje")
                    }
                }
            )
        }
    }
}

/**
 * Active trip content with map and statistics
 */
@Composable
fun ActiveTripContent(
    tripState: TripState.Active,
    booking: weTech.weRide.data.models.bookings.BookingResource?,
    vehicle: weTech.weRide.data.models.vehicles.VehicleResource?,
    onEndTrip: () -> Unit,
    onReportProblem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        // Map placeholder (would use Google Maps in production)
        MapPlaceholder(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
        )

        // Trip statistics
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Duration and distance row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.AccessTime,
                    label = "Duración",
                    value = tripState.formattedDuration,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.DirectionsCar,
                    label = "Distancia",
                    value = tripState.formattedDistance,
                    modifier = Modifier.weight(1f)
                )
            }

            // Speed and battery row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.Speed,
                    label = "Velocidad actual",
                    value = String.format("%.0f km/h", tripState.currentSpeed),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.BatteryFull,
                    label = "Batería",
                    value = "${tripState.batteryLevel}%",
                    valueColor = getBatteryColor(tripState.batteryLevel),
                    modifier = Modifier.weight(1f)
                )
            }

            // Vehicle info card
            vehicle?.let {
                VehicleTripCard(vehicle = it)
            }

            // Estimated cost card
            CostCard(
                estimatedCost = tripState.formattedEstimatedCost,
                durationMinutes = (tripState.durationSeconds / 60).toInt()
            )

            // Action buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeRideButton(
                    text = "Finalizar viaje",
                    onClick = onEndTrip,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = onReportProblem,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.ReportProblem,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Reportar problema")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Map placeholder (would be Google Maps in production)
 */
@Composable
fun MapPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFE8F5E9)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = EnergyGreen.copy(alpha = 0.5f)
            )
            Text(
                text = "Mapa del viaje",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Statistics card
 */
@Composable
fun StatCard(
    icon: ImageVector,
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
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

/**
 * Vehicle trip card
 */
@Composable
fun VehicleTripCard(
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
 * Cost card
 */
@Composable
fun CostCard(
    estimatedCost: String,
    durationMinutes: Int,
    modifier: Modifier = Modifier
) {
    WeRideCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = EnergyGreen.copy(alpha = 0.1f)
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
                    text = "Costo estimado",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$durationMinutes min × S/ 0.50/min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = estimatedCost,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnergyGreen
            )
        }
    }
}

/**
 * Problem report dialog
 */
@Composable
fun ProblemReportDialog(
    selectedCategory: ProblemCategory?,
    description: String,
    isSubmitting: Boolean,
    onCategorySelected: (ProblemCategory) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar problema") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Selecciona el tipo de problema:",
                    style = MaterialTheme.typography.labelMedium
                )

                // Problem categories
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProblemCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { onCategorySelected(category) },
                            label = { Text(category.displayName) },
                            leadingIcon = if (selectedCategory == category) {
                                {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null
                        )
                    }
                }

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Describe el problema") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = selectedCategory != null && description.isNotBlank() && !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Enviar reporte")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Get type icon
 */
private fun getTypeIcon(type: String): ImageVector {
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
