package com.example.labx.ui.screen

// Importaciones necesarias para la nueva funcionalidad
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.util.Log
import com.example.labx.R
import com.example.labx.data.repository.CarritoRepository
import com.example.labx.data.repository.ProductoRepositoryImpl
import com.example.labx.domain.model.Producto
import com.example.labx.ui.viewmodel.ProductoViewModel
import com.example.labx.ui.viewmodel.ProductoViewModelFactory

/**
 * HomeScreen: Pantalla principal de la app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    productoRepository: ProductoRepositoryImpl,
    carritoRepository: CarritoRepository,
    onProductoClick: (Int) -> Unit,
    onCarritoClick: () -> Unit,
    onRegistroClick: () -> Unit,
    onVolverPortada: () -> Unit
) {

    val viewModel: ProductoViewModel = viewModel(
        factory = ProductoViewModelFactory(productoRepository)
    )

    val uiState by viewModel.uiState.collectAsState()
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }

    val productosFiltrados = remember(uiState.productos, textoBusqueda, categoriaSeleccionada) {
        uiState.productos.filter { producto ->
            val coincideTexto = textoBusqueda.isBlank() ||
                    producto.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    producto.descripcion.contains(textoBusqueda, ignoreCase = true)
            val coincideCategoria = categoriaSeleccionada == null ||
                    producto.categoria == categoriaSeleccionada
            coincideTexto && coincideCategoria
        }
    }

    val categorias = remember(uiState.productos) {
        uiState.productos.map { it.categoria }.distinct().sorted()
    }

    val scope = rememberCoroutineScope()

    // Función local para manejar el agregado al carrito (pasada al hijo)
    val onAgregarAlCarrito: (Producto) -> Unit = { producto ->
        scope.launch {
            carritoRepository.agregarProducto(producto)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos Disponibles") },
                navigationIcon = {
                    IconButton(onClick = onVolverPortada) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver a Portada"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRegistroClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Registro"
                        )
                    }
                    IconButton(onClick = onCarritoClick) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrito"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.estaCargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.cargarProductos() }) {
                            Text("Reintentar")
                        }
                    }
                }

                uiState.productos.isEmpty() -> {
                    Text(
                        text = "No hay productos disponibles",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // ... (Campo de búsqueda, chips y contador de resultados se mantienen igual) ...
                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { textoBusqueda = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Buscar productos...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar"
                                )
                            },
                            trailingIcon = {
                                if (textoBusqueda.isNotEmpty()) {
                                    IconButton(onClick = { textoBusqueda = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Limpiar"
                                        )
                                    }
                                }
                            },
                            singleLine = true
                        )

                        if (categorias.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = categoriaSeleccionada == null,
                                    onClick = { categoriaSeleccionada = null },
                                    label = { Text("Todos") }
                                )
                                categorias.forEach { categoria ->
                                    FilterChip(
                                        selected = categoriaSeleccionada == categoria,
                                        onClick = {
                                            categoriaSeleccionada = if (categoriaSeleccionada == categoria) {
                                                null
                                            } else {
                                                categoria
                                            }
                                        },
                                        label = { Text(categoria) }
                                    )
                                }
                            }
                        }

                        if (textoBusqueda.isNotEmpty() || categoriaSeleccionada != null) {
                            Text(
                                text = "${productosFiltrados.size} resultado(s)",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Lista de productos (GRID MODERNO)
                        ProductGrid(
                            productos = productosFiltrados,
                            onProductoClick = onProductoClick,
                            onAgregarAlCarrito = onAgregarAlCarrito
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductGrid(
    productos: List<Producto>,
    onProductoClick: (Int) -> Unit,
    onAgregarAlCarrito: (Producto) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2), // 2 columnas
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        modifier = Modifier.fillMaxSize()
    ) {
        items(productos) { producto ->
            ProductItemCard(
                producto = producto,
                onProductoClick = onProductoClick,
                onAgregarAlCarrito = onAgregarAlCarrito
            )
        }
    }
}

@Composable
fun ProductItemCard(
    producto: Producto,
    onProductoClick: (Int) -> Unit,
    onAgregarAlCarrito: (Producto) -> Unit
) {
    // Estado local para mostrar confirmación breve al agregar
    var mostrarMensaje by remember { mutableStateOf(false) }

    // Efecto para ocultar el mensaje
    if (mostrarMensaje) {
        LaunchedEffect(Unit) {
            delay(1500)
            mostrarMensaje = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductoClick(producto.id) },
        shape = RoundedCornerShape(16.dp), // Bordes más redondeados
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Contenedor de la imagen con Badges
            Box(modifier = Modifier.height(160.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(producto.imagenUrl)
                        .crossfade(true)
                        .error(R.drawable.icon) // Fallback icon si falla
                        .build(),
                    contentDescription = producto.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Badge de Categoría (flotante)
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = producto.categoria,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Badge de Stock (si es bajo)
                if (producto.stock > 0 && producto.stock < 5) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "¡Quedan ${producto.stock}!",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Información del producto
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = producto.precioFormateado(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón de acción (Ancho completo o ícono)
                Button(
                    onClick = {
                        onAgregarAlCarrito(producto)
                        mostrarMensaje = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mostrarMensaje) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (mostrarMensaje) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    enabled = producto.hayStock
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (mostrarMensaje) "¡Agregado!" else (if (producto.hayStock) "Agregar" else "Agotado"))
                }
            }
        }
    }
}
