package com.example.labx.domain.model

/**
 * Datos del formulario de registro
 */
data class FormularioRegistro(
    val nombreCompleto: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val aceptaTerminos: Boolean = false
)
