package com.example.labx.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.labx.domain.model.Rol

/**
 * Se encarga de gestiona datos persistentes simples
 */
class PreferenciasManager(context: Context) {
    
    // Obtener SharedPreferences del sistema
    private val prefs: SharedPreferences = context.getSharedPreferences(
        NOMBRE_ARCHIVO,
        Context.MODE_PRIVATE  // Solo esta app puede leer
    )

    companion object {
        private const val NOMBRE_ARCHIVO = "level_up_commerce_prefs"
        
        // Claves (constantes para evitar typos)
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ROLE = "user_role"
        
        // Credenciales por defecto (en app real, estarían en BD segura)
        const val ADMIN_USERNAME = "admin"
        const val ADMIN_PASSWORD = "admin123"
    }
    
    /**
     * Guarda sesión de usuario (cualquier rol)
     */
    fun guardarSesion(username: String, rol: Rol) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USERNAME, username)
            putString(KEY_USER_ROLE, rol.name)
            apply()  // Guarda en background
        }
    }
    
    // Mantener compatibilidad con código existente de admin por ahora, redirigiendo a nueva lógica
    fun guardarSesionAdmin(username: String) {
        guardarSesion(username, Rol.ADMIN)
    }
    
    /**
     * Verifica si hay un usuario logueado
     */
    fun estaLogueado(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Verifica si el usuario logueado es Admin
     */
    fun estaAdminLogueado(): Boolean {
        return estaLogueado() && obtenerRolUsuario() == Rol.ADMIN
    }
    
    /**
     * Obtiene username del usuario logueado
     */
    fun obtenerUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun obtenerUsernameAdmin(): String? = obtenerUsername()

    /**
     * Obtiene el rol del usuario logueado
     */
    fun obtenerRolUsuario(): Rol? {
        val roleName = prefs.getString(KEY_USER_ROLE, null) ?: return null
        return try {
            Rol.valueOf(roleName)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    /**
     * Cierra sesión
     */
    fun cerrarSesion() {
        prefs.edit().apply {
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_USERNAME)
            remove(KEY_USER_ROLE)
            apply()
        }
    }

    fun cerrarSesionAdmin() = cerrarSesion()
    
    /**
     * Valida credenciales de admin
     * En app real: Consulta a backend con hash de password
     */
    fun validarCredencialesAdmin(username: String, password: String): Boolean {
        return username == ADMIN_USERNAME && password == ADMIN_PASSWORD
    }
}
