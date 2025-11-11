package com.example.labx.domain.model

/**
 * Representa el carrito de compras completo
 */
data class Carrito(
    val items: List<ItemCarrito> = emptyList()
) {
    val cantidadTotal: Int 
        get() = items.sumOf { it.cantidad }

    val precioTotal: Double 
        get() = items.sumOf { it.subtotal }

    val estaVacio: Boolean 
        get() = items.isEmpty()
}
