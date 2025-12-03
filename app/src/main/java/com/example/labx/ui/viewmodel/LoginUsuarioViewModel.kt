package com.example.labx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.labx.data.local.PreferenciasManager
import com.example.labx.domain.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginUsuarioViewModel(
    private val preferenciasManager: PreferenciasManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun login(onLoginSuccess: () -> Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Por favor completa todos los campos")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        // Simulación de autenticación
        // En un caso real, aquí llamaríamos a un Repository -> API
        if (email.contains("@")) { // Validación básica simulación
            preferenciasManager.guardarSesion(email, Rol.CLIENTE)
            _uiState.value = _uiState.value.copy(isLoading = false)
            onLoginSuccess()
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Credenciales inválidas"
            )
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginUsuarioViewModelFactory(
    private val preferenciasManager: PreferenciasManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginUsuarioViewModel::class.java)) {
            return LoginUsuarioViewModel(preferenciasManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
