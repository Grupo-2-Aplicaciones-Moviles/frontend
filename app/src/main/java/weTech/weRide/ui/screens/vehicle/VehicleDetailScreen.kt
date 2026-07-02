package weTech.weRide.ui.screens.vehicle

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.ui.components.RatingBar
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.components.WeRideOutlinedButton
import weTech.weRide.ui.navigation.Screen
import weTech.weRide.ui.theme.*

/**
 * Vehicle Detail Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    navController: NavController,
    viewModel: VehicleDetailViewModel = koinViewModel()
) {
    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val currentImageIndex by viewModel.currentImageIndex.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del vehículo") },
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

                vehicle != null -> {
                    VehicleDetailContent(
                        vehicle = vehicle!!,
                        currentImageIndex = currentImageIndex,
                        onPreviousImage = { viewModel.previousImage() },
                        onNextImage = { viewModel.nextImage() },
                        onReserveClick = {
                            navController.navigate(
                                Screen.Reservation.createRoute(vehicle!!.id)
                            )
                        },
                        onScheduleClick = {
                            navController.navigate(
                                Screen.ScheduledUnlock.createRoute(vehicle!!.id)
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Vehicle detail content
 */
@Composable
fun VehicleDetailContent(
    vehicle: VehicleResource,
    currentImageIndex: Int,
    onPreviousImage: () -> Unit,
    onNextImage: () -> Unit,
    onReserveClick: () -> Unit,
    onScheduleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Image gallery
        VehicleImageGallery(
            vehicle = vehicle,
            currentIndex = currentImageIndex,
            onPrevious = onPreviousImage,
            onNext = onNextImage,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        // Title and basic info
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${vehicle.brand} ${vehicle.model}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = vehicle.getTypeDisplayName(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Type badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = getTypeColor(vehicle.type).copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getTypeIcon(vehicle.type),
                            contentDescription = null,
                            tint = getTypeColor(vehicle.type),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = vehicle.getTypeDisplayName(),
                            style = MaterialTheme.typography.labelMedium,
                            color = getTypeColor(vehicle.type),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Rating
            if (vehicle.rating != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RatingBar(
                        rating = vehicle.rating ?: 0.0,
                        starSize = 20
                    )
                    Text(
                        text = vehicle.getRatingFormatted(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Divider
        HorizontalDivider()

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(
                icon = Icons.Default.BatteryFull,
                label = "Batería",
                value = "${vehicle.getBatteryPercent()}%",
                color = getBatteryColor(vehicle.battery),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            StatCard(
                icon = Icons.Default.Route,
                label = "Rango",
                value = "${vehicle.range} km",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            StatCard(
                icon = Icons.Default.Speed,
                label = "Velocidad Máx",
                value = "${vehicle.maxSpeed} km/h",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }

        // Divider
        HorizontalDivider()

        // Specifications
        SpecificationSection(
            title = "Especificaciones",
            items = listOf(
                "Año" to vehicle.year.toString(),
                "Peso" to "${vehicle.weight} kg",
                "Placa" to vehicle.licensePlate,
                "Color" to vehicle.color,
                "Kilometraje" to "${vehicle.totalKilometers?.let { "%.1f".format(it) } ?: "N/A"} km"
            )
        )

        // Features
        vehicle.features?.let { features ->
            if (features.isNotEmpty()) {
                HorizontalDivider()
                SpecificationSection(
                    title = "Características",
                    items = features.map { it to "" }
                )
            }
        }

        // Maintenance status
        vehicle.maintenanceStatus?.let { status ->
            HorizontalDivider()
            MaintenanceStatusCard(status = status)
        }

        // Location
        HorizontalDivider()
        LocationCard(location = vehicle.location)

        // Price
        HorizontalDivider()
        PriceCard(
            pricePerMinute = vehicle.pricePerMinute,
            estimatedCost = vehicle.pricePerMinute * 30 // 30 min example
        )

        // Reserve button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeRideButton(
                text = "Reservar ahora",
                onClick = onReserveClick,
                modifier = Modifier.weight(1f)
            )

            WeRideOutlinedButton(
                text = "Programar",
                onClick = onScheduleClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Vehicle image gallery
 */
@Composable
fun VehicleImageGallery(
    vehicle: VehicleResource,
    currentIndex: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(16.dp))
    ) {
        // Main image
        AsyncImage(
            model = vehicle.image,
            contentDescription = "${vehicle.brand} ${vehicle.model}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Navigation buttons
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Imagen anterior",
                        tint = White
                    )
                }

                IconButton(
                    onClick = onNext,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Siguiente imagen",
                        tint = White
                    )
                }
            }
        }

        // Image indicator
        if (vehicle.image != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${currentIndex + 1} / 1",
                    style = MaterialTheme.typography.labelMedium,
                    color = White
                )
            }
        }
    }
}

/**
 * Stat card
 */
@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

/**
 * Specification section
 */
@Composable
fun SpecificationSection(
    title: String,
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        items.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value.ifEmpty { "Sí" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Maintenance status card
 */
@Composable
fun MaintenanceStatusCard(
    status: String,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status.lowercase()) {
        "good", "bueno" -> SuccessGreen to "Buen estado"
        "fair", "regular" -> WarningOrange to "Revisión necesaria"
        "poor", "malo" -> ErrorRed to "Mantenimiento urgente"
        else -> MaterialTheme.colorScheme.primary to status
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = color
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

/**
 * Location card
 */
@Composable
fun LocationCard(
    location: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = EnergyGreen
            )
            Column {
                Text(
                    text = "Ubicación",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Price card
 */
@Composable
fun PriceCard(
    pricePerMinute: Double,
    estimatedCost: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = EnergyGreen.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Precio por minuto",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "S/ ${String.format("%.2f", pricePerMinute)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnergyGreen
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Costo estimado (30 min)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "S/ ${String.format("%.2f", estimatedCost)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnergyGreen
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
