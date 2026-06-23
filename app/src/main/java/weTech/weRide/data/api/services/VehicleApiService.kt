package weTech.weRide.data.api.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import weTech.weRide.data.models.vehicles.CreateVehicleRequest
import weTech.weRide.data.models.vehicles.UpdateVehicleRequest
import weTech.weRide.data.models.vehicles.VehicleResource

/**
 * Vehicle API Service
 */
interface VehicleApiService {

    @GET("vehicles")
    suspend fun getAllVehicles(): Response<List<VehicleResource>>

    @GET("vehicles/{id}")
    suspend fun getVehicleById(@Path("id") id: String): Response<VehicleResource>

    @POST("vehicles")
    suspend fun createVehicle(@Body request: CreateVehicleRequest): Response<VehicleResource>

    @PUT("vehicles/{id}")
    suspend fun updateVehicle(
        @Path("id") id: String,
        @Body request: UpdateVehicleRequest
    ): Response<VehicleResource>

    @DELETE("vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: String): Response<Unit>
}
