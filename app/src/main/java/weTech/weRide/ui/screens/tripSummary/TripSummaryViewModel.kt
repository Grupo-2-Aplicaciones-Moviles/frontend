package weTech.weRide.ui.screens.tripSummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import weTech.weRide.data.models.bookings.BookingResource
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.data.repository.BookingRepository
import weTech.weRide.data.repository.VehicleRepository
import weTech.weRide.utils.Resource

/**
 * ViewModel for Trip Summary Screen
 * Manages trip summary data and navigation to rating
 */
class TripSummaryViewModel(
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    // Booking and Vehicle
    private val _booking = MutableStateFlow<BookingResource?>(null)
    val booking: StateFlow<BookingResource?> = _booking.asStateFlow()

    private val _vehicle = MutableStateFlow<VehicleResource?>(null)
    val vehicle: StateFlow<VehicleResource?> = _vehicle.asStateFlow()

    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Trip statistics
    private val _tripStats = MutableStateFlow<TripStats?>(null)
    val tripStats: StateFlow<TripStats?> = _tripStats.asStateFlow()

    /**
     * Initialize trip summary with booking ID
     */
    fun initializeTripSummary(bookingId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch booking details
                when (val result = bookingRepository.getBookingById(bookingId)) {
                    is Resource.Success -> {
                        _booking.value = result.data
                        result.data.vehicleId?.let { vehicleId ->
                            fetchVehicleDetails(vehicleId.toString())
                        }

                        // Calculate trip statistics from booking data
                        _tripStats.value = calculateTripStats(result.data)
                    }
                    is Resource.Error -> {
                        _error.value = result.message
                    }
                    else -> {
                        // Loading
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load trip summary"
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
     * Calculate trip statistics from booking
     */
    private fun calculateTripStats(booking: BookingResource): TripStats {
        val duration = booking.duration ?: 0
        val distance = booking.distance ?: 0.0
        val totalCost = booking.totalCost ?: 0.0
        val discount = booking.discount ?: 0.0
        val finalCost = booking.finalCost ?: totalCost

        return TripStats(
            durationMinutes = duration,
            distanceKm = distance,
            averageSpeed = booking.averageSpeed ?: 0.0,
            baseCost = totalCost,
            discount = discount,
            finalCost = finalCost,
            paymentMethod = booking.paymentMethod ?: "N/A",
            paymentStatus = booking.paymentStatus ?: "pending"
        )
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }
}

/**
 * Trip statistics data class
 */
data class TripStats(
    val durationMinutes: Int,
    val distanceKm: Double,
    val averageSpeed: Double,
    val baseCost: Double,
    val discount: Double,
    val finalCost: Double,
    val paymentMethod: String,
    val paymentStatus: String
) {
    val formattedDuration: String
        get() {
            val hours = durationMinutes / 60
            val mins = durationMinutes % 60
            return when {
                hours > 0 -> "${hours}h ${mins}min"
                else -> "${mins}min"
            }
        }

    val formattedDistance: String
        get() = String.format("%.2f km", distanceKm)

    val formattedAverageSpeed: String
        get() = String.format("%.1f km/h", averageSpeed)

    val formattedBaseCost: String
        get() = String.format("S/ %.2f", baseCost)

    val formattedDiscount: String
        get() = String.format("S/ %.2f", discount)

    val formattedFinalCost: String
        get() = String.format("S/ %.2f", finalCost)

    val paymentMethodDisplayName: String
        get() = when (paymentMethod.lowercase()) {
            "card" -> "Tarjeta"
            "yape" -> "Yape"
            "plin" -> "Plin"
            else -> paymentMethod
        }

    val paymentStatusDisplayName: String
        get() = when (paymentStatus.lowercase()) {
            "pending" -> "Pendiente"
            "paid" -> "Pagado"
            "failed" -> "Fallido"
            else -> paymentStatus
        }

    val isPaid: Boolean
        get() = paymentStatus.lowercase() == "paid"
}
