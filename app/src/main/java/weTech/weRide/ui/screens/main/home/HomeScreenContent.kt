package weTech.weRide.ui.screens.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.launch
import weTech.weRide.ui.components.SkeletonMap
import weTech.weRide.ui.components.StaticMap
import weTech.weRide.ui.theme.*

/**
 * Home Screen with interactive map and vehicle markers
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onVehicleClick: (String) -> Unit,
    onNavigateToReservation: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    // State
    val vehicles by viewModel.filteredVehicles.collectAsStateWithLifecycle()
    val selectedVehicle by viewModel.selectedVehicle.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val selectedMinBattery by viewModel.selectedMinBattery.collectAsStateWithLifecycle()

    // UI state
    var showFilterSheet by remember { mutableStateOf(false) }
    var showVehicleBottomSheet by remember { mutableStateOf(false) }

    // Location permissions
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = LocationUtils.getLocationPermissions().toList()
    )

    // Location callback for updates
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    viewModel.updateUserLocation(LatLng(location.latitude, location.longitude))
                }
            }
        }
    }

    // Handle lifecycle events
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (locationPermissions.allPermissionsGranted) {
                        LocationUtils.requestLocationUpdates(context, locationCallback)
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    LocationUtils.stopLocationUpdates(context, locationCallback)
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            LocationUtils.stopLocationUpdates(context, locationCallback)
        }
    }

    // Initial location fetch
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            getUserLocation(context, locationCallback, viewModel)
        } else if (!locationPermissions.allPermissionsGranted && locationPermissions.shouldShowRationale) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }

    // Show bottom sheet when vehicle is selected
    LaunchedEffect(selectedVehicle) {
        showVehicleBottomSheet = selectedVehicle != null
    }

    // View mode state
    var showVehicleList by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        // Map center - use user location or default
        val mapCenter = userLocation ?: LocationUtils.getDefaultLocation()

        // Static Map using Geoapify API
        StaticMap(
            center = mapCenter,
            vehicles = vehicles,
            userLocation = userLocation,
            modifier = Modifier.fillMaxSize(),
            zoom = 15,
            apiKey = "19cc9e8a03e74f38a34c2698d0cf30e0",
            onVehicleClick = { vehicle ->
                viewModel.selectVehicle(vehicle)
                showVehicleBottomSheet = true
            }
        )

        // Search bar
        HomeSearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            onClearQuery = { viewModel.updateSearchQuery("") },
            onFilterClick = { showFilterSheet = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        )

        // My location button
        IconButton(
            onClick = {
                if (locationPermissions.allPermissionsGranted) {
                    getUserLocation(context, locationCallback, viewModel)
                } else {
                    locationPermissions.launchMultiplePermissionRequest()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Mi ubicación",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Vehicle list preview at bottom (shows first few vehicles)
        if (vehicles.isNotEmpty() && !showVehicleBottomSheet) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 80.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vehicles.take(3)) { vehicle ->
                        VehiclePreviewCard(
                            vehicle = vehicle,
                            onClick = {
                                viewModel.selectVehicle(vehicle)
                                showVehicleBottomSheet = true
                            }
                        )
                    }
                }
            }
        }

        // Loading indicator with skeleton map
        if (isLoading && vehicles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                SkeletonMap()
            }
        }

        // Vehicle count badge
        if (vehicles.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .zIndex(1f),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "${vehicles.size} vehículos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        // Empty state message
        if (!isLoading && vehicles.isEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "No hay vehículos disponibles",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Intenta con otros filtros o busca en otra área",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Error message
        error?.let { errorMessage ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.loadVehicles() }) {
                        Text(
                            text = "Reintentar",
                            color = EnergyGreen
                        )
                    }
                },
                dismissAction = {
                    TextButton(onClick = { viewModel.dismissError() }) {
                        Text("Cerrar")
                    }
                }
            ) {
                Text(errorMessage)
            }
        }

        // Filter modal bottom sheet
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                FilterSheetContent(
                    selectedType = selectedType,
                    selectedMinBattery = selectedMinBattery,
                    onTypeSelected = { viewModel.updateTypeFilter(it) },
                    onMinBatteryChanged = { viewModel.updateMinBatteryFilter(it) },
                    onClearFilters = {
                        viewModel.clearFilters()
                        showFilterSheet = false
                    },
                    onApply = { showFilterSheet = false },
                    onDismiss = { showFilterSheet = false }
                )
            }
        }

        // Vehicle bottom sheet
        if (showVehicleBottomSheet && selectedVehicle != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.selectVehicle(null)
                    showVehicleBottomSheet = false
                },
                sheetState = rememberModalBottomSheetState()
            ) {
                VehicleSheetContent(
                    vehicle = selectedVehicle!!,
                    distance = viewModel.getDistanceToVehicle(selectedVehicle!!),
                    walkingTime = viewModel.getWalkingTimeToVehicle(selectedVehicle!!),
                    onNavigate = {
                        onNavigateToReservation(selectedVehicle!!.id)
                        showVehicleBottomSheet = false
                    },
                    onClose = {
                        viewModel.selectVehicle(null)
                        showVehicleBottomSheet = false
                    }
                )
            }
        }
    }
}

/**
 * Get user location and update ViewModel
 */
private fun getUserLocation(
    context: android.content.Context,
    locationCallback: LocationCallback,
    viewModel: HomeViewModel
) {
    LocationUtils.getLastKnownLocation(
        context,
        onSuccess = { viewModel.updateUserLocation(it) },
        onFailure = {
            LocationUtils.requestLocationUpdates(context, locationCallback)
        }
    )
}

/**
 * Parse location string to LatLng
 */
private fun parseLocationToLatLng(location: String): LatLng {
    return try {
        val parts = location.split(",")
        if (parts.size == 2) {
            LatLng(parts[0].trim().toDouble(), parts[1].trim().toDouble())
        } else {
            // Default location if parsing fails (Lima, Peru)
            LatLng(-12.0464, -77.0429)
        }
    } catch (e: Exception) {
        LatLng(-12.0464, -77.0429)
    }
}

/**
 * Vehicle preview card for the bottom list view
 */
@Composable
private fun VehiclePreviewCard(
    vehicle: weTech.weRide.data.models.vehicles.VehicleResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (vehicle.type.lowercase()) {
                            "scooter" -> ScooterGreen.copy(alpha = 0.1f)
                            "bike" -> BikeBlue.copy(alpha = 0.1f)
                            "motorcycle" -> MotorcycleOrange.copy(alpha = 0.1f)
                            else -> EnergyGreen.copy(alpha = 0.1f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (vehicle.type.lowercase()) {
                        "scooter" -> Icons.Default.ElectricMoped
                        "bike" -> Icons.Default.DirectionsBike
                        "motorcycle" -> Icons.Default.Motorcycle
                        else -> Icons.Default.ElectricMoped
                    },
                    contentDescription = vehicle.type,
                    tint = when (vehicle.type.lowercase()) {
                        "scooter" -> ScooterGreen
                        "bike" -> BikeBlue
                        "motorcycle" -> MotorcycleOrange
                        else -> EnergyGreen
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            // Vehicle info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${vehicle.brand} ${vehicle.model}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Battery
                    Text(
                        text = "${vehicle.battery}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            vehicle.battery >= 50 -> EnergyGreen
                            vehicle.battery >= 20 -> Color(0xFFFFC107)
                            else -> Color(0xFFF44336)
                        }
                    )
                    // Range
                    Text(
                        text = "${vehicle.range}km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Price and rating
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "S/${String.format("%.2f", vehicle.pricePerMinute)}/min",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = EnergyGreen
                )
                vehicle.rating?.let {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = vehicle.getRatingFormatted(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
