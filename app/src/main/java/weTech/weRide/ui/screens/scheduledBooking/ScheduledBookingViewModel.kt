package weTech.weRide.ui.screens.scheduledBooking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import weTech.weRide.data.models.bookings.BookingResource
import weTech.weRide.data.models.bookings.CreateBookingRequest
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.data.repository.BookingRepository
import weTech.weRide.data.repository.VehicleRepository
import weTech.weRide.utils.Resource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Scheduled Booking ViewModel
 * Manages scheduled booking state and operations
 */
class ScheduledBookingViewModel(
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository,
    private val vehicleId: String,
    private val userId: Long
) : ViewModel() {

    // Vehicle state
    private val _vehicle = MutableStateFlow<VehicleResource?>(null)
    val vehicle: StateFlow<VehicleResource?> = _vehicle.asStateFlow()

    private val _isLoadingVehicle = MutableStateFlow(false)
    val isLoadingVehicle: StateFlow<Boolean> = _isLoadingVehicle.asStateFlow()

    private val _vehicleError = MutableStateFlow<String?>(null)
    val vehicleError: StateFlow<String?> = _vehicleError.asStateFlow()

    // Date/Time selection state
    private val _selectedDate = MutableStateFlow<LocalDateTime?>(null)
    val selectedDate: StateFlow<LocalDateTime?> = _selectedDate.asStateFlow()

    private val _selectedDuration = MutableStateFlow(30) // Default 30 minutes
    val selectedDuration: StateFlow<Int> = _selectedDuration.asStateFlow()

    // Availability state
    private val _isCheckingAvailability = MutableStateFlow(false)
    val isCheckingAvailability: StateFlow<Boolean> = _isCheckingAvailability.asStateFlow()

    private val _isAvailable = MutableStateFlow<Boolean?>(null)
    val isAvailable: StateFlow<Boolean?> = _isAvailable.asStateFlow()

    private val _availabilityMessage = MutableStateFlow<String?>(null)
    val availabilityMessage: StateFlow<String?> = _availabilityMessage.asStateFlow()

    // Booking state
    private val _isCreatingBooking = MutableStateFlow(false)
    val isCreatingBooking: StateFlow<Boolean> = _isCreatingBooking.asStateFlow()

    private val _bookingResult = MutableStateFlow<BookingResource?>(null)
    val bookingResult: StateFlow<BookingResource?> = _bookingResult.asStateFlow()

    private val _bookingError = MutableStateFlow<String?>(null)
    val bookingError: StateFlow<String?> = _bookingError.asStateFlow()

    // Form validation
    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    init {
        loadVehicle()
    }

    /**
     * Load vehicle details
     */
    private fun loadVehicle() {
        viewModelScope.launch {
            _isLoadingVehicle.value = true
            _vehicleError.value = null

            when (val result = vehicleRepository.getVehicleById(vehicleId)) {
                is Resource.Success -> {
                    _vehicle.value = result.data
                    _isLoadingVehicle.value = false
                }
                is Resource.Error -> {
                    _vehicleError.value = result.message
                    _isLoadingVehicle.value = false
                }
                else -> {
                    _isLoadingVehicle.value = false
                }
            }
        }
    }

    /**
     * Select date and time
     */
    fun selectDateTime(dateTime: LocalDateTime) {
        _selectedDate.value = dateTime
        _isAvailable.value = null
        _availabilityMessage.value = null
        validateForm()
    }

    /**
     * Select duration in minutes
     */
    fun selectDuration(minutes: Int) {
        _selectedDuration.value = minutes
        validateForm()
    }

    /**
     * Validate form
     */
    private fun validateForm() {
        _isFormValid.value = _selectedDate.value != null &&
                              _selectedDate.value!!.isAfter(LocalDateTime.now()) &&
                              _selectedDuration.value > 0
    }

    /**
     * Check vehicle availability for selected time
     */
    fun checkAvailability() {
        val startDateTime = _selectedDate.value ?: return
        val endDateTime = startDateTime.plusMinutes(_selectedDuration.value.toLong())

        viewModelScope.launch {
            _isCheckingAvailability.value = true
            _availabilityMessage.value = null

            try {
                // Check for conflicting bookings
                val result = bookingRepository.searchBookings(
                    vehicleId = vehicleId.toLong(),
                    startAtFrom = startDateTime.format(DateTimeFormatter.ISO_DATE_TIME),
                    startAtTo = endDateTime.format(DateTimeFormatter.ISO_DATE_TIME),
                    status = "confirmed"
                )

                when (result) {
                    is Resource.Success -> {
                        val conflictingBookings = result.data?.filter { booking ->
                            booking.status == "confirmed" || booking.status == "active"
                        } ?: emptyList()

                        if (conflictingBookings.isEmpty()) {
                            _isAvailable.value = true
                            _availabilityMessage.value = "El vehículo está disponible para el horario seleccionado"
                        } else {
                            _isAvailable.value = false
                            _availabilityMessage.value = "El vehículo no está disponible en este horario. Por favor selecciona otro horario."
                        }
                        _isCheckingAvailability.value = false
                    }
                    is Resource.Error -> {
                        _isAvailable.value = false
                        _availabilityMessage.value = result.message ?: "Error al verificar disponibilidad"
                        _isCheckingAvailability.value = false
                    }
                    else -> {
                        _isCheckingAvailability.value = false
                    }
                }
            } catch (e: Exception) {
                _isAvailable.value = false
                _availabilityMessage.value = "Error al verificar disponibilidad: ${e.message}"
                _isCheckingAvailability.value = false
            }
        }
    }

    /**
     * Create scheduled booking
     */
    fun createScheduledBooking(onSuccess: (Long) -> Unit) {
        val startDateTime = _selectedDate.value ?: run {
            _bookingError.value = "Por favor selecciona una fecha y hora"
            return
        }

        val endDateTime = startDateTime.plusMinutes(_selectedDuration.value.toLong())

        if (!validateBookingTime(startDateTime)) {
            return
        }

        viewModelScope.launch {
            _isCreatingBooking.value = true
            _bookingError.value = null

            val request = CreateBookingRequest(
                userId = userId,
                vehicleId = vehicleId.toLong(),
                startDate = startDateTime.format(DateTimeFormatter.ISO_DATE_TIME),
                endDate = endDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
            )

            when (val result = bookingRepository.createBooking(request)) {
                is Resource.Success -> {
                    _bookingResult.value = result.data
                    _isCreatingBooking.value = false

                    result.data.bookingId.let { bookingId ->
                        // Schedule notification for 15 minutes before start time
                        scheduleNotification(bookingId, startDateTime)
                        onSuccess(bookingId)
                    }
                }
                is Resource.Error -> {
                    _bookingError.value = result.message ?: "Error al crear la reserva"
                    _isCreatingBooking.value = false
                }
                else -> {
                    _isCreatingBooking.value = false
                }
            }
        }
    }

    /**
     * Validate booking time
     */
    private fun validateBookingTime(startDateTime: LocalDateTime): Boolean {
        val now = LocalDateTime.now()

        // Check if date is in the future
        if (startDateTime.isBefore(now.plusMinutes(15))) {
            _bookingError.value = "La reserva debe ser al menos 15 minutos en el futuro"
            return false
        }

        // Check if date is too far in the future (max 7 days)
        if (startDateTime.isAfter(now.plusDays(7))) {
            _bookingError.value = "Las reservas solo pueden hacerse con hasta 7 días de anticipación"
            return false
        }

        return true
    }

    /**
     * Schedule notification for booking
     */
    private fun scheduleNotification(bookingId: Long, startDateTime: LocalDateTime) {
        // Calculate notification time (15 minutes before booking)
        val notificationTime = startDateTime.minusMinutes(15)

        // TODO: Implement notification scheduling using WorkManager or AlarmManager
        // This will be implemented in the NotificationScheduler utility
    }

    /**
     * Reset error states
     */
    fun resetErrors() {
        _bookingError.value = null
        _availabilityMessage.value = null
    }

    /**
     * Clear booking result
     */
    fun clearBookingResult() {
        _bookingResult.value = null
    }

    /**
     * Get estimated cost
     */
    fun getEstimatedCost(): Double? {
        val vehicle = _vehicle.value ?: return null
        val duration = _selectedDuration.value
        return vehicle.pricePerMinute * duration
    }

    /**
     * Get formatted end time
     */
    fun getFormattedEndTime(): String? {
        val startDateTime = _selectedDate.value ?: return null
        val endDateTime = startDateTime.plusMinutes(_selectedDuration.value.toLong())
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return endDateTime.format(formatter)
    }

    /**
     * Get formatted date
     */
    fun getFormattedDate(): String? {
        val dateTime = _selectedDate.value ?: return null
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", java.util.Locale("es", "PE"))
        return dateTime.format(formatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    /**
     * Get formatted time
     */
    fun getFormattedTime(): String? {
        val dateTime = _selectedDate.value ?: return null
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return dateTime.format(formatter)
    }
}
