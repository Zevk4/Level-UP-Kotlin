package com.example.labx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.labx.ui.state.RegistroUiState
import com.example.labx.domain.validator.ValidadorFormulario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * RegistroViewModel: Gestiona el formulario de registro
 */
class RegistroViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()
    
    /**
     * Actualiza el nombre completo y valida
     */
    fun onNombreChange(nombre: String) {
        val errores = _uiState.value.errores.copy(
            nombreCompletoError = ValidadorFormulario.validarNombreCompleto(nombre)
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(nombreCompleto = nombre),
            errores = errores
        )
    }
    
    /**
     * Actualiza el email y valida formato
     */
    fun onEmailChange(email: String) {
        val errores = _uiState.value.errores.copy(
            emailError = ValidadorFormulario.validarEmail(email)
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(email = email),
            errores = errores
        )
    }
    
    /**
     * Actualiza la contraseña y valida requisitos de seguridad
     */
    fun onPasswordChange(password: String) {
        val errores = _uiState.value.errores.copy(
            passwordError = ValidadorFormulario.validarPassword(password)
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(password = password),
            errores = errores
        )
    }
    
    /**
     * Verifica si el formulario completo es válido
     */
    fun esFormularioValido(): Boolean {
        val form = _uiState.value.formulario
        val errors = _uiState.value.errores
        
        return form.nombreCompleto.isNotBlank() &&
                form.email.isNotBlank() &&
                form.password.isNotBlank() &&
                errors.nombreCompletoError == null &&
                errors.emailError == null &&
                errors.passwordError == null
    }
    
    /**
     * Intenta registrar al usuario
     */
    fun registrar(onExito: () -> Unit) {
        if (esFormularioValido()) {
            _uiState.value = _uiState.value.copy(estaGuardando = true)
            
            // Simular envío (en la vida real sería: repositorio.registrarUsuario(formulario))
            // Por ahora solo marcamos como exitoso
            _uiState.value = _uiState.value.copy(
                estaGuardando = false,
                registroExitoso = true
            )
            
            onExito()
        }
    }
}

/**
 * Factory para RegistroViewModel
 */
class RegistroViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            return RegistroViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
