package weTech.weRide.ui.screens.vehicle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.data.repository.VehicleRepository
import weTech.weRide.utils.Resource
import weTech.weRide.utils.getData

/**
 * ViewModel for Vehicle Detail Screen
 */
class VehicleDetailViewModel(
    private val vehicleRepository: VehicleRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Vehicle
    private val _vehicle = MutableStateFlow<VehicleResource?>(null)
    val vehicle: StateFlow<VehicleResource?> = _vehicle.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Current image index for gallery
    private val _currentImageIndex = MutableStateFlow(0)
    val currentImageIndex: StateFlow<Int> = _currentImageIndex.asStateFlow()

    init {
        val vehicleId = savedStateHandle.get<String>("vehicleId")
        if (vehicleId != null) {
            loadVehicle(vehicleId)
        } else {
            _error.value = "Vehicle ID not provided"
        }
    }

    /**
     * Load vehicle by ID
     */
    fun loadVehicle(vehicleId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = vehicleRepository.getVehicleById(vehicleId)) {
                is Resource.Success -> {
                    _vehicle.value = result.getData()
                    _isLoading.value = false
                }
                is Resource.Error -> {
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
     * Update current image index
     */
    fun updateImageIndex(index: Int) {
        _currentImageIndex.value = index
    }

    /**
     * Next image in gallery
     */
    fun nextImage() {
        val images = _vehicle.value?.image?.let { listOf(it) } ?: emptyList()
        if (images.isNotEmpty()) {
            _currentImageIndex.value = (_currentImageIndex.value + 1) % images.size
        }
    }

    /**
     * Previous image in gallery
     */
    fun previousImage() {
        val images = _vehicle.value?.image?.let { listOf(it) } ?: emptyList()
        if (images.isNotEmpty()) {
            _currentImageIndex.value = if (_currentImageIndex.value > 0) {
                _currentImageIndex.value - 1
            } else {
                images.size - 1
            }
        }
    }

    /**
     * Get image count
     */
    fun getImageCount(): Int {
        return _vehicle.value?.image?.let { 1 } ?: 0
    }

    /**
     * Dismiss error
     */
    fun dismissError() {
        _error.value = null
    }
}
