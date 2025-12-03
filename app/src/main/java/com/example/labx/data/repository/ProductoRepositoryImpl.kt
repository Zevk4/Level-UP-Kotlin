package com.example.labx.data.repository

import android.util.Log
import com.example.labx.data.local.dao.ProductoDao
import com.example.labx.data.local.entity.toEntity
import com.example.labx.data.local.entity.toProducto
import com.example.labx.data.remote.api.ProductoApiService
import com.example.labx.data.remote.dto.aDto
import com.example.labx.data.remote.dto.aModelo
import com.example.labx.domain.model.Producto
import com.example.labx.domain.repository.RepositorioProductos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.net.UnknownHostException

/**
 * Implementación del repositorio de productos
 */
class ProductoRepositoryImpl(
    private val productoDao: ProductoDao,
    private val apiService: ProductoApiService
) : RepositorioProductos {

    companion object {
        private const val TAG = "ProductoRepository"
    }

    override fun obtenerProductos(): Flow<List<Producto>> = flow {
        try {
            Log.d(TAG, "Intentando obtener productos desde API REST...")

            // Hacer peticion a la API
            val respuesta = apiService.obtenerTodosLosProductos()

            // Verificar si fue exitosa
            if (respuesta.isSuccessful) {
                val cuerpoRespuesta = respuesta.body()

                if (cuerpoRespuesta != null) {
                    // Mapear DTOs a modelos de dominio
                    val listaProductos = cuerpoRespuesta.map { it.aModelo() }
                    val entidades = listaProductos.map { it.toEntity() }
                    
                    // Actualizar base de datos local con datos frescos
                    productoDao.insertarProductos(entidades)

                    Log.d(TAG, "✓ Productos obtenidos de API y guardados localmente: ${listaProductos.size} items")
                    emit(listaProductos)

                } else {
                    // Respuesta exitosa pero sin datos
                    Log.w(TAG, "⚠ Respuesta vacía, usando datos locales")
                    usarDatosLocales(this)
                }

            } else {
                // Error HTTP (4xx, 5xx)
                Log.w(TAG, "⚠ Error HTTP ${respuesta.code()}, usando datos locales")
                usarDatosLocales(this)
            }

        } catch (excepcion: UnknownHostException) {
            // Sin internet o host invalido
            Log.e(TAG, "✗ Sin conexion a internet, usando datos locales")
            usarDatosLocales(this)

        } catch (excepcion: IOException) {
            // Error de red (timeout, etc)
            Log.e(TAG, "✗ Error de red, usando datos locales")
            usarDatosLocales(this)

        } catch (excepcion: Exception) {
            // Error inesperado
            Log.e(TAG, "✗ Error inesperado: ${excepcion.message}")
            usarDatosLocales(this)
        }
    }

    override fun obtenerProductosPorCategoria(categoria: String): Flow<List<Producto>> = flow {
        try {
            Log.d(TAG, "Solicitando productos de categoría '$categoria' a la API...")
            
            val respuesta = apiService.obtenerProductosPorCategoria(categoria)
            
            if (respuesta.isSuccessful && respuesta.body() != null) {
                val listaProductos = respuesta.body()!!.map { it.aModelo() }
                Log.d(TAG, "✓ ${listaProductos.size} productos obtenidos de categoría '$categoria'")
                emit(listaProductos)
            } else {
                Log.w(TAG, "⚠ Error API categoría (Code: ${respuesta.code()}), filtrando localmente")
                filtrarLocalmente(this, categoria)
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error al obtener categoría: ${e.message}, filtrando localmente")
            filtrarLocalmente(this, categoria)
        }
    }

    private suspend fun filtrarLocalmente(
        flowCollector: kotlinx.coroutines.flow.FlowCollector<List<Producto>>,
        categoria: String
    ) {
        productoDao.obtenerTodosLosProductos().collect { listaEntidades ->
            // Filtramos en memoria ya que el DAO no tiene consulta específica por categoría aún
            val productosFiltrados = listaEntidades
                .map { it.toProducto() }
                .filter { it.categoria.equals(categoria, ignoreCase = true) }
            
            Log.d(TAG, "✓ Filtrado local: ${productosFiltrados.size} items para '$categoria'")
            flowCollector.emit(productosFiltrados)
        }
    }

    private suspend fun usarDatosLocales(
        flowCollector: kotlinx.coroutines.flow.FlowCollector<List<Producto>>
    ) {
        productoDao.obtenerTodosLosProductos().collect { listaEntidades ->
            val productosLocales = listaEntidades.map { it.toProducto() }

            if (productosLocales.isEmpty()) {
                Log.w(TAG, "Base de datos local está vacía")
            } else {
                Log.d(TAG, "✓ Productos de cache local: ${productosLocales.size} items")
            }

            flowCollector.emit(productosLocales)
        }
    }

    override suspend fun obtenerProductoPorId(id: Int): Producto? {
        // Intentar obtener de la API primero
        try {
            Log.d(TAG, "Buscando producto ID: $id en API...")
            val respuesta = apiService.obtenerProductoPorId(id)
            
            if (respuesta.isSuccessful && respuesta.body() != null) {
                val producto = respuesta.body()!!.aModelo()
                
                // Actualizar caché local
                productoDao.insertarProducto(producto.toEntity())
                Log.d(TAG, "✓ Producto ID: $id actualizado desde API")
                
                return producto
            } else {
                 Log.w(TAG, "⚠ Error al obtener producto de API (Code: ${respuesta.code()}), usando local")
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error de red al obtener producto: ${e.message}")
        }

        // Fallback: Buscar en base de datos local
        return productoDao.obtenerProductoPorId(id)?.toProducto()
    }

    override suspend fun insertarProductos(productos: List<Producto>) {
        val entities = productos.map { it.toEntity() }
        productoDao.insertarProductos(entities)
    }

    override suspend fun insertarProducto(producto: Producto): Long {
        return try {
            Log.d(TAG, "Creando producto: ${producto.nombre} en API...")

            val productoDto = producto.aDto()
            val respuesta = apiService.agregarProducto(productoDto)

            if (respuesta.isSuccessful) {
                Log.d(TAG, "✓ Producto creado en API")
                // Si la API devuelve el objeto creado (con ID nuevo), deberíamos usar ese ID
                val productoCreado = respuesta.body()?.aModelo() ?: producto
                
                val idLocal = productoDao.insertarProducto(productoCreado.toEntity())
                Log.d(TAG, "✓ Producto guardado localmente con ID: $idLocal")
                idLocal
            } else {
                Log.w(TAG, "⚠ Error en API, guardando solo localmente")
                productoDao.insertarProducto(producto.toEntity())
            }

        } catch (excepcion: Exception) {
            Log.e(TAG, "✗ Error de red, guardando solo localmente")
            productoDao.insertarProducto(producto.toEntity())
        }
    }

    override suspend fun actualizarProducto(producto: Producto) {
        try {
            Log.d(TAG, "Actualizando producto: ${producto.nombre} en API...")
            val productoDto = producto.aDto()
            val respuesta = apiService.modificarProducto(producto.id, productoDto)

            if (respuesta.isSuccessful) {
                Log.d(TAG, "✓ Producto actualizado en API")
            } else {
                Log.w(TAG, "⚠ Error en API al actualizar, actualizando solo localmente")
            }
        } catch (excepcion: Exception) {
            Log.e(TAG, "✗ Error de red al actualizar, actualizando solo localmente: ${excepcion.message}")
        }
        // Siempre actualizamos localmente para mantener la UI consistente
        productoDao.actualizarProducto(producto.toEntity())
    }

    override suspend fun eliminarProducto(producto: Producto) {
        try {
            Log.d(TAG, "Eliminando producto: ${producto.nombre} en API...")
            val respuesta = apiService.borrarProducto(producto.id)

            if (respuesta.isSuccessful) {
                Log.d(TAG, "✓ Producto eliminado en API")
            } else {
                Log.w(TAG, "⚠ Error en API al eliminar, eliminando solo localmente")
            }
        } catch (excepcion: Exception) {
            Log.e(TAG, "✗ Error de red al eliminar, eliminando solo localmente: ${excepcion.message}")
        }
        // Siempre eliminamos localmente para mantener la UI consistente
        productoDao.eliminarProducto(producto.toEntity())
    }

    override suspend fun eliminarTodosLosProductos() {
        productoDao.eliminarTodosLosProductos()
    }
}
