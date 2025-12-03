package com.example.labx.domain.model

/**
 * Datos del formulario de registro
 */
data class FormularioRegistro(
    val nombreCompleto: String = "",
    val email: String = "",
    val password: String = ""
)
