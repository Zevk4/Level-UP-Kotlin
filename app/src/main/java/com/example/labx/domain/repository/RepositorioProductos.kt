package com.example.labx.domain.repository

import com.example.labx.domain.model.Producto
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de productos
 */
interface RepositorioProductos {
    
    /**
     * Obtiene todos los productos como Flow
     */
    fun obtenerProductos(): Flow<List<Producto>>
    
    /**
     * Obtiene un producto por su ID
     */
    suspend fun obtenerProductoPorId(id: Int): Producto?
    
    /**
     * Inserta varios productos en la base de datos
     */
    suspend fun insertarProductos(productos: List<Producto>)
    
    /**
     * Inserta un solo producto
     */
    suspend fun insertarProducto(producto: Producto): Long
    
    /**
     * Actualiza un producto existente
     */
    suspend fun actualizarProducto(producto: Producto)
    
    /**
     * Elimina un producto específico
     */
    suspend fun eliminarProducto(producto: Producto)
    
    /**
     * Elimina todos los productos
     */
    suspend fun eliminarTodosLosProductos()
}
