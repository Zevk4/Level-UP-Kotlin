package com.example.labx.data.remote.dto

import com.example.labx.domain.model.Producto
import com.google.gson.annotations.SerializedName

/**
 * DTO para mapear JSON de la API a objetos Kotlin.
 * Actualizado según la respuesta real de la API.
 */
data class ProductoDto(
    @SerializedName("id")
    val identificador: Int?,

    @SerializedName("nombre")
    val titulo: String?,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("precio")
    val precio: Double?, // Gson convertirá "1800.00" a Double automáticamente

    @SerializedName("imagen")
    val urlImagen: String?,

    @SerializedName("categoria_nombre") // Antes "categoria" - El JSON usa categoria_nombre
    val categoria: String?,

    @SerializedName("stock")
    val stock: Int?
)

// Extension function: DTO → Modelo de dominio
fun ProductoDto.aModelo(): Producto {
    return Producto(
        id = this.identificador ?: 0,
        nombre = this.titulo ?: "Sin nombre",
        descripcion = this.descripcion ?: "Sin descripción",
        precio = this.precio ?: 0.0,
        imagenUrl = this.urlImagen ?: "",
        categoria = this.categoria ?: "General",
        stock = this.stock ?: 0 // Ahora usamos el stock real de la API
    )
}

// Extension function: Modelo de dominio → DTO
fun Producto.aDto(): ProductoDto {
    return ProductoDto(
        identificador = this.id,
        titulo = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio,
        urlImagen = this.imagenUrl,
        categoria = this.categoria, // Mapeamos el nombre de la categoría a categoria_nombre
        stock = this.stock
    )
}
