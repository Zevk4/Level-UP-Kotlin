package com.example.labx.data.remote.api

import com.example.labx.data.remote.dto.ProductoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interface del servicio API - Define endpoints disponibles
 */
interface ProductoApiService {

    // GET: Obtener todos los productos
    @GET("api/productos")
    suspend fun obtenerTodosLosProductos(): Response<List<ProductoDto>>

    // GET: Obtener producto por ID
    @GET("api/productos/{id}")
    suspend fun obtenerProductoPorId(
        @Path("id") identificador: Int
    ): Response<ProductoDto>

    // GET: Obtener productos por categoria
    @GET("api/productos/category/{categoria}")
    suspend fun obtenerProductosPorCategoria(
        @Path("categoria") nombreCategoria: String
    ): Response<List<ProductoDto>>

    // POST: Crear nuevo producto
    @POST("api/productos")
    suspend fun agregarProducto(
        @Body nuevoProducto: ProductoDto
    ): Response<ProductoDto>

    // PUT: Actualizar producto existente
    @PUT("api/productos/{id}")
    suspend fun modificarProducto(
        @Path("id") identificador: Int,
        @Body productoActualizado: ProductoDto
    ): Response<ProductoDto>

    // DELETE: Eliminar producto
    @DELETE("api/productos/{id}")
    suspend fun borrarProducto(
        @Path("id") identificador: Int
    ): Response<Unit>
}