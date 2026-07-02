package weTech.weRide.ui.screens.editProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import weTech.weRide.data.pref.TokenManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Edit Profile UI State
 */
data class EditProfileState(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val memberSince: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null
)

/**
 * Edit Profile ViewModel
 * Manages profile editing state and operations
 */
class EditProfileViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    init {
        loadProfileData()
    }

    /**
     * Load current profile data
     */
    fun loadProfileData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val userId = tokenManager.getUserId().first() ?: ""
                val name = tokenManager.getUserName().first() ?: ""
                val email = tokenManager.getUserEmail().first() ?: ""
                val photoUrl = tokenManager.getUserPhoto().first() ?: ""

                // Calculate member since (using current month/year as default)
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "PE"))
                val memberSince = dateFormat.format(calendar.time)

                _state.value = EditProfileState(
                    userId = userId,
                    name = name,
                    email = email,
                    photoUrl = photoUrl,
                    memberSince = memberSince,
                    isLoading = false,
                    isSaving = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Save profile changes
     */
    fun saveProfile(
        name: String,
        email: String,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)

            try {
                val userId = tokenManager.getUserId().first() ?: ""

                // Validate inputs
                if (name.isBlank()) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = "El nombre no puede estar vacío"
                    )
                    onComplete(false)
                    return@launch
                }

                if (email.isBlank()) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = "El correo electrónico no puede estar vacío"
                    )
                    onComplete(false)
                    return@launch
                }

                if (!isValidEmail(email)) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = "Correo electrónico inválido"
                    )
                    onComplete(false)
                    return@launch
                }

                // Save to TokenManager
                tokenManager.saveUserInfo(
                    userId = userId,
                    email = email,
                    name = name,
                    photoUrl = _state.value.photoUrl.takeIf { it.isNotEmpty() }
                )

                _state.value = _state.value.copy(
                    name = name,
                    email = email,
                    isSaving = false,
                    error = null
                )

                onComplete(true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.message ?: "Error al guardar"
                )
                onComplete(false)
            }
        }
    }

    /**
     * Update profile photo
     * Note: Currently stores URL. Backend integration needed for file upload.
     */
    fun updatePhoto(photoUrl: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                tokenManager.saveUserPhoto(photoUrl)
                _state.value = _state.value.copy(photoUrl = photoUrl)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    /**
     * Validate email format
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return emailRegex.matches(email)
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
