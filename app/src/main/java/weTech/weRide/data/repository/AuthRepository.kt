package weTech.weRide.data.repository

import kotlinx.coroutines.flow.Flow
import weTech.weRide.data.api.services.AuthApiService
import weTech.weRide.data.models.auth.AuthResponse
import weTech.weRide.data.models.auth.SignInRequest
import weTech.weRide.data.models.auth.SignUpRequest
import weTech.weRide.data.pref.TokenManager
import weTech.weRide.utils.Resource

/**
 * Authentication Repository
 * Handles all authentication-related operations
 */
class AuthRepository(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) {

    /**
     * Sign in with username and password
     */
    suspend fun signIn(username: String, password: String): Resource<AuthResponse> {
        return try {
            val response = authApiService.signIn(SignInRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Save token
                tokenManager.saveToken(authResponse.token)
                tokenManager.saveUserInfo(
                    userId = authResponse.id.toString(),
                    email = username,
                    name = username.split('@').firstOrNull() ?: username // Use username before @ as display name
                )
                Resource.Success(authResponse)
            } else {
                Resource.Error(response.message() ?: "Sign in failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Sign up with username and password
     * Note: Deployed backend returns AccountResource(id, username) from sign-up,
     * so we need to call sign-in afterwards to get the token
     */
    suspend fun signUp(username: String, password: String): Resource<AuthResponse> {
        return try {
            // First, sign up to create the account
            val signUpResponse = authApiService.signUp(SignUpRequest(username, password))
            if (!signUpResponse.isSuccessful || signUpResponse.body() == null) {
                return Resource.Error(signUpResponse.message() ?: "Sign up failed")
            }

            // Account created successfully, now sign in to get the token
            val signInResponse = authApiService.signIn(SignInRequest(username, password))
            if (signInResponse.isSuccessful && signInResponse.body() != null) {
                val authResponse = signInResponse.body()!!
                // Save token
                tokenManager.saveToken(authResponse.token)
                tokenManager.saveUserInfo(
                    userId = authResponse.id.toString(),
                    email = username,
                    name = username.split('@').firstOrNull() ?: username // Use username before @ as display name
                )
                Resource.Success(authResponse)
            } else {
                Resource.Error("Account created but sign in failed: ${signInResponse.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Logout user
     */
    suspend fun logout() {
        tokenManager.clearAll()
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Boolean> {
        return tokenManager.isLoggedIn()
    }

    /**
     * Get user ID
     */
    fun getUserId(): Flow<String?> {
        return tokenManager.getUserId()
    }

    /**
     * Get user email
     */
    fun getUserEmail(): Flow<String?> {
        return tokenManager.getUserEmail()
    }

    /**
     * Get user name
     */
    fun getUserName(): Flow<String?> {
        return tokenManager.getUserName()
    }

    /**
     * Get auth token
     */
    fun getToken(): Flow<String?> {
        return tokenManager.getToken()
    }
}
