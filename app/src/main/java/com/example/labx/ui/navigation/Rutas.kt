package com.example.labx.ui.navigation

/**
 * Rutas: Nombres de las pantallas para navegación
 */
object Rutas {
    const val PORTADA = "portada"
    const val HOME = "home"
    const val DETALLE = "detalle"
    const val CARRITO = "carrito"
    
    // Flujo de Autenticación
    const val AUTH_SELECTOR = "auth_selector"
    const val LOGIN_USUARIO = "login_usuario"
    const val REGISTRO = "registro"

    // Rutas de administración
    const val LOGIN_ADMIN = "login_admin"
    const val PANEL_ADMIN = "panel_admin"
    const val FORMULARIO_PRODUCTO = "formulario_producto?productoId={productoId}"

    // Funciones helper para pasar argumentos
    fun detalleConId(id: Int) = "detalle/$id"
    fun formularioEditar(id: Int) = "formulario_producto?productoId=$id"
}