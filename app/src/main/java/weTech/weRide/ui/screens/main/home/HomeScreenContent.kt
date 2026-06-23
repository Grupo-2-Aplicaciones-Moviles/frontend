package weTech.weRide.ui.screens.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.launch
import weTech.weRide.ui.components.SkeletonMap
import weTech.weRide.ui.theme.EnergyGreen

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

    // Camera position
    val cameraPositionState = rememberCameraPositionState {
        LocationUtils.getDefaultCameraPosition()
    }

    // Map properties
    val mapProperties by remember(selectedVehicle) {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = LocationUtils.hasLocationPermissions(context),
                mapType = MapType.NORMAL
            )
        )
    }

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = true,
                mapToolbarEnabled = false
            )
        )
    }

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

    // Update camera when user location is available
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(location, 15f)
            )
        }
    }

    // Show bottom sheet when vehicle is selected
    LaunchedEffect(selectedVehicle) {
        showVehicleBottomSheet = selectedVehicle != null
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Map
        GoogleMap(
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            onMapLoaded = { /* Map loaded */ },
            modifier = Modifier.fillMaxSize()
        ) {
            // Vehicle markers will be added here
            vehicles.forEach { vehicle ->
                val vehicleLocation = parseLocationToLatLng(vehicle.location)
                Marker(
                    state = rememberMarkerState(position = vehicleLocation),
                    title = "${vehicle.brand} ${vehicle.model}",
                    snippet = "Batería: ${vehicle.battery}%",
                    onClick = {
                        viewModel.selectVehicle(vehicle)
                        showVehicleBottomSheet = true
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(vehicleLocation, 17f)
                            )
                        }
                        true
                    }
                )
            }

            // User location marker
            userLocation?.let { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Tu ubicación"
                )
            }
        }

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
                    .padding(16.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = "${vehicles.size} vehículos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
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
