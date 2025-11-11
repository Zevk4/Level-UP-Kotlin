package com.example.labx.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.labx.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de productos
 */
@Dao
interface ProductoDao {
    /**
     * Obtiene todos los productos ordenados por nombre
     */
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodosLosProductos(): Flow<List<ProductoEntity>>
    
    /**
     * Obtiene producto por ID
     */
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerProductoPorId(id: Int): ProductoEntity?
    
    /**
     * Inserta varios productos, si ya existen los reemplaza
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)
    
    /**
     * Inserta un solo producto y retorna el ID del producto insertado
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductoEntity): Long
    
    /**
     * Actualiza un producto existente
     */
    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)
    
    /**
     * Elimina un producto específico
     */
    @Delete
    suspend fun eliminarProducto(producto: ProductoEntity)
    
    /**
     * Elimina todos los productos
     */
    @Query("DELETE FROM productos")
    suspend fun eliminarTodosLosProductos()
}
