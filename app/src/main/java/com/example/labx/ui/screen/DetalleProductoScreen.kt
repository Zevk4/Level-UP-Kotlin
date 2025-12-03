package com.example.labx.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.labx.data.repository.CarritoRepository
import com.example.labx.data.repository.ProductoRepositoryImpl
import com.example.labx.domain.model.Producto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * DetalleProductoScreen: Rediseño premium y moderno
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: Int,
    productoRepository: ProductoRepositoryImpl,
    carritoRepository: CarritoRepository,
    onVolverClick: () -> Unit
) {
    // Estado del producto
    var producto by remember { mutableStateOf<Producto?>(null) }
    var estaCargando by remember { mutableStateOf(true) }
    var mostrarMensaje by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Cargar producto al crear la pantalla
    LaunchedEffect(productoId) {
        estaCargando = true
        producto = productoRepository.obtenerProductoPorId(productoId)
        estaCargando = false
    }

    // Fondo general oscuro suave (Surface color para dark mode)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            // TopBar transparente/integrada
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onVolverClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            // Barra inferior fija para la acción principal
            if (producto != null) {
                AddToCartBottomBar(
                    producto = producto!!,
                    onAddToCart = {
                        scope.launch {
                            carritoRepository.agregarProducto(producto!!)
                            mostrarMensaje = true
                        }
                    },
                    mostrarMensaje = mostrarMensaje
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplicar padding del Scaffold
        ) {
            when {
                estaCargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                producto == null -> {
                    Text(
                        text = "Producto no encontrado",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                else -> {
                    // Contenido Principal con Scroll
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(bottom = 100.dp) // Espacio para la BottomBar
                    ) {
                        // 1. Sección de Imagen Hero
                        ProductHeroImage(producto!!)

                        // 2. Sección de Información
                        ProductInfoSection(producto!!)
                    }
                }
            }
            
            // Snack/Toast personalizado flotante para confirmación
            if (mostrarMensaje) {
                LaunchedEffect(Unit) {
                    delay(2000)
                    mostrarMensaje = false
                }
            }
        }
    }
}

@Composable
fun ProductHeroImage(producto: Producto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp) // Altura generosa para el "Hero"
    ) {
        // Fondo decorativo sutil detrás de la imagen (gradiente radial)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

        val context = LocalContext.current
        // Carga de imagen
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(producto.imagenUrl)
                .crossfade(true)
                .build(),
            contentDescription = producto.nombre,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ProductInfoSection(producto: Producto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-20).dp) // Sutil superposición visual hacia arriba
    ) {
        // Categoría (Chip)
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = producto.categoria.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        // Título Grande y Audaz
        Text(
            text = producto.nombre,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stock Badge (Indicador Visual)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val stockColor = if (producto.hayStock) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
            val stockText = if (producto.stock > 10) "En Stock (${producto.stock})" else if (producto.stock > 0) "¡Últimas ${producto.stock} unidades!" else "Agotado"
            
            Icon(
                imageVector = if(producto.hayStock) Icons.Default.CheckCircle else Icons.Default.ShoppingCart, // Icono placeholder
                contentDescription = null,
                tint = stockColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stockText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = stockColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(24.dp))

        // Descripción
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = producto.descripcion,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun AddToCartBottomBar(
    producto: Producto,
    onAddToCart: () -> Unit,
    mostrarMensaje: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Precio destacado
            Column {
                Text(
                    text = "Precio Total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = producto.precioFormateado(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black, // Muy grueso para destacar
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Botón de Acción (CTA)
            Button(
                onClick = onAddToCart,
                enabled = producto.hayStock,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mostrarMensaje) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 1.dp
                )
            ) {
                AnimatedContent(targetState = mostrarMensaje, label = "ButtonText") { success ->
                    if (success) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("¡Agregado!", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = if (producto.hayStock) "Agregar al Carrito" else "Agotado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
