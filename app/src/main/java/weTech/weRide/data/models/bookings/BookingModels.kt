package weTech.weRide.data.models.bookings

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Booking Resource
 */
data class BookingResource(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("bookingId")
    val bookingId: Long,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("vehicleId")
    val vehicleId: Long,

    @SerializedName("startLocationId")
    val startLocationId: Long? = null,

    @SerializedName("endLocationId")
    val endLocationId: Long? = null,

    @SerializedName("reservedAt")
    val reservedAt: String? = null, // LocalDateTime as string

    @SerializedName("startDate")
    val startDate: String? = null, // LocalDateTime as string

    @SerializedName("endDate")
    val endDate: String? = null, // LocalDateTime as string

    @SerializedName("actualStartDate")
    val actualStartDate: String? = null, // LocalDateTime as string

    @SerializedName("actualEndDate")
    val actualEndDate: String? = null, // LocalDateTime as string

    @SerializedName("status")
    val status: String,

    @SerializedName("totalCost")
    val totalCost: Double? = null,

    @SerializedName("discount")
    val discount: Double? = null,

    @SerializedName("finalCost")
    val finalCost: Double? = null,

    @SerializedName("paymentMethod")
    val paymentMethod: String? = null,

    @SerializedName("paymentStatus")
    val paymentStatus: String? = null,

    @SerializedName("distance")
    val distance: Double? = null,

    @SerializedName("duration")
    val duration: Int? = null,

    @SerializedName("averageSpeed")
    val averageSpeed: Double? = null,

    @SerializedName("rating")
    val rating: RatingResource? = null,

    @SerializedName("issues")
    val issues: List<String>? = null

) {

    fun isActive(): Boolean = status == "active"
    fun isPending(): Boolean = status == "confirmed"
    fun isCompleted(): Boolean = status == "completed"
    fun isCancelled(): Boolean = status == "cancelled"
    fun isDraft(): Boolean = status == "draft"

    fun getStatusDisplayName(): String = when (status.lowercase()) {
        "draft" -> "Borrador"
        "confirmed" -> "Confirmada"
        "active" -> "En curso"
        "completed" -> "Completada"
        "cancelled" -> "Cancelada"
        else -> status
    }
}

/**
 * Rating Resource
 */
data class RatingResource(
    @SerializedName("score")
    val score: Int? = null,

    @SerializedName("comment")
    val comment: String? = null
)

/**
 * Create Booking Request
 */
data class CreateBookingRequest(
    val userId: Long,
    val vehicleId: Long,
    val startDate: String, // ISO datetime string
    val endDate: String, // ISO datetime string
    val startLocationId: Long? = null,
    val endLocationId: Long? = null,
    val paymentMethod: String = "WALLET", // WALLET, CARD, YAPE, PLIN
    val paymentStatus: String = "PENDING", // PENDING, PAID, FAILED
    val status: String = "DRAFT" // DRAFT, CONFIRMED, ACTIVE, COMPLETED, CANCELLED
)

/**
 * Save Booking Draft Request
 */
data class SaveBookingDraftRequest(
    val userId: Long,
    val vehicleId: Long,
    val startDate: String, // ISO datetime string
    val endDate: String // ISO datetime string
)

/**
 * Update Booking Draft Request
 */
data class UpdateBookingDraftRequest(
    val bookingId: Long,
    val vehicleId: Long? = null,
    val startDate: String? = null, // ISO datetime string
    val endDate: String? = null // ISO datetime string
)

/**
 * Confirm Booking from Draft Request
 */
data class ConfirmBookingFromDraftRequest(
    val bookingId: Long,
    val paymentMethod: String
)

/**
 * Cancel Booking Request
 */
data class CancelBookingRequest(
    val bookingId: Long,
    val reason: String? = null
)

/**
 * Submit Rating Request
 */
data class SubmitRatingRequest(
    val bookingId: Long,
    val score: Int,
    val comment: String? = null
)

/**
 * Report Problem Request
 */
data class ReportProblemRequest(
    val bookingId: Long,
    val category: String,
    val description: String,
    val imageUrl: String? = null
)
