package weTech.weRide.ui.screens.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import weTech.weRide.data.auth.AuthStateManager
import weTech.weRide.data.models.bookings.CreateBookingRequest
import weTech.weRide.data.models.bookings.BookingResource
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.data.repository.BookingRepository
import weTech.weRide.data.repository.VehicleRepository
import weTech.weRide.utils.Resource
import weTech.weRide.utils.getData
import kotlin.math.max
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ViewModel for Reservation Screen
 */
class ReservationViewModel(
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    // Vehicle
    private val _vehicle = MutableStateFlow<VehicleResource?>(null)
    val vehicle: StateFlow<VehicleResource?> = _vehicle.asStateFlow()

    // Booking
    private val _booking = MutableStateFlow<BookingResource?>(null)
    val booking: StateFlow<BookingResource?> = _booking.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Remaining time (in seconds)
    private val _remainingTime = MutableStateFlow(15 * 60) // 15 minutes
    val remainingTime: StateFlow<Int> = _remainingTime.asStateFlow()

    // Is timer running
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    // Distance to vehicle
    private val _distanceToVehicle = MutableStateFlow<Int?>(null)
    val distanceToVehicle: StateFlow<Int?> = _distanceToVehicle.asStateFlow()

    // Walking time
    private val _walkingTime = MutableStateFlow<Int?>(null)
    val walkingTime: StateFlow<Int?> = _walkingTime.asStateFlow()

    init {
        // Start countdown timer
        startTimer()
    }

    /**
     * Load vehicle and create booking
     */
    fun loadVehicleAndCreateBooking(vehicleId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Load vehicle
            when (val vehicleResult = vehicleRepository.getVehicleById(vehicleId)) {
                is Resource.Success -> {
                    _vehicle.value = vehicleResult.getData()

                    // Create booking
                    createBooking(vehicleId)
                }
                is Resource.Error -> {
                    _error.value = vehicleResult.message
                    _isLoading.value = false
                }
                else -> {
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Create booking draft
     */
    private fun createBooking(vehicleId: String) {
        viewModelScope.launch {
            val userId = authStateManager.getUserId().firstOrNull()?.toLongOrNull()
            if (userId == null) {
                _error.value = "Usuario no autenticado"
                _isLoading.value = false
                return@launch
            }

            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            val request = CreateBookingRequest(
                userId = userId,
                vehicleId = vehicleId.toLong(),
                startDate = now,
                endDate = now
            )

            when (val result = bookingRepository.createBooking(request)) {
                is Resource.Success -> {
                    _booking.value = result.getData()
                    _isLoading.value = false
                    startTimer()
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
     * Start countdown timer
     */
    private fun startTimer() {
        _isTimerRunning.value = true
        viewModelScope.launch {
            while (_remainingTime.value > 0 && _isTimerRunning.value) {
                kotlinx.coroutines.delay(1000L)
                _remainingTime.value = max(0, _remainingTime.value - 1)
            }

            // Timer expired
            if (_remainingTime.value == 0) {
                // Handle expiration
                cancelBooking("Reservation time expired")
            }
        }
    }

    /**
     * Stop timer
     */
    fun stopTimer() {
        _isTimerRunning.value = false
    }

    /**
     * Update distance and walking time
     */
    fun updateDistance(distance: Int?, walkingTime: Int?) {
        _distanceToVehicle.value = distance
        _walkingTime.value = walkingTime
    }

    /**
     * Cancel booking
     */
    fun cancelBooking(reason: String = "User cancelled") {
        viewModelScope.launch {
            _booking.value?.let { booking ->
                bookingRepository.cancelBooking(booking.id ?: booking.bookingId)
                _booking.value = null
                _isTimerRunning.value = false
            }
        }
    }

    /**
     * Confirm booking
     */
    fun confirmBooking() {
        viewModelScope.launch {
            _booking.value?.let { booking ->
                _isLoading.value = true
                val result = bookingRepository.updateBookingStatus(
                    booking.id ?: booking.bookingId,
                    "confirmed"
                )
                when (result) {
                    is Resource.Success -> {
                        _booking.value = result.getData()
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
    }

    /**
     * Get formatted remaining time
     */
    fun getFormattedRemainingTime(): String {
        val minutes = _remainingTime.value / 60
        val seconds = _remainingTime.value % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Check if reservation is about to expire (less than 2 minutes)
     */
    fun isAboutToExpire(): Boolean {
        return _remainingTime.value <= 120 // 2 minutes
    }

    /**
     * Dismiss error
     */
    fun dismissError() {
        _error.value = null
    }
}
