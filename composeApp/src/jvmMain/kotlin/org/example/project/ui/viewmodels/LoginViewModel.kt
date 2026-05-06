package org.example.project.ui.viewmodels
import org.example.project.data.remote.models.*
import org.example.project.data.repository.MatrimonyRepository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val userId: String = "CM975223",
    val password: String = "jeffin123!",
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val loginSuccess: LoginResponse? = null
)

sealed interface LoginEvent {
    data class OnUserIdChange(val userId: String) : LoginEvent
    data class OnPasswordChange(val password: String) : LoginEvent
    data object OnLoginClick : LoginEvent
    data object ResetSuccessState : LoginEvent
}

class LoginViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUserIdChange -> _state.update { it.copy(userId = event.userId) }
            is LoginEvent.OnPasswordChange -> _state.update { it.copy(password = event.password) }
            is LoginEvent.OnLoginClick -> performLogin()
            is LoginEvent.ResetSuccessState -> _state.update { it.copy(loginSuccess = null) }
        }
    }

    private fun performLogin() {
        val currentState = _state.value
        if (currentState.userId.isBlank() || currentState.password.isBlank()) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, resultMessage = null) }
            val request = LoginRequest(userId = currentState.userId, password = currentState.password)
            val result = repository.login(request)
            
            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, loginSuccess = result.getOrNull()) }
            } else {
                _state.update { it.copy(isLoading = false, resultMessage = "Error: ${result.exceptionOrNull()?.message}") }
            }
        }
    }
}

