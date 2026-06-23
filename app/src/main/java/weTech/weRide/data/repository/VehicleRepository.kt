package weTech.weRide.data.repository

import weTech.weRide.data.api.services.VehicleApiService
import weTech.weRide.data.models.vehicles.CreateVehicleRequest
import weTech.weRide.data.models.vehicles.UpdateVehicleRequest
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.utils.Resource
import weTech.weRide.utils.getData

/**
 * Vehicle Repository
 * Handles all vehicle-related operations
 */
class VehicleRepository(
    private val vehicleApiService: VehicleApiService
) {

    /**
     * Get all vehicles
     */
    suspend fun getAllVehicles(): Resource<List<VehicleResource>> {
        return try {
            val response = vehicleApiService.getAllVehicles()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch vehicles")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get vehicle by ID
     */
    suspend fun getVehicleById(id: String): Resource<VehicleResource> {
        return try {
            val response = vehicleApiService.getVehicleById(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Vehicle not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Create new vehicle (admin only)
     */
    suspend fun createVehicle(request: CreateVehicleRequest): Resource<VehicleResource> {
        return try {
            val response = vehicleApiService.createVehicle(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create vehicle")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Update vehicle (admin only)
     */
    suspend fun updateVehicle(id: String, request: UpdateVehicleRequest): Resource<VehicleResource> {
        return try {
            val response = vehicleApiService.updateVehicle(id, request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to update vehicle")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Delete vehicle (admin only)
     */
    suspend fun deleteVehicle(id: String): Resource<Unit> {
        return try {
            val response = vehicleApiService.deleteVehicle(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete vehicle")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Get available vehicles only
     */
    suspend fun getAvailableVehicles(): Resource<List<VehicleResource>> {
        val result = getAllVehicles()
        return if (result.isSuccess()) {
            Resource.Success(result.getData()!!.filter { it.isAvailable() })
        } else {
            result as Resource.Error
        }
    }

    /**
     * Get vehicles by type
     */
    suspend fun getVehiclesByType(type: String): Resource<List<VehicleResource>> {
        val result = getAllVehicles()
        return if (result.isSuccess()) {
            Resource.Success(result.getData()!!.filter { it.type.equals(type, ignoreCase = true) })
        } else {
            result as Resource.Error
        }
    }

    /**
     * Filter vehicles
     */
    suspend fun filterVehicles(
        type: String? = null,
        minBattery: Int? = null,
        minRating: Double? = null
    ): Resource<List<VehicleResource>> {
        val result = getAllVehicles()
        return if (result.isSuccess()) {
            var filtered = result.getData()!!

            type?.let { filtered = filtered.filter { v -> v.type.equals(it, ignoreCase = true) } }
            minBattery?.let { filtered = filtered.filter { v -> v.battery >= it } }
            minRating?.let { filtered = filtered.filter { v -> (v.rating ?: 0.0) >= it } }

            Resource.Success(filtered)
        } else {
            result as Resource.Error
        }
    }
}
