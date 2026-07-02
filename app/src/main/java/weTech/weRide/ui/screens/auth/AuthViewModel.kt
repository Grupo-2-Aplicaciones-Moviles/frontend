package weTech.weRide.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import weTech.weRide.data.repository.AuthRepository
import weTech.weRide.utils.Resource

/**
 * Authentication State
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * Authentication ViewModel
 * Handles authentication logic and state management
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel(), KoinComponent {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState.asStateFlow()

    /**
     * Sign in with username and password
     */
    suspend fun signIn(username: String, password: String): Boolean {
        _loginState.value = AuthState.Loading
        return try {
            when (val result = authRepository.signIn(username, password)) {
                is Resource.Success -> {
                    _loginState.value = AuthState.Success
                    true
                }
                is Resource.Error -> {
                    _loginState.value = AuthState.Error((result as Resource.Error).message)
                    false
                }
                else -> false
            }
        } catch (e: Exception) {
            _loginState.value = AuthState.Error(e.message ?: "Unknown error")
            false
        }
    }

    /**
     * Sign up with username and password
     */
    suspend fun signUp(username: String, password: String): Boolean {
        _registerState.value = AuthState.Loading
        return try {
            when (val result = authRepository.signUp(username, password)) {
                is Resource.Success -> {
                    _registerState.value = AuthState.Success
                    true
                }
                is Resource.Error -> {
                    _registerState.value = AuthState.Error((result as Resource.Error).message)
                    false
                }
                else -> false
            }
        } catch (e: Exception) {
            _registerState.value = AuthState.Error(e.message ?: "Unknown error")
            false
        }
    }
}
