package weTech.weRide.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.koin.androidx.compose.koinViewModel
import weTech.weRide.ui.navigation.Screen
import weTech.weRide.ui.screens.main.garage.*
import weTech.weRide.ui.components.SkeletonVehicleCard
import weTech.weRide.ui.theme.EnergyGreen

/**
 * Garage Screen - Vehicle Catalog
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun GarageScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: GarageViewModel = koinViewModel()
) {
    val vehicles by viewModel.filteredVehicles.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteVehicleIds.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var viewType by remember { mutableStateOf(ViewType.GRID)} // GRID or LIST

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Garaje") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search and filter bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onFilterClick = { showFilterSheet = true },
                    viewType = viewType,
                    onViewTypeChange = { viewType = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                // Vehicle count
                if (vehicles.isNotEmpty()) {
                    Text(
                        text = "${vehicles.size} vehículos disponibles",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Vehicle list/grid
                if (isLoading && vehicles.isEmpty()) {
                    // Skeleton loading state
                    if (viewType == ViewType.GRID) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(6) {
                                SkeletonVehicleCard()
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(6) {
                                SkeletonVehicleCard()
                            }
                        }
                    }
                } else if (vehicles.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No hay vehículos disponibles",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Intenta con otros filtros",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    val filteredList = if (searchQuery.isNotBlank()) {
                        viewModel.searchVehicles(searchQuery)
                    } else {
                        vehicles
                    }

                    if (viewType == ViewType.GRID) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredList) { vehicle ->
                                CompactVehicleCard(
                                    vehicle = vehicle,
                                    isFavorite = favoriteIds.contains(vehicle.id),
                                    onFavoriteClick = { viewModel.toggleFavorite(vehicle.id) },
                                    onClick = {
                                        navController.navigate(
                                            Screen.VehicleDetail.createRoute(vehicle.id)
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredList.size) { index ->
                                VehicleCard(
                                    vehicle = filteredList[index],
                                    isFavorite = favoriteIds.contains(filteredList[index].id),
                                    onFavoriteClick = { viewModel.toggleFavorite(filteredList[index].id) },
                                    onClick = {
                                        navController.navigate(
                                            Screen.VehicleDetail.createRoute(filteredList[index].id)
                                        )
                                    }
                                )
                            }
                        }
                    }
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
                            Text("Reintentar")
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

            // Filter bottom sheet
            if (showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showFilterSheet = false }
                ) {
                    GarageFilterSheet(
                        selectedType = selectedType,
                        sortOption = sortOption,
                        onTypeSelected = { viewModel.updateTypeFilter(it) },
                        onSortOptionChange = { viewModel.updateSortOption(it) },
                        onClearFilters = {
                            viewModel.clearFilters()
                            showFilterSheet = false
                        },
                        onDismiss = { showFilterSheet = false }
                    )
                }
            }
        }
    }
}

/**
 * Search bar for garage
 */
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    viewType: ViewType,
    onViewTypeChange: (ViewType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Buscar vehículos...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        IconButton(onClick = onFilterClick) {
            Icon(Icons.Default.FilterList, contentDescription = "Filtros")
        }

        // View type toggle
        IconButton(onClick = { onViewTypeChange(if (viewType == ViewType.GRID) ViewType.LIST else ViewType.GRID) }) {
            Icon(
                imageVector = if (viewType == ViewType.GRID) Icons.Default.ViewList else Icons.Default.ViewModule,
                contentDescription = "Cambiar vista"
            )
        }
    }
}

/**
 * Garage filter sheet
 */
@Composable
private fun GarageFilterSheet(
    selectedType: String?,
    sortOption: GarageViewModel.SortOption,
    onTypeSelected: (String?) -> Unit,
    onSortOptionChange: (GarageViewModel.SortOption) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = "Filtros",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Type filter
        Text(
            text = "Tipo de vehículo",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        val vehicleTypes = listOf(
            "Todos" to null,
            "Scooter" to "scooter",
            "Bicicleta" to "bike",
            "Motocicleta" to "motorcycle"
        )

        vehicleTypes.forEach { (label, value) ->
            FilterChip(
                selected = selectedType == value,
                onClick = { onTypeSelected(value) },
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sort options
        Text(
            text = "Ordenar por",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        val sortOptions = listOf(
            "Cercanía" to GarageViewModel.SortOption.NEARBY,
            "Precio: Menor a Mayor" to GarageViewModel.SortOption.PRICE_LOW_TO_HIGH,
            "Precio: Mayor a Menor" to GarageViewModel.SortOption.PRICE_HIGH_TO_LOW,
            "Batería" to GarageViewModel.SortOption.BATTERY_HIGH_TO_LOW,
            "Calificación" to GarageViewModel.SortOption.RATING_HIGH_TO_LOW,
            "Marca A-Z" to GarageViewModel.SortOption.BRAND_AZ
        )

        sortOptions.forEach { (label, value) ->
            FilterChip(
                selected = sortOption == value,
                onClick = { onSortOptionChange(value) },
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onClearFilters,
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpiar")
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("Aplicar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * View type enum
 */
private enum class ViewType {
    GRID, LIST
}
