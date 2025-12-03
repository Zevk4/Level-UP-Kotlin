package com.example.labx.domain.validator

import com.example.labx.domain.model.ErroresFormulario
import com.example.labx.domain.model.FormularioRegistro

/**
 * Validador de formulario de registro
 */
object ValidadorFormulario {
    
    // Regex para email: algo@algo.com
    private val emailRegex = Regex(
        "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    )
    
    // Regex para contraseña: mínimo 8 caracteres, 1 mayúscula, 1 número
    private val passwordRegex = Regex(
        "^(?=.*[A-Z])(?=.*[0-9]).{8,}$"
    )
    
    /**
     * Valida todo el formulario y devuelve los errores
     */
    fun validarFormulario(formulario: FormularioRegistro): ErroresFormulario {
        return ErroresFormulario(
            nombreCompletoError = validarNombreCompleto(formulario.nombreCompleto),
            emailError = validarEmail(formulario.email),
            passwordError = validarPassword(formulario.password)
        )
    }
    
    fun validarNombreCompleto(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre es obligatorio"
            nombre.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            !nombre.contains(" ") -> "Ingresa tu nombre completo"
            else -> null
        }
    }
    
    fun validarEmail(email: String): String? {
        return when {
            email.isBlank() -> "El email es obligatorio"
            !emailRegex.matches(email) -> "Email inválido (ejemplo: usuario@mail.com)"
            else -> null
        }
    }
    
    fun validarPassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es obligatoria"
            !passwordRegex.matches(password) -> 
                "Contraseña débil (mínimo 8 caracteres, 1 mayúscula y 1 número)"
            else -> null
        }
    }
}
