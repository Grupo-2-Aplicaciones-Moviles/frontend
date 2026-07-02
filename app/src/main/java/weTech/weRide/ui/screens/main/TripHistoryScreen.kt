package weTech.weRide.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import weTech.weRide.data.models.bookings.BookingResource
import weTech.weRide.ui.screens.main.tripHistory.TripHistoryViewModel
import weTech.weRide.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Trip History Screen
 * Displays user's completed trips with statistics and list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripHistoryScreen(
    viewModel: TripHistoryViewModel = koinViewModel()
) {
    val trips by viewModel.trips.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isEmpty by viewModel.isEmpty.collectAsStateWithLifecycle()

    // Pull to refresh
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis viajes") },
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
                isLoading && trips.isEmpty() -> {
                    // Initial loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EnergyGreen)
                    }
                }

                error != null && trips.isEmpty() -> {
                    // Error state
                    ErrorState(
                        message = error ?: "Error al cargar viajes",
                        onRetry = { viewModel.refresh() }
                    )
                }

                isEmpty -> {
                    // Empty state
                    EmptyState()
                }

                else -> {
                    // Trip list with stats
                    TripHistoryContent(
                        trips = trips,
                        totalTrips = viewModel.getTotalTrips(),
                        totalCost = viewModel.getTotalCost(),
                        totalDistance = viewModel.getTotalDistance(),
                        totalDuration = viewModel.getTotalDuration(),
                        onRefresh = { viewModel.refresh() }
                    )
                }
            }
        }
    }
}

/**
 * Trip history content with stats and list
 */
@Composable
fun TripHistoryContent(
    trips: List<BookingResource>,
    totalTrips: Int,
    totalCost: Double,
    totalDistance: Double,
    totalDuration: Int,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats cards
        item {
            TripStatsRow(
                totalTrips = totalTrips,
                totalCost = totalCost,
                totalDistance = totalDistance,
                totalDuration = totalDuration
            )
        }

        // Section header
        item {
            Text(
                text = "Historial de viajes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Trip list
        items(trips) { trip ->
            TripCard(trip = trip)
        }

        // Refresh button at bottom
        item {
            TextButton(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Actualizar")
            }
        }
    }
}

/**
 * Trip statistics row
 */
@Composable
fun TripStatsRow(
    totalTrips: Int,
    totalCost: Double,
    totalDistance: Double,
    totalDuration: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.Default.DirectionsCar,
            label = "Viajes",
            value = totalTrips.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.AttachMoney,
            label = "Gastado",
            value = "S/${String.format("%.0f", totalCost)}",
            color = EnergyGreen,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.Timer,
            label = "Tiempo",
            value = formatDuration(totalDuration),
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Individual stat card
 */
@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = EnergyGreen
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Trip card
 */
@Composable
fun TripCard(trip: BookingResource) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with date and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatDate(trip.startDate ?: ""),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formatTime(trip.startDate, trip.endDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = getStatusColor(trip.status).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = getStatusText(trip.status),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = getStatusColor(trip.status),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider()

            // Trip details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TripDetailItem(
                    icon = Icons.Default.AccessTime,
                    label = "Duración",
                    value = formatDuration(trip.duration ?: 0)
                )
                TripDetailItem(
                    icon = Icons.Default.Route,
                    label = "Distancia",
                    value = "${String.format("%.1f", trip.distance ?: 0.0)} km"
                )
                TripDetailItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Costo",
                    value = "S/${String.format("%.2f", trip.finalCost ?: 0.0)}",
                    color = EnergyGreen
                )
            }

            // Rating if available
            trip.rating?.score?.let { ratingScore ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < ratingScore) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < ratingScore) StarYellow else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "$ratingScore.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Trip detail item
 */
@Composable
fun TripDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Empty state
 */
@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Text(
                text = "Aún no tienes viajes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Reserva un vehículo para comenzar tu primera aventura",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Error state
 */
@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

/**
 * Format date from ISO string
 */
private fun formatDate(dateString: String): String {
    return try {
        val date = LocalDateTime.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "PE"))
        date.format(formatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    } catch (e: Exception) {
        "Fecha desconocida"
    }
}

/**
 * Format time range from ISO strings
 */
private fun formatTime(startDate: String?, endDate: String?): String {
    return try {
        val start = LocalDateTime.parse(startDate ?: "")
        val end = LocalDateTime.parse(endDate ?: startDate ?: "")
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        "${start.format(formatter)} - ${end.format(formatter)}"
    } catch (e: Exception) {
        "--:--"
    }
}

/**
 * Format duration in minutes to readable format
 */
private fun formatDuration(minutes: Int): String {
    return when {
        minutes < 60 -> "${minutes}m"
        minutes < 1440 -> "${minutes / 60}h ${minutes % 60}m"
        else -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
    }
}

/**
 * Get status text for display
 */
private fun getStatusText(status: String?): String {
    return when (status?.uppercase()) {
        "CONFIRMED" -> "Programado"
        "ACTIVE" -> "En curso"
        "COMPLETED" -> "Completado"
        "CANCELLED" -> "Cancelado"
        "DRAFT" -> "Borrador"
        else -> status ?: "Desconocido"
    }
}

/**
 * Get status color
 */
private fun getStatusColor(status: String?): Color {
    return when (status?.uppercase()) {
        "CONFIRMED" -> InfoBlue
        "ACTIVE" -> EnergyGreen
        "COMPLETED" -> SuccessGreen
        "CANCELLED" -> ErrorRed
        "DRAFT" -> MediumGray
        else -> MediumGray
    }
}
