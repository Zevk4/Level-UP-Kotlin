package com.example.labx.domain.model

/**
 * Usuario: Modelo de dominio para usuarios del sistema
 */
data class Usuario(
    val username: String,
    val password: String,
    val rol: Rol = Rol.USUARIO
)

enum class Rol {
    USUARIO,
    ADMIN
}
