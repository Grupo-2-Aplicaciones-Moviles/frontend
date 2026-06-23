package weTech.weRide.data.models.vehicles

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Vehicle Resource
 */
data class VehicleResource(
    @SerializedName("id")
    val id: String,

    @SerializedName("brand")
    val brand: String,

    @SerializedName("model")
    val model: String,

    @SerializedName("year")
    val year: Int,

    @SerializedName("battery")
    val battery: Int,

    @SerializedName("maxSpeed")
    val maxSpeed: Int,

    @SerializedName("range")
    val range: Int,

    @SerializedName("weight")
    val weight: Double,

    @SerializedName("color")
    val color: String,

    @SerializedName("licensePlate")
    val licensePlate: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("companyId")
    val companyId: String,

    @SerializedName("pricePerMinute")
    val pricePerMinute: Double,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("features")
    val features: List<String>? = null,

    @SerializedName("maintenanceStatus")
    val maintenanceStatus: String? = null,

    @SerializedName("lastMaintenance")
    val lastMaintenance: Date? = null,

    @SerializedName("nextMaintenance")
    val nextMaintenance: Date? = null,

    @SerializedName("totalKilometers")
    val totalKilometers: Double? = null,

    @SerializedName("rating")
    val rating: Double? = null

) {

    /**
     * Check if vehicle is available
     */
    fun isAvailable(): Boolean = status.equals("available", ignoreCase = true)

    /**
     * Get battery level as percentage
     */
    fun getBatteryPercent(): Int = battery.coerceIn(0, 100)

    /**
     * Get vehicle type display name
     */
    fun getTypeDisplayName(): String = when (type.lowercase()) {
        "scooter" -> "Scooter"
        "bike" -> "Bicicleta"
        "motorcycle" -> "Motocicleta"
        else -> type
    }

    /**
     * Get rating formatted
     */
    fun getRatingFormatted(): String = rating?.let { "%.1f".format(it) } ?: "N/A"
}

/**
 * Create Vehicle Request
 */
data class CreateVehicleRequest(
    val brand: String,
    val model: String,
    val year: Int,
    val battery: Int,
    val maxSpeed: Int,
    val range: Int,
    val weight: Double,
    val color: String,
    val licensePlate: String,
    val location: String,
    val type: String,
    val companyId: String,
    val pricePerMinute: Double,
    val image: String? = null,
    val features: List<String>? = null
)

/**
 * Update Vehicle Request
 */
data class UpdateVehicleRequest(
    val brand: String? = null,
    val model: String? = null,
    val year: Int? = null,
    val battery: Int? = null,
    val maxSpeed: Int? = null,
    val range: Int? = null,
    val weight: Double? = null,
    val color: String? = null,
    val licensePlate: String? = null,
    val location: String? = null,
    val status: String? = null,
    val type: String? = null,
    val pricePerMinute: Double? = null,
    val image: String? = null,
    val features: List<String>? = null
)
