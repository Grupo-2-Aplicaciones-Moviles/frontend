package weTech.weRide.ui.screens.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.ui.theme.BikeBlue
import weTech.weRide.ui.theme.EnergyGreen
import weTech.weRide.ui.theme.MotorcycleOrange
import weTech.weRide.ui.theme.ScooterGreen
import weTech.weRide.ui.theme.White

/**
 * Vehicle marker icon for map
 */
@Composable
fun VehicleMarkerIcon(
    vehicle: VehicleResource,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (vehicle.type.lowercase()) {
        "scooter" -> Icons.Default.ElectricMoped to ScooterGreen
        "bike" -> Icons.Default.DirectionsBike to BikeBlue
        "motorcycle" -> Icons.Default.Motorcycle to MotorcycleOrange
        else -> Icons.Default.ElectricBike to EnergyGreen
    }

    val backgroundColor = if (isSelected) EnergyGreen else color
    val size = if (isSelected) 48.dp else 40.dp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (isSelected) {
                    Modifier.size(size + 8.dp)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = vehicle.type,
            tint = White,
            modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
        )
    }
}

/**
 * Vehicle info card for marker info window
 */
@Composable
fun VehicleInfoCard(
    vehicle: VehicleResource,
    distance: Int? = null,
    walkingTime: Int? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Brand and Model
        Text(
            text = "${vehicle.brand} ${vehicle.model}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        // Type
        Text(
            text = vehicle.getTypeDisplayName(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Divider
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )

        // Battery
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Batería",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${vehicle.getBatteryPercent()}%",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = when {
                    vehicle.battery >= 50 -> EnergyGreen
                    vehicle.battery >= 20 -> Color(0xFFFFC107)
                    else -> Color(0xFFF44336)
                }
            )
        }

        // Distance
        distance?.let {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Distancia",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = LocationUtils.formatDistance(it),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Walking time
        walkingTime?.let {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Caminando",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = LocationUtils.formatWalkingTime(it),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Price
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Precio",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "S/ ${String.format("%.2f", vehicle.pricePerMinute)}/min",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = EnergyGreen
            )
        }
    }
}

/**
 * Simple marker label for battery percentage
 */
@Composable
fun VehicleBatteryLabel(
    battery: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                when {
                    battery >= 50 -> EnergyGreen
                    battery >= 20 -> Color(0xFFFFC107)
                    else -> Color(0xFFF44336)
                }
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$battery%",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = White,
            fontWeight = FontWeight.Bold
        )
    }
}
