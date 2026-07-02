package weTech.weRide.ui.screens.main.tripHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import weTech.weRide.data.auth.AuthStateManager
import weTech.weRide.data.models.bookings.BookingResource
import weTech.weRide.data.repository.BookingRepository
import weTech.weRide.utils.Resource

/**
 * Trip History ViewModel
 * Manages trip history state
 */
class TripHistoryViewModel(
    private val bookingRepository: BookingRepository,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    // Trip list state
    private val _trips = MutableStateFlow<List<BookingResource>>(emptyList())
    val trips: StateFlow<List<BookingResource>> = _trips.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    init {
        loadTripHistory()
    }

    /**
     * Load trip history (all bookings)
     */
    fun loadTripHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val userId = authStateManager.getUserId().firstOrNull()?.toLongOrNull()
            if (userId == null) {
                _error.value = "Usuario no autenticado"
                _isLoading.value = false
                return@launch
            }

            when (val result = bookingRepository.getBookingsByUserId(userId)) {
                is Resource.Success -> {
                    val allTrips = result.data ?: emptyList()
                    // Sort: CONFIRMED (upcoming) first, then COMPLETED, then others
                    val sortedTrips = allTrips.sortedByDescending { trip ->
                        when (trip.status) {
                            "CONFIRMED" -> 3
                            "ACTIVE" -> 2
                            "COMPLETED" -> 1
                            else -> 0
                        }
                    }
                    _trips.value = sortedTrips
                    _isEmpty.value = sortedTrips.isEmpty()
                    _isLoading.value = false
                }
                is Resource.Error -> {
                    _error.value = result.message ?: "Error al cargar historial de viajes"
                    _isLoading.value = false
                }
                else -> {
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Refresh trip history
     */
    fun refresh() {
        loadTripHistory()
    }

    /**
     * Get formatted trip stats
     */
    fun getTotalTrips(): Int {
        return _trips.value.size
    }

    fun getTotalCost(): Double {
        return _trips.value.sumOf { it.finalCost ?: 0.0 }
    }

    fun getTotalDistance(): Double {
        return _trips.value.sumOf { it.distance ?: 0.0 }
    }

    fun getTotalDuration(): Int {
        return _trips.value.sumOf { it.duration ?: 0 }
    }
}
