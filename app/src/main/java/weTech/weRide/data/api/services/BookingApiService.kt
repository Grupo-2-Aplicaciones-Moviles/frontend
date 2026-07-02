package weTech.weRide.data.api.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import weTech.weRide.data.models.bookings.BookingResource
import weTech.weRide.data.models.bookings.CancelBookingRequest
import weTech.weRide.data.models.bookings.ConfirmBookingFromDraftRequest
import weTech.weRide.data.models.bookings.CreateBookingRequest
import weTech.weRide.data.models.bookings.ReportProblemRequest
import weTech.weRide.data.models.bookings.SaveBookingDraftRequest
import weTech.weRide.data.models.bookings.SubmitRatingRequest
import weTech.weRide.data.models.common.PageResponse

/**
 * Booking API Service
 */
interface BookingApiService {

    @POST("bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<BookingResource>

    @POST("bookings/draft")
    suspend fun saveDraft(@Body request: SaveBookingDraftRequest): Response<BookingResource>

    @GET("bookings/{id}")
    suspend fun getBookingById(@Path("id") id: Long): Response<BookingResource>

    @GET("bookings")
    suspend fun searchBookings(
        @Query("customerId") customerId: Long? = null,
        @Query("vehicleId") vehicleId: Long? = null,
        @Query("status") status: String? = null,
        @Query("startAtFrom") startAtFrom: String? = null,
        @Query("startAtTo") startAtTo: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<BookingResource>>

    @GET("bookings/user/{userId}")
    suspend fun getBookingsByUserId(
        @Path("userId") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<BookingResource>>

    @GET("bookings/user/{userId}/pending")
    suspend fun getPendingBookingsByUser(
        @Path("userId") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<BookingResource>>

    @GET("bookings/user/{userId}/completed")
    suspend fun getCompletedBookingsByUser(
        @Path("userId") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<BookingResource>>

    @GET("bookings/vehicle/{vehicleId}")
    suspend fun getBookingsByVehicle(
        @Path("vehicleId") vehicleId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<BookingResource>>

    @GET("bookings/status/{status}")
    suspend fun getBookingsByStatus(
        @Path("status") status: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<BookingResource>>

    @GET("bookings/drafts")
    suspend fun getDraftsByCustomer(
        @Query("customerId") customerId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<BookingResource>>

    @POST("bookings/{id}/cancel")
    suspend fun cancelBooking(
        @Path("id") bookingId: Long,
        @Body request: CancelBookingRequest
    ): Response<BookingResource>

    @PATCH("bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") bookingId: Long,
        @Query("status") status: String
    ): Response<BookingResource>

    @POST("bookings/{id}/rating")
    suspend fun submitRating(@Body request: SubmitRatingRequest): Response<BookingResource>

    @DELETE("bookings/draft/{draftId}")
    suspend fun deleteDraft(@Path("draftId") draftId: Long): Response<String>
}
