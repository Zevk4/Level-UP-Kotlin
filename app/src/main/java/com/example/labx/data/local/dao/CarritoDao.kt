package com.example.labx.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.labx.data.local.entity.CarritoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para tabla carrito
 * Define las operaciones SQL disponibles
 */
@Dao
interface CarritoDao {

    /**
     * Obtiene todos los items del carrito en tiempo real
     */
    @Query("SELECT * FROM carrito")
    fun obtenerTodo(): Flow<List<CarritoEntity>>

    /**
     * Inserta un nuevo producto al carrito
     */
    @Insert
    suspend fun insertar(item: CarritoEntity)

    /**
     * Elimina todos los items del carrito
     */
    @Query("DELETE FROM carrito")
    suspend fun vaciar()

    /**
     * Calcula el total sumando todos los precios * cantidades
     */
    @Query("SELECT SUM(precio * cantidad) FROM carrito")
    fun obtenerTotal(): Flow<Double?>

    /**
     * Busca si un producto ya está en el carrito
     */
    @Query("SELECT * FROM carrito WHERE productoId = :productoId LIMIT 1")
    suspend fun obtenerPorProductoId(productoId: Int): CarritoEntity?

    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    @Query("UPDATE carrito SET cantidad = :cantidad WHERE productoId = :productoId")
    suspend fun actualizarCantidad(productoId: Int, cantidad: Int)

    /**
     * Elimina un producto específico del carrito
     */
    @Query("DELETE FROM carrito WHERE productoId = :productoId")
    suspend fun eliminarProducto(productoId: Int)
}