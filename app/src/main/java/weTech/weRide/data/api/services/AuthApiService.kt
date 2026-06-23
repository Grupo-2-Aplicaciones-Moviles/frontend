package weTech.weRide.data.api.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import weTech.weRide.data.models.auth.AuthResponse
import weTech.weRide.data.models.auth.SignInRequest
import weTech.weRide.data.models.auth.SignUpRequest

/**
 * Authentication API Service
 */
interface AuthApiService {

    @POST("authentication/sign-in")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    @POST("authentication/sign-up")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>
}
