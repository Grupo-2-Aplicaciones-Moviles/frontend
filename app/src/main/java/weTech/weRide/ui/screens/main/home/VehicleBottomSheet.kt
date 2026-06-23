package weTech.weRide.ui.screens.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.ui.components.RatingBar
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.theme.EnergyGreen
import weTech.weRide.ui.theme.LightGray
import weTech.weRide.ui.theme.White

/**
 * Vehicle Bottom Sheet for displaying vehicle details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleBottomSheet(
    vehicle: VehicleResource,
    distance: Int? = null,
    walkingTime: Int? = null,
    onNavigate: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        sheetContent = {
            VehicleSheetContent(
                vehicle = vehicle,
                distance = distance,
                walkingTime = walkingTime,
                onNavigate = onNavigate,
                onClose = onClose
            )
        },
        sheetPeekHeight = 280.dp,
        scaffoldState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetShadowElevation = 8.dp,
        sheetTonalElevation = 8.dp,
        modifier = modifier
    ) { paddingValues ->
        // Main content (map) goes here
        Box(modifier = Modifier.padding(paddingValues))
    }
}

/**
 * Vehicle sheet content
 */
@Composable
fun VehicleSheetContent(
    vehicle: VehicleResource,
    distance: Int? = null,
    walkingTime: Int? = null,
    onNavigate: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(LightGray)
            )
        }

        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(EnergyGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (vehicle.type.lowercase()) {
                            "scooter" -> Icons.Default.ElectricScooter
                            "bike" -> Icons.Default.DirectionsBike
                            "motorcycle" -> Icons.Default.TwoWheeler
                            else -> Icons.Default.ElectricScooter
                        },
                        contentDescription = vehicle.type,
                        tint = EnergyGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "${vehicle.brand} ${vehicle.model}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = vehicle.getTypeDisplayName(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Close button
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar"
                )
            }
        }

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Battery
            StatItem(
                icon = Icons.Default.BatteryFull,
                label = "Batería",
                value = "${vehicle.getBatteryPercent()}%",
                color = when {
                    vehicle.battery >= 50 -> EnergyGreen
                    vehicle.battery >= 20 -> Color(0xFFFFC107)
                    else -> Color(0xFFF44336)
                }
            )

            // Range
            StatItem(
                icon = Icons.Default.Route,
                label = "Rango",
                value = "${vehicle.range} km",
                color = MaterialTheme.colorScheme.primary
            )

            // Speed
            StatItem(
                icon = Icons.Default.Speed,
                label = "Velocidad",
                value = "${vehicle.maxSpeed} km/h",
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Distance and walking time
        if (distance != null || walkingTime != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                distance?.let {
                    DistanceItem(
                        icon = Icons.Default.LocationOn,
                        label = "Distancia",
                        value = LocationUtils.formatDistance(it)
                    )
                }

                walkingTime?.let {
                    DistanceItem(
                        icon = Icons.Default.DirectionsWalk,
                        label = "Caminando",
                        value = LocationUtils.formatWalkingTime(it)
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

        // Price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Precio por minuto",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "S/ ${String.format("%.2f", vehicle.pricePerMinute)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EnergyGreen
            )
        }

        // Reserve button
        WeRideButton(
            text = "Reservar ahora",
            onClick = onNavigate,
            modifier = Modifier.fillMaxWidth()
        )

        // License plate
        Text(
            text = "Placa: ${vehicle.licensePlate}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * Stat item for vehicle stats
 */
@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

/**
 * Distance item for distance and walking time
 */
@Composable
private fun DistanceItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
