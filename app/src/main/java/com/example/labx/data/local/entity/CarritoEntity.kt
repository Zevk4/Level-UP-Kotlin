package com.example.labx.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.labx.domain.model.Producto

/**
 * Entidad Room para tabla "carrito"
 * Representa un producto en el carrito del usuario
 */
@Entity(tableName = "carrito")
data class CarritoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productoId: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val stock: Int,
    val cantidad: Int = 1
)

/**
 * Funcion para convertir un Entity a Producto
 */
fun CarritoEntity.toDomain(): Producto {
    return Producto(
        id = productoId,
        nombre = nombre,
        descripcion = descripcion,
        precio = precio,
        imagenUrl = imagenUrl,
        categoria = categoria,
        stock = stock
    )
}