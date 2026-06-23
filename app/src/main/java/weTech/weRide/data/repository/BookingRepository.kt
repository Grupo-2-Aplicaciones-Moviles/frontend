package weTech.weRide.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import weTech.weRide.data.api.services.BookingApiService
import weTech.weRide.data.auth.AuthStateManager
import weTech.weRide.data.models.bookings.BookingResource
import weTech.weRide.data.models.bookings.CancelBookingRequest
import weTech.weRide.data.models.bookings.CreateBookingRequest
import weTech.weRide.data.models.bookings.SaveBookingDraftRequest
import weTech.weRide.data.models.bookings.SubmitRatingRequest
import weTech.weRide.utils.Resource

/**
 * Booking Repository
 * Handles all booking-related operations
 */
class BookingRepository(
    private val bookingApiService: BookingApiService,
    private val authStateManager: AuthStateManager
) {

    /**
     * Create a new booking
     */
    suspend fun createBooking(request: CreateBookingRequest): Resource<BookingResource> {
        return try {
            val response = bookingApiService.createBooking(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create booking")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Save a booking draft
     */
    suspend fun saveDraft(request: SaveBookingDraftRequest): Resource<BookingResource> {
        return try {
            val response = bookingApiService.saveDraft(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to save draft")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get booking by ID
     */
    suspend fun getBookingById(id: Long): Resource<BookingResource> {
        return try {
            val response = bookingApiService.getBookingById(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Booking not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Search bookings
     */
    suspend fun searchBookings(
        customerId: Long? = null,
        vehicleId: Long? = null,
        status: String? = null,
        startAtFrom: String? = null,
        startAtTo: String? = null,
        page: Int = 0,
        size: Int = 20
    ): Resource<List<BookingResource>> {
        return try {
            val response = bookingApiService.searchBookings(
                customerId = customerId,
                vehicleId = vehicleId,
                status = status,
                startAtFrom = startAtFrom,
                startAtTo = startAtTo,
                page = page,
                size = size
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to search bookings")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get bookings by user ID
     */
    suspend fun getBookingsByUserId(userId: Long, page: Int = 0, size: Int = 20): Resource<List<BookingResource>> {
        return try {
            val response = bookingApiService.getBookingsByUserId(userId, page, size)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch user bookings")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get pending bookings for user
     */
    suspend fun getPendingBookingsByUser(userId: Long, page: Int = 0, size: Int = 20): Resource<List<BookingResource>> {
        return try {
            val response = bookingApiService.getPendingBookingsByUser(userId, page, size)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch pending bookings")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get completed bookings for user
     */
    suspend fun getCompletedBookingsByUser(userId: Long, page: Int = 0, size: Int = 20): Resource<List<BookingResource>> {
        return try {
            val response = bookingApiService.getCompletedBookingsByUser(userId, page, size)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch completed bookings")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get bookings by vehicle
     */
    suspend fun getBookingsByVehicle(vehicleId: Long, page: Int = 0, size: Int = 20): Resource<List<BookingResource>> {
        return try {
            val response = bookingApiService.getBookingsByVehicle(vehicleId, page, size)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch vehicle bookings")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get bookings by status
     */
    suspend fun getBookingsByStatus(status: String, page: Int = 0, size: Int = 20): Resource<List<BookingResource>> {
        return try {
            val response = bookingApiService.getBookingsByStatus(status, page, size)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch bookings by status")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get draft bookings for customer
     */
    suspend fun getDraftsByCustomer(customerId: Long, page: Int = 0, size: Int = 20): Resource<List<BookingResource>> {
        return try {
            val response = bookingApiService.getDraftsByCustomer(customerId, page, size)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch draft bookings")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Delete draft booking
     */
    suspend fun deleteDraft(draftId: Long): Resource<String> {
        return try {
            val response = bookingApiService.deleteDraft(draftId)
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: "Draft deleted successfully")
            } else {
                Resource.Error(response.message() ?: "Failed to delete draft")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Cancel booking by ID
     */
    suspend fun cancelBooking(bookingId: Long): Resource<BookingResource> {
        return try {
            val request = CancelBookingRequest(bookingId = bookingId, reason = "User cancelled")
            val response = bookingApiService.cancelBooking(bookingId, request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to cancel booking")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Cancel booking with request
     */
    suspend fun cancelBooking(request: CancelBookingRequest): Resource<BookingResource> {
        return cancelBooking(request.bookingId)
    }

    /**
     * Update booking status
     */
    suspend fun updateBookingStatus(bookingId: Long, status: String): Resource<BookingResource> {
        return try {
            val response = bookingApiService.updateBookingStatus(bookingId, status)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to update booking status")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Submit rating for a booking
     */
    suspend fun submitRating(request: SubmitRatingRequest): Resource<BookingResource> {
        return try {
            val response = bookingApiService.submitRating(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to submit rating")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get bookings for the currently authenticated user
     */
    suspend fun getCurrentUserBookings(page: Int = 0, size: Int = 20): Resource<List<BookingResource>> {
        return try {
            val userId = authStateManager.getUserId().firstOrNull()
            if (userId != null) {
                getBookingsByUserId(userId.toLong(), page, size)
            } else {
                Resource.Error("User not authenticated")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get pending bookings for the currently authenticated user
     */
    suspend fun getCurrentUserPendingBookings(page: Int = 0, size: Int = 20): Resource<List<BookingResource>> {
        return try {
            val userId = authStateManager.getUserId().firstOrNull()
            if (userId != null) {
                getPendingBookingsByUser(userId.toLong(), page, size)
            } else {
                Resource.Error("User not authenticated")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get completed bookings for the currently authenticated user
     */
    suspend fun getCurrentUserCompletedBookings(page: Int = 0, size: Int = 20): Resource<List<BookingResource>> {
        return try {
            val userId = authStateManager.getUserId().firstOrNull()
            if (userId != null) {
                getCompletedBookingsByUser(userId.toLong(), page, size)
            } else {
                Resource.Error("User not authenticated")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get draft bookings for the currently authenticated user
     */
    suspend fun getCurrentUserDrafts(page: Int = 0, size: Int = 20): Resource<List<BookingResource>> {
        return try {
            val userId = authStateManager.getUserId().firstOrNull()
            if (userId != null) {
                getDraftsByCustomer(userId.toLong(), page, size)
            } else {
                Resource.Error("User not authenticated")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }
}
