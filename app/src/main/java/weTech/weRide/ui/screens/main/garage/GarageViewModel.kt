package weTech.weRide.ui.screens.main.garage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.data.repository.VehicleRepository

/**
 * ViewModel for Garage Screen
 * Manages vehicle list, filtering, and favorites
 */
class GarageViewModel(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    // All vehicles
    private val _vehicles = MutableStateFlow<List<VehicleResource>>(emptyList())
    val vehicles: StateFlow<List<VehicleResource>> = _vehicles.asStateFlow()

    // Filtered vehicles
    private val _filteredVehicles = MutableStateFlow<List<VehicleResource>>(emptyList())
    val filteredVehicles: StateFlow<List<VehicleResource>> = _filteredVehicles.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Selected vehicle type filter
    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    // Favorite vehicles
    private val _favoriteVehicleIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteVehicleIds: StateFlow<Set<String>> = _favoriteVehicleIds.asStateFlow()

    // Sort option
    private val _sortOption = MutableStateFlow<SortOption>(SortOption.NEARBY)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    init {
        loadVehicles()
    }

    /**
     * Load all vehicles from API
     */
    fun loadVehicles() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = vehicleRepository.getAllVehicles()) {
                is weTech.weRide.utils.Resource.Success -> {
                    _vehicles.value = result.data ?: emptyList()
                    applyFiltersAndSort()
                    _isLoading.value = false
                }
                is weTech.weRide.utils.Resource.Error -> {
                    _error.value = result.message
                    _isLoading.value = false
                }
                else -> {
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Update vehicle type filter
     */
    fun updateTypeFilter(type: String?) {
        _selectedType.value = type
        applyFiltersAndSort()
    }

    /**
     * Toggle favorite status
     */
    fun toggleFavorite(vehicleId: String) {
        val currentFavorites = _favoriteVehicleIds.value.toMutableSet()
        if (currentFavorites.contains(vehicleId)) {
            currentFavorites.remove(vehicleId)
        } else {
            currentFavorites.add(vehicleId)
        }
        _favoriteVehicleIds.value = currentFavorites
    }

    /**
     * Update sort option
     */
    fun updateSortOption(sort: SortOption) {
        _sortOption.value = sort
        applyFiltersAndSort()
    }

    /**
     * Check if vehicle is favorite
     */
    fun isFavorite(vehicleId: String): Boolean {
        return _favoriteVehicleIds.value.contains(vehicleId)
    }

    /**
     * Get only favorite vehicles
     */
    fun getFavoriteVehicles(): List<VehicleResource> {
        return _vehicles.value.filter { vehicle ->
            _favoriteVehicleIds.value.contains(vehicle.id)
        }
    }

    /**
     * Clear all filters
     */
    fun clearFilters() {
        _selectedType.value = null
        _sortOption.value = SortOption.NEARBY
        applyFiltersAndSort()
    }

    /**
     * Apply filters and sort
     */
    private fun applyFiltersAndSort() {
        var filtered = _vehicles.value

        // Filter by type
        _selectedType.value?.let { type ->
            filtered = filtered.filter { it.type.equals(type, ignoreCase = true) }
        }

        // Only show available vehicles
        filtered = filtered.filter { it.isAvailable() }

        // Apply sorting
        filtered = when (_sortOption.value) {
            SortOption.NEARBY -> filtered // Would require user location
            SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.pricePerMinute }
            SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.pricePerMinute }
            SortOption.BATTERY_HIGH_TO_LOW -> filtered.sortedByDescending { it.battery }
            SortOption.RATING_HIGH_TO_LOW -> filtered.sortedByDescending { it.rating ?: 0.0 }
            SortOption.BRAND_AZ -> filtered.sortedBy { "${it.brand} ${it.model}" }
        }

        _filteredVehicles.value = filtered
    }

    /**
     * Get vehicles by type
     */
    fun getVehiclesByType(type: String): List<VehicleResource> {
        return _vehicles.value.filter { it.type.equals(type, ignoreCase = true) }
    }

    /**
     * Search vehicles by query
     */
    fun searchVehicles(query: String): List<VehicleResource> {
        if (query.isBlank()) return _filteredVehicles.value

        val lowerQuery = query.lowercase()
        return _filteredVehicles.value.filter { vehicle ->
            vehicle.brand.lowercase().contains(lowerQuery) ||
            vehicle.model.lowercase().contains(lowerQuery) ||
            vehicle.licensePlate.lowercase().contains(lowerQuery) ||
            vehicle.location.lowercase().contains(lowerQuery)
        }
    }

    /**
     * Dismiss error
     */
    fun dismissError() {
        _error.value = null
    }

    /**
     * Sort options
     */
    enum class SortOption {
        NEARBY,
        PRICE_LOW_TO_HIGH,
        PRICE_HIGH_TO_LOW,
        BATTERY_HIGH_TO_LOW,
        RATING_HIGH_TO_LOW,
        BRAND_AZ
    }
}
