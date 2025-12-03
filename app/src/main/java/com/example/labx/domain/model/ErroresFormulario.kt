package com.example.labx.domain.model

/**
 * Errores de validación del formulario
 */
data class ErroresFormulario(
    val nombreCompletoError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
) {
    // Verifica si hay algún error
    fun hayErrores(): Boolean {
        return nombreCompletoError != null ||
                emailError != null ||
                passwordError != null
    }
}
