package com.example.labx.ui.screenimport

import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.labx.data.repository.CarritoRepository
import com.example.labx.domain.model.ItemCarrito
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * CarritoScreen: Muestra todos los productos en el carrito y permite finalizar la compra.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    carritoRepository: CarritoRepository,
    onVolverClick: () -> Unit,
    onProductoClick: (Int) -> Unit
) {
    var mostrarMensajeCompra by remember { mutableStateOf(false) }
    val itemsCarrito by carritoRepository.obtenerCarrito().collectAsState(initial = emptyList())
    val total by carritoRepository.obtenerTotal().collectAsState(initial = 0.0)

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito (${itemsCarrito.size})") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (itemsCarrito.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                scope.launch { carritoRepository.vaciarCarrito() }
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                "Vaciar carrito",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (itemsCarrito.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Fila para el Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            formatearPrecio(total),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Botón de "FINALIZAR COMPRA"
                    Button(
                        onClick = {
                            scope.launch {
                                carritoRepository.vaciarCarrito()
                                mostrarMensajeCompra = true
                                // Espera 2.5 segundos antes de volver a la portada
                                delay(2500)
                                onVolverClick()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("FINALIZAR COMPRA")
                    }
                }

            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (itemsCarrito.isEmpty() && !mostrarMensajeCompra) {
                // Carrito vacío
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🛒", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Tu carrito está vacío", fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onVolverClick) { Text("Ir a comprar") }
                }
            } else if (mostrarMensajeCompra) {
                // 3. Mensaje de compra realizada con éxito
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("✅", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "¡Compra realizada con éxito!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Gracias por preferir Level-UP-Gammer")
                }
            } else {
                // Lista de productos en el carrito
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(itemsCarrito) { item ->
                        CarritoItemCard(
                            item = item,
                            onCantidadChange = { nuevaCantidad ->
                                scope.launch {
                                    carritoRepository.modificarCantidad(item.producto.id, nuevaCantidad)
                                }
                            },
                            onEliminarClick = {
                                scope.launch {
                                    carritoRepository.eliminarProducto(item.producto.id)
                                }
                            },
                            onClick = { onProductoClick(item.producto.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * CarritoItemCard: Muestra un producto en el carrito con controles de cantidad
 */
@Composable
fun CarritoItemCard(
    item: ItemCarrito,
    onCantidadChange: (Int) -> Unit,
    onEliminarClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            val context = LocalContext.current
            val imageResId = context.resources.getIdentifier(
                item.producto.imagenUrl,
                "drawable",
                context.packageName
            )

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(if (imageResId != 0) imageResId else item.producto.imagenUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.producto.nombre,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Información y controles
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(item.producto.nombre, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "Precio: ${formatearPrecio(item.producto.precio)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (item.cantidad > 1) onCantidadChange(item.cantidad - 1) },
                        modifier = Modifier.size(32.dp),
                        enabled = item.cantidad > 1
                    ) {
                        Icon(
                            Icons.Default.Clear, "Disminuir",
                            tint = if (item.cantidad > 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    Text(
                        "${item.cantidad}", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.widthIn(min = 30.dp).wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    IconButton(
                        onClick = { onCantidadChange(item.cantidad + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, "Aumentar", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Text(
                    "Subtotal: ${formatearPrecio(item.subtotal)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onEliminarClick) {
                Icon(Icons.Default.Delete, "Eliminar producto", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

/**
 * formatear precio
 */
fun formatearPrecio(precio: Double): String {
    val precioEntero = precio.toInt()
    return "$${precioEntero.toString().reversed().chunked(3).joinToString(".").reversed()}"
}
