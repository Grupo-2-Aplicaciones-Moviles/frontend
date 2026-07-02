package weTech.weRide.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import weTech.weRide.data.pref.TokenManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * User profile data
 */
data class UserProfile(
    val name: String,
    val email: String,
    val memberSince: String,
    val totalTrips: Int = 0,
    val rating: Double = 0.0,
    val isLoading: Boolean = false
)

/**
 * Profile ViewModel
 * Manages user profile state and data
 */
class ProfileViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _profileState = MutableStateFlow<UserProfile>(
        UserProfile(
            name = "",
            email = "",
            memberSince = "",
            totalTrips = 0,
            rating = 0.0,
            isLoading = true
        )
    )
    val profileState: StateFlow<UserProfile> = _profileState.asStateFlow()

    init {
        loadUserProfile()
    }

    /**
     * Load user profile from TokenManager
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val email = tokenManager.getUserEmail().first()
                val name = tokenManager.getUserName().first()

                // Calculate member since date (using current month/year as default)
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("MMM yyyy", Locale("es", "PE"))
                val memberSince = dateFormat.format(calendar.time)

                _profileState.value = UserProfile(
                    name = name?.ifEmpty { "Usuario" } ?: "Usuario",
                    email = email ?: "",
                    memberSince = memberSince,
                    totalTrips = 0, // TODO: Fetch from backend API
                    rating = 0.0, // TODO: Fetch from backend API
                    isLoading = false
                )
            } catch (e: Exception) {
                _profileState.value = UserProfile(
                    name = "Usuario",
                    email = "",
                    memberSince = "",
                    totalTrips = 0,
                    rating = 0.0,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Logout user
     */
    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearAll()
            onComplete()
        }
    }
}
