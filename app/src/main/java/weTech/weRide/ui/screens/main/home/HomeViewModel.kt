package weTech.weRide.ui.screens.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.data.repository.VehicleRepository

/**
 * ViewModel for Home Screen
 * Manages vehicle list, user location, and filtering
 */
class HomeViewModel(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    // Vehicles
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

    // User location
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    // Selected vehicle for bottom sheet
    private val _selectedVehicle = MutableStateFlow<VehicleResource?>(null)
    val selectedVehicle: StateFlow<VehicleResource?> = _selectedVehicle.asStateFlow()

    // Active filters
    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    private val _selectedMinBattery = MutableStateFlow(0)
    val selectedMinBattery: StateFlow<Int> = _selectedMinBattery.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

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
                    applyFilters()
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
     * Update user location
     */
    fun updateUserLocation(location: LatLng) {
        _userLocation.value = location
    }

    /**
     * Select vehicle for bottom sheet
     */
    fun selectVehicle(vehicle: VehicleResource?) {
        _selectedVehicle.value = vehicle
    }

    /**
     * Update vehicle type filter
     */
    fun updateTypeFilter(type: String?) {
        _selectedType.value = type
        applyFilters()
    }

    /**
     * Update minimum battery filter
     */
    fun updateMinBatteryFilter(battery: Int) {
        _selectedMinBattery.value = battery
        applyFilters()
    }

    /**
     * Update search query
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    /**
     * Clear all filters
     */
    fun clearFilters() {
        _selectedType.value = null
        _selectedMinBattery.value = 0
        _searchQuery.value = ""
        applyFilters()
    }

    /**
     * Apply all active filters
     */
    private fun applyFilters() {
        var filtered = _vehicles.value

        // Filter by type
        _selectedType.value?.let { type ->
            filtered = filtered.filter { it.type.equals(type, ignoreCase = true) }
        }

        // Filter by battery
        if (_selectedMinBattery.value > 0) {
            filtered = filtered.filter { it.battery >= _selectedMinBattery.value }
        }

        // Filter by search query
        if (_searchQuery.value.isNotEmpty()) {
            val query = _searchQuery.value.lowercase()
            filtered = filtered.filter {
                it.brand.lowercase().contains(query) ||
                it.model.lowercase().contains(query) ||
                it.licensePlate.lowercase().contains(query) ||
                it.location.lowercase().contains(query)
            }
        }

        // Only show available vehicles
        filtered = filtered.filter { it.isAvailable() }

        _filteredVehicles.value = filtered
    }

    /**
     * Get nearby vehicles within a radius
     */
    fun getNearbyVehicles(radiusKm: Double = 2.0): List<VehicleResource> {
        val userLoc = _userLocation.value ?: return emptyList()

        return _filteredVehicles.value.filter { vehicle ->
            val vehicleLoc = parseLocationToLatLng(vehicle.location)
            val distance = calculateDistance(userLoc, vehicleLoc)
            distance <= radiusKm
        }.sortedBy { vehicle ->
            val vehicleLoc = parseLocationToLatLng(vehicle.location)
            calculateDistance(userLoc, vehicleLoc)
        }
    }

    /**
     * Parse location string to LatLng
     * Expected format: "latitude,longitude" or from API
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
     * Calculate distance between two points in km
     */
    private fun calculateDistance(from: LatLng, to: LatLng): Double {
        val r = 6371 // Earth's radius in km
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(from.latitude)) * Math.cos(Math.toRadians(to.latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    /**
     * Get distance to vehicle in meters
     */
    fun getDistanceToVehicle(vehicle: VehicleResource): Int? {
        val userLoc = _userLocation.value ?: return null
        val vehicleLoc = parseLocationToLatLng(vehicle.location)
        val distanceKm = calculateDistance(userLoc, vehicleLoc)
        return (distanceKm * 1000).toInt()
    }

    /**
     * Get walking time to vehicle (approximate 5km/h walking speed)
     */
    fun getWalkingTimeToVehicle(vehicle: VehicleResource): Int? {
        val distance = getDistanceToVehicle(vehicle) ?: return null
        return (distance / 83).toInt() // 83 meters per minute ~ 5km/h
    }

    /**
     * Dismiss error
     */
    fun dismissError() {
        _error.value = null
    }
}
