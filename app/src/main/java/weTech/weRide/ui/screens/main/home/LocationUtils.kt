package weTech.weRide.ui.screens.main.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

/**
 * Utilities for handling location permissions and getting user location
 */
object LocationUtils {

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get required location permissions
     */
    fun getLocationPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    /**
     * Get user's last known location
     */
    fun getLastKnownLocation(
        context: Context,
        onSuccess: (LatLng) -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        onSuccess(LatLng(location.latitude, location.longitude))
                    } else {
                        onFailure()
                    }
                }
                .addOnFailureListener {
                    onFailure()
                }
        } catch (e: SecurityException) {
            onFailure()
        }
    }

    /**
     * Request location updates
     */
    fun requestLocationUpdates(
        context: Context,
        locationCallback: LocationCallback
    ) {
        try {
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)

            val locationRequest = LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                5000 // Update every 5 seconds
            ).apply {
                setMinUpdateIntervalMillis(3000) // Minimum 3 seconds between updates
                setGranularity(com.google.android.gms.location.Granularity.GRANULARITY_PERMISSION_LEVEL)
                setDurationMillis(30000) // Stop after 30 seconds
            }.build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Handle security exception
        }
    }

    /**
     * Stop location updates
     */
    fun stopLocationUpdates(
        context: Context,
        locationCallback: LocationCallback
    ) {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Calculate distance between two points in meters
     */
    fun calculateDistance(from: LatLng, to: LatLng): Double {
        val r = 6371000 // Earth's radius in meters
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(from.latitude)) * Math.cos(Math.toRadians(to.latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    /**
     * Format distance for display
     */
    fun formatDistance(meters: Int): String {
        return when {
            meters < 1000 -> "$meters m"
            else -> "${meters / 1000}.${(meters % 1000) / 100} km"
        }
    }

    /**
     * Format walking time for display
     */
    fun formatWalkingTime(minutes: Int): String {
        return when {
            minutes < 60 -> "$minutes min"
            minutes >= 60 -> "${minutes / 60}h ${minutes % 60}min"
            else -> "${minutes}min"
        }
    }

    /**
     * Get default location (Lima, Peru)
     */
    fun getDefaultLocation(): LatLng {
        return LatLng(-12.0464, -77.0429)
    }

    /**
     * Get default camera position
     */
    fun getDefaultCameraPosition(): com.google.android.gms.maps.model.CameraPosition {
        return com.google.android.gms.maps.model.CameraPosition.builder()
            .target(getDefaultLocation())
            .zoom(15f)
            .build()
    }
}
