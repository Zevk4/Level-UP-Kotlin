package com.example.labx.data.local

import android.content.Context
import com.example.labx.domain.model.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ProductoInicializador: Carga productos de ejemplo en la BD si está vacía.
 */
object ProductoInicializador {
    
    /**
     * Inserta productos si la base de datos está vacíaaa
     */
    fun inicializarProductos(context: Context) {
        val database = AppDatabase.getDatabase(context)
        val productoDao = database.productoDao()
        
        // Ejecutar en background (no bloquear la UI)
        CoroutineScope(Dispatchers.IO).launch {
            val productosExistentes = productoDao.obtenerProductoPorId(1)
            if (productosExistentes == null) { // Solo insertar si no hay productos
                val productosDeEjemplo = listOf(
                    Producto(
                        id = 1,
                        nombre = "Mouse Logitech G502 Negro",
                        descripcion = "El mouse negro G502 es un ratón para juegos ergonómico y de alto rendimiento de Logitech G, con un sensor óptico HERO de hasta 25.600 DPI, 11 botones programables, iluminación RGB personalizable y un sistema de pesas ajustables.",
                        precio = 25000.0,
                        imagenUrl = "g502",
                        categoria = "Periféricos",
                        stock = 12
                    ),
                    Producto(
                        id = 2,
                        nombre = "Mouse Logitech G502 X Blanco",
                        descripcion = "G502 X es la última adición a la legendaria gama G502. Rediseñado para ofrecer una impresionante reducción de peso, con tan sólo 89 gramos Con nuestros primerísimos interruptores LIGHTFORCE híbridos óptico-mecánicos y un sensor HERO 25K por debajo del micrón.",
                        precio = 45000.0,
                        imagenUrl = "g502x",
                        categoria = "Periféricos",
                        stock = 10
                    ),
                    Producto(
                        id = 3,
                        nombre = "Audífonos Gamer Chinos",
                        descripcion = "Audífonos gaming over-ear con sonido envolvente 7.1, micrófono cancelación de ruido y almohadillas de espuma viscoelástica.",
                        precio = 30000.0,
                        imagenUrl = "audi1",
                        categoria = "Audio",
                        stock = 12
                    ),
                    Producto(
                        id = 4,
                        nombre = "PC Gamer AlienWare",
                        descripcion = "Los PC Alienware de escritorio ofrecen alto rendimiento, potenciados por tarjetas gráficas NVIDIA GeForce RTX serie 50 o RTX 4070 Ti, y procesadores Intel Core Ultra 9 o i7-13700KF.",
                        precio = 45000.0,
                        imagenUrl = "pc",
                        categoria = "Computacion",
                        stock = 9
                    ),
                    Producto(
                        id = 5,
                        nombre = "Play Station 5 Pro",
                        descripcion = "La PlayStation 5 Pro promete una mejora significativa en la experiencia de juego. Ofrecerá gráficos en 4K/8K, trazado de rayos y la posibilidad de jugar a una frecuencia de hasta 120 fps.",
                        precio = 380000.0,
                        imagenUrl = "ps5",
                        categoria = "Consolas",
                        stock = 6
                    ),
                    Producto(
                        id = 6,
                        nombre = "Silla Gamer Secret Lab",
                        descripcion = "Nuestra silla ergonómica tecnológicamente más avanzada. Descubre el diseño multipremiado, la silla de preferencia de los mejores jugadores de esports, profesionales y más de 3 millones de usuarios de todo el mundo.",
                        precio = 189000.0,
                        imagenUrl = "sillagamer",
                        categoria = "Almacenamiento",
                        stock = 20
                    ),
                )
                // Insertar en la db
                productoDao.insertarProductos(productosDeEjemplo.map { it.toEntity() })
            }
        }
    }
}

// Extension function para convertir Producto a ProductoEntity
private fun Producto.toEntity() = com.example.labx.data.local.entity.ProductoEntity(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
    stock = stock
)
