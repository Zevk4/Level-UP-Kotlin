package com.example.labx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.labx.data.local.AppDatabase
import com.example.labx.data.local.PreferenciasManager
import com.example.labx.data.local.ProductoInicializador
import com.example.labx.data.repository.CarritoRepository
import com.example.labx.data.repository.ProductoRepositoryImpl
import com.example.labx.ui.navigation.NavGraph
import com.example.labx.ui.theme.LabxTheme
import com.example.labx.ui.viewmodel.ProductoViewModel
import com.example.labx.ui.viewmodel.ProductoViewModelFactory

/**
 * Punto de entrada de la aplicación
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Crear base de datos
        val database = AppDatabase.getDatabase(applicationContext)
        
        // Inicializar productos de ejemplo (solo primera vez)
        ProductoInicializador.inicializarProductos(applicationContext)
        
        // Crear repositorios
        val productoRepository = ProductoRepositoryImpl(database.productoDao())
        val carritoRepository = CarritoRepository(database.carritoDao())
        
        // Crear PreferenciasManager para sesión admin
        val preferenciasManager = PreferenciasManager(applicationContext)

        setContent {
            LabxTheme {
                Surface {
                    // Crear NavController para gestionar navegación
                    val navController = rememberNavController()
                    
                    // Crear ViewModel con Factory
                    val productoViewModel: ProductoViewModel = viewModel(
                        factory = ProductoViewModelFactory(productoRepository)
                    )
                    
                    // NavGraph: Define todas las pantallas y rutas
                    NavGraph(
                        navController = navController,
                        productoRepository = productoRepository,
                        carritoRepository = carritoRepository,
                        preferenciasManager = preferenciasManager,
                        productoViewModel = productoViewModel
                    )
                }
            }
        }
    }
}