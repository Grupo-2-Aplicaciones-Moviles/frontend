package weTech.weRide.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import weTech.weRide.data.pref.TokenManager
import weTech.weRide.utils.Constants

/**
 * Authentication State Manager
 * Manages user authentication state across the app
 */
class AuthStateManager(private val tokenManager: TokenManager) {

    companion object {
        private val USER_ID_KEY = stringPreferencesKey(Constants.KEY_USER_ID)
        private val USER_EMAIL_KEY = stringPreferencesKey(Constants.KEY_USER_EMAIL)
        private val USER_NAME_KEY = stringPreferencesKey(Constants.KEY_USER_NAME)
        private val USER_PHOTO_KEY = stringPreferencesKey(Constants.KEY_USER_PHOTO)
        private val IS_LOGGED_IN_KEY = stringPreferencesKey(Constants.KEY_IS_LOGGED_IN)
    }

    /**
     * Get current authentication state as a flow
     */
    fun getAuthState(): Flow<AuthState> {
        return tokenManager.getToken().map { token ->
            if (token != null) {
                AuthState.Authenticated(
                    token = token,
                    userId = getUserIdSync(),
                    email = getUserEmailSync(),
                    name = getUserNameSync(),
                    photoUrl = getUserPhotoSync()
                )
            } else {
                AuthState.NotAuthenticated
            }
        }
    }

    /**
     * Get current user ID as a flow
     */
    fun getUserId(): Flow<String?> {
        return tokenManager.getUserId()
    }

    /**
     * Get user email as a flow
     */
    fun getUserEmail(): Flow<String?> {
        return tokenManager.getUserEmail()
    }

    /**
     * Get user name as a flow
     */
    fun getUserName(): Flow<String?> {
        return tokenManager.getUserName()
    }

    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Flow<Boolean> {
        return tokenManager.isLoggedIn()
    }

    /**
     * Save authentication data after successful login
     */
    suspend fun saveAuthData(
        token: String,
        userId: String,
        email: String,
        name: String,
        photoUrl: String? = null
    ) {
        tokenManager.saveToken(token)
        tokenManager.saveUserInfo(userId, email, name)
        if (photoUrl != null) {
            saveUserPhoto(photoUrl)
        }
    }

    /**
     * Save user photo URL
     */
    private suspend fun saveUserPhoto(photoUrl: String) {
        // This would need access to DataStore, can be added to TokenManager
    }

    /**
     * Clear all authentication data (logout)
     */
    suspend fun clearAuthData() {
        tokenManager.clearAll()
    }

    // Synchronous methods for immediate access (use with caution)
    private suspend fun getUserIdSync(): String? {
        var result: String? = null
        tokenManager.getUserId().collect { result = it }
        return result
    }

    private suspend fun getUserEmailSync(): String? {
        var result: String? = null
        tokenManager.getUserEmail().collect { result = it }
        return result
    }

    private suspend fun getUserNameSync(): String? {
        var result: String? = null
        tokenManager.getUserName().collect { result = it }
        return result
    }

    private suspend fun getUserPhotoSync(): String? {
        // This would be implemented if photo URL is stored
        return null
    }
}

/**
 * Authentication state sealed class
 */
sealed class AuthState {
    object NotAuthenticated : AuthState()
    data class Authenticated(
        val token: String,
        val userId: String?,
        val email: String?,
        val name: String?,
        val photoUrl: String?
    ) : AuthState()
}

/**
 * Extension function to check if user is authenticated
 */
fun AuthState.isAuthenticated(): Boolean {
    return this is AuthState.Authenticated
}

/**
 * Extension function to get user ID from auth state
 */
fun AuthState.getUserId(): String? {
    return if (this is AuthState.Authenticated) userId else null
}

/**
 * Extension function to get token from auth state
 */
fun AuthState.getToken(): String? {
    return if (this is AuthState.Authenticated) token else null
}
