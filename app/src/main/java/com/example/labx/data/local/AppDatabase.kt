package com.example.labx.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.labx.data.local.dao.CarritoDao
import com.example.labx.data.local.dao.ProductoDao
import com.example.labx.data.local.entity.CarritoEntity
import com.example.labx.data.local.entity.ProductoEntity

/**
 * Database principal de la app
 */
@Database(
    entities = [CarritoEntity::class, ProductoEntity::class],
    version = 2, // Incrementado por agregar ProductoEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provee acceso al DAO de carrito
     */
    abstract fun carritoDao(): CarritoDao
    /**
     * Provee acceso al DAO de productos
     */
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene instancia única de la base de datos
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "level_up_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}