package com.example.labx.data.repository

import com.example.labx.data.local.dao.CarritoDao
import com.example.labx.data.local.entity.CarritoEntity
import com.example.labx.data.local.entity.toDomain
import com.example.labx.domain.model.ItemCarrito
import com.example.labx.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository del carrito con gestión de cantidad
 */
class CarritoRepository(private val carritoDao: CarritoDao) {

    /**
     * Obtiene todos los items del carrito como ItemCarrito
     */
    fun obtenerCarrito(): Flow<List<ItemCarrito>> {
        return carritoDao.obtenerTodo()
            .map { entities -> 
                entities.map { entity ->
                    ItemCarrito(
                        producto = entity.toDomain(),
                        cantidad = entity.cantidad
                    )
                }
            }
    }

    /**
     * MEJORADO: Agrega o incrementa la cantidad de un producto
     */
    suspend fun agregarProducto(producto: Producto, cantidad: Int = 1) {
        // Paso 1: Buscar si ya existe
        val existente = carritoDao.obtenerPorProductoId(producto.id)
        
        if (existente != null) {
            // Caso A: Ya existe → sumar cantidad
            val nuevaCantidad = existente.cantidad + cantidad
            carritoDao.actualizarCantidad(producto.id, nuevaCantidad)
        } else {
            // Caso B: No existe → insertar nuevo
            val entity = CarritoEntity(
                productoId = producto.id,
                nombre = producto.nombre,
                descripcion = producto.descripcion,
                precio = producto.precio,
                imagenUrl = producto.imagenUrl,
                categoria = producto.categoria,
                stock = producto.stock,
                cantidad = cantidad
            )
            carritoDao.insertar(entity)
        }
    }

    /**
     * Modifica la cantidad de un producto ya en el carrito
     */
    suspend fun modificarCantidad(productoId: Int, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            // Si la cantidad es 0 o negativa → eliminar
            eliminarProducto(productoId)
        } else {
            // Actualizar a la nueva cantidad
            carritoDao.actualizarCantidad(productoId, nuevaCantidad)
        }
    }

    /**
     * Elimina un producto específico del carrito
     */
    suspend fun eliminarProducto(productoId: Int) {
        carritoDao.eliminarProducto(productoId)
    }

    /**
     * Vacía el carrito completo
     */
    suspend fun vaciarCarrito() {
        carritoDao.vaciar()
    }

    /**
     * Obtiene el total del carrito
     */
    fun obtenerTotal(): Flow<Double> {
        return carritoDao.obtenerTotal()
            .map { it ?: 0.0 }
    }
}