package weTech.weRide.ui.screens.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import weTech.weRide.data.models.bookings.BookingResource
import weTech.weRide.data.models.bookings.SubmitRatingRequest
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.data.repository.BookingRepository
import weTech.weRide.data.repository.VehicleRepository
import weTech.weRide.utils.Resource

/**
 * ViewModel for Trip Rating Screen
 * Manages rating submission and booking data
 */
class RatingViewModel(
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    // Booking and Vehicle
    private val _booking = MutableStateFlow<BookingResource?>(null)
    val booking: StateFlow<BookingResource?> = _booking.asStateFlow()

    private val _vehicle = MutableStateFlow<VehicleResource?>(null)
    val vehicle: StateFlow<VehicleResource?> = _vehicle.asStateFlow()

    // Rating state
    private val _selectedRating = MutableStateFlow(0)
    val selectedRating: StateFlow<Int> = _selectedRating.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Success state
    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted: StateFlow<Boolean> = _isSubmitted.asStateFlow()

    /**
     * Initialize rating with booking ID
     */
    fun initializeRating(bookingId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch booking details
                when (val result = bookingRepository.getBookingById(bookingId)) {
                    is Resource.Success -> {
                        _booking.value = result.data

                        // Check if already rated
                        if (result.data.rating != null) {
                            _selectedRating.value = result.data.rating.score ?: 0
                            _comment.value = result.data.rating.comment ?: ""
                        }

                        // Fetch vehicle details
                        result.data.vehicleId?.let { vehicleId ->
                            fetchVehicleDetails(vehicleId.toString())
                        }
                    }
                    is Resource.Error -> {
                        _error.value = result.message
                    }
                    else -> {
                        // Loading
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load booking"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetch vehicle details
     */
    private suspend fun fetchVehicleDetails(vehicleId: String) {
        when (val result = vehicleRepository.getVehicleById(vehicleId)) {
            is Resource.Success -> {
                _vehicle.value = result.data
            }
            is Resource.Error -> {
                _error.value = result.message
            }
            else -> {
                // Loading
            }
        }
    }

    /**
     * Update rating
     */
    fun updateRating(rating: Int) {
        _selectedRating.value = rating.coerceIn(1, 5)
    }

    /**
     * Update comment
     */
    fun updateComment(text: String) {
        _comment.value = text
    }

    /**
     * Submit rating
     */
    fun submitRating() {
        val booking = _booking.value ?: return
        val rating = _selectedRating.value

        if (rating < 1) {
            _error.value = "Por favor selecciona una calificación"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = SubmitRatingRequest(
                    bookingId = booking.id ?: booking.bookingId,
                    score = rating,
                    comment = _comment.value.takeIf { it.isNotBlank() }
                )

                // TODO: Implement rating submission API when available
                // For now, simulate success
                kotlinx.coroutines.delay(500)

                _isSubmitted.value = true
                _error.value = null

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al enviar calificación"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Skip rating
     */
    fun skipRating() {
        _isSubmitted.value = true
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Check if rating is valid for submission
     */
    fun isValidRating(): Boolean {
        return _selectedRating.value >= 1
    }

    /**
     * Get rating label
     */
    fun getRatingLabel(): String {
        return when (_selectedRating.value) {
            1 -> "Malo"
            2 -> "Regular"
            3 -> "Bueno"
            4 -> "Muy bueno"
            5 -> "Excelente"
            else -> ""
        }
    }
}
