package weTech.weRide.data.models.auth

import com.google.gson.annotations.SerializedName

/**
 * Sign In Request
 */
data class SignInRequest(
    val username: String,
    val password: String
)

/**
 * Sign Up Request
 */
data class SignUpRequest(
    val username: String,
    val password: String
)

/**
 * Auth Response
 */
data class AuthResponse(
    val id: Long,
    val token: String
)

/**
 * Account Resource
 */
data class AccountResource(
    val id: Long,
    val email: String
)

/**
 * Phone Verification Request
 */
data class PhoneVerificationRequest(
    val phoneNumber: String,
    val countryCode: String = "+51"
)

/**
 * Verify Code Request
 */
data class VerifyCodeRequest(
    val phoneNumber: String,
    val code: String
)

/**
 * Profile Setup Request
 */
data class ProfileSetupRequest(
    val fullName: String,
    val dateOfBirth: String,
    val gender: String,
    val profilePictureUrl: String? = null
)
