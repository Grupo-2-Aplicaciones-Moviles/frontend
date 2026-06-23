package weTech.weRide.ui.screens.activeTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import weTech.weRide.data.models.bookings.BookingResource
import weTech.weRide.data.models.bookings.ReportProblemRequest
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.data.repository.BookingRepository
import weTech.weRide.data.repository.VehicleRepository
import weTech.weRide.utils.Resource
import java.time.LocalDateTime
import java.time.Duration

/**
 * ViewModel for Active Trip Screen
 * Manages trip statistics, location tracking, and problem reporting
 */
class ActiveTripViewModel(
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    // Trip state
    private val _tripState = MutableStateFlow<TripState>(TripState.Idle)
    val tripState: StateFlow<TripState> = _tripState.asStateFlow()

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

    // Problem reporting state
    private val _showProblemDialog = MutableStateFlow(false)
    val showProblemDialog: StateFlow<Boolean> = _showProblemDialog.asStateFlow()

    private val _selectedProblemCategory = MutableStateFlow<ProblemCategory?>(null)
    val selectedProblemCategory: StateFlow<ProblemCategory?> = _selectedProblemCategory.asStateFlow()

    private val _problemDescription = MutableStateFlow("")
    val problemDescription: StateFlow<String> = _problemDescription.asStateFlow()

    private val _isSubmittingProblem = MutableStateFlow(false)
    val isSubmittingProblem: StateFlow<Boolean> = _isSubmittingProblem.asStateFlow()

    // Timer job for trip duration
    private var timerJob: Job? = null

    /**
     * Initialize trip with booking ID
     */
    fun initializeTrip(bookingId: Long) {
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

                        // Calculate trip duration from actual start date
                        val actualStartDate = result.data.actualStartDate
                        if (actualStartDate != null) {
                            startTripTimer(actualStartDate)
                        }

                        _tripState.value = TripState.Active()
                    }
                    is Resource.Error -> {
                        _error.value = result.message
                        _tripState.value = TripState.Error(result.message ?: "Unknown error")
                    }
                    else -> {
                        // Loading
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
                _tripState.value = TripState.Error(e.message ?: "Failed to load trip")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetch vehicle details
     */
    private suspend fun fetchVehicleDetails(vehicleId: String) {
        withContext(Dispatchers.IO) {
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
    }

    /**
     * Start trip duration timer
     */
    private fun startTripTimer(startDate: String) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                try {
                    val start = LocalDateTime.parse(startDate)
                    val now = LocalDateTime.now()
                    val duration = Duration.between(start, now)
                    val seconds = duration.seconds

                    val current = _tripState.value as? TripState.Active ?: TripState.Active()
                    _tripState.value = current.copy(
                        durationSeconds = seconds,
                        distanceKm = calculateDistance(seconds),
                        averageSpeed = calculateAverageSpeed(seconds)
                    )
                } catch (e: Exception) {
                    // Parse error, ignore
                }
                delay(1000)
            }
        }
    }

    /**
     * Calculate estimated distance (mock calculation)
     * In real app, this would come from GPS/vehicle data
     */
    private fun calculateDistance(durationSeconds: Long): Double {
        // Mock: assume average speed of 15 km/h for scooters
        val hours = durationSeconds / 3600.0
        return hours * 15.0
    }

    /**
     * Calculate average speed
     */
    private fun calculateAverageSpeed(durationSeconds: Long): Double {
        val distance = calculateDistance(durationSeconds)
        val hours = durationSeconds / 3600.0
        return if (hours > 0) distance / hours else 0.0
    }

    /**
     * Update current battery level (mock, would come from vehicle API)
     */
    fun updateBatteryLevel(battery: Int) {
        val current = _tripState.value
        if (current is TripState.Active) {
            _tripState.value = current.copy(batteryLevel = battery.coerceIn(0, 100))
        }
    }

    /**
     * Update current speed (mock, would come from vehicle GPS)
     */
    fun updateCurrentSpeed(speed: Double) {
        val current = _tripState.value
        if (current is TripState.Active) {
            _tripState.value = current.copy(currentSpeed = speed)
        }
    }

    /**
     * Update current location (would come from GPS)
     */
    fun updateCurrentLocation(latitude: Double, longitude: Double) {
        val current = _tripState.value
        if (current is TripState.Active) {
            _tripState.value = current.copy(
                currentLocation = Pair(latitude, longitude)
            )
        }
    }

    /**
     * Show problem dialog
     */
    fun showProblemReportDialog() {
        _showProblemDialog.value = true
    }

    /**
     * Hide problem dialog
     */
    fun hideProblemReportDialog() {
        _showProblemDialog.value = false
        _selectedProblemCategory.value = null
        _problemDescription.value = ""
    }

    /**
     * Select problem category
     */
    fun selectProblemCategory(category: ProblemCategory) {
        _selectedProblemCategory.value = category
    }

    /**
     * Update problem description
     */
    fun updateProblemDescription(description: String) {
        _problemDescription.value = description
    }

    /**
     * Submit problem report
     */
    fun submitProblem() {
        val booking = _booking.value ?: return
        val category = _selectedProblemCategory.value?.displayName ?: return
        val description = _problemDescription.value

        if (description.isBlank()) {
            _error.value = "Por favor describe el problema"
            return
        }

        viewModelScope.launch {
            _isSubmittingProblem.value = true
            try {
                val request = ReportProblemRequest(
                    bookingId = booking.id ?: booking.bookingId,
                    category = category,
                    description = description
                )

                // TODO: Implement problem submission API when available
                // For now, simulate success
                delay(500)

                _showProblemDialog.value = false
                _selectedProblemCategory.value = null
                _problemDescription.value = ""
                _error.value = null

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al reportar problema"
            } finally {
                _isSubmittingProblem.value = false
            }
        }
    }

    /**
     * End trip
     */
    fun endTrip() {
        timerJob?.cancel()
        _tripState.value = TripState.Completed
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

/**
 * Trip state sealed class
 */
sealed class TripState {
    data object Idle : TripState()
    data object Loading : TripState()
    data class Active(
        val batteryLevel: Int = 100,
        val currentSpeed: Double = 0.0,
        val durationSeconds: Long = 0,
        val distanceKm: Double = 0.0,
        val averageSpeed: Double = 0.0,
        val currentLocation: Pair<Double, Double>? = null
    ) : TripState() {
        val formattedDuration: String
            get() {
                val hours = durationSeconds / 3600
                val minutes = (durationSeconds % 3600) / 60
                val seconds = durationSeconds % 60
                return when {
                    hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
                    else -> String.format("%02d:%02d", minutes, seconds)
                }
            }

        val formattedDistance: String
            get() = String.format("%.2f km", distanceKm)

        val formattedAverageSpeed: String
            get() = String.format("%.1f km/h", averageSpeed)

        val estimatedCost: Double
            get() = (durationSeconds / 60.0) * 0.50 // S/0.50 per minute

        val formattedEstimatedCost: String
            get() = String.format("S/ %.2f", estimatedCost)
    }

    data class Error(val message: String) : TripState()
    data object Completed : TripState()
}

/**
 * Problem categories
 */
enum class ProblemCategory(val displayName: String, val icon: String) {
    VEHICLE_DAMAGE("Daño al vehículo", "vehicle_damage"),
    BATTERY_ISSUE("Problema de batería", "battery"),
    BRAKE_ISSUE("Problema de frenos", "brake"),
    TIRE_ISSUE("Problema con las llantas", "tire"),
    LOCK_ISSUE("Problema de bloqueo", "lock"),
    OTHER("Otro", "other")
}
