package com.example.labx.domain.model

/**
 * Usuario: Modelo de dominio para usuarios del sistema
 */
data class Usuario(
    val username: String,
    val email: String, // Nuevo campo
    val password: String,
    val rol: Rol = Rol.CLIENTE
)

enum class Rol {
    ADMIN,
    CLIENTE
}
