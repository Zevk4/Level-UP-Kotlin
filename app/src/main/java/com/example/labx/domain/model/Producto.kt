package com.example.labx.domain.model

/**
 * Modelo de dominio para Producto
 */
data class Producto(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val stock: Int
) {
    /**
     * Formatea el precio con separador de miles
     */
    fun precioFormateado(): String {
        val precioEntero = precio.toInt()
        return "$${precioEntero.toString().reversed().chunked(3).joinToString(".").reversed()}"
    }
    
    /**
     * Verifica si hay stock disponible
     */
    val hayStock: Boolean
        get() = stock > 0
}