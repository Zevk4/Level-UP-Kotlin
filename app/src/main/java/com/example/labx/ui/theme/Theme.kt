package com.example.labx.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- ASIGNACIÓN DE COLORES PARA EL TEMA OSCURO ---
private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,                // Color para botones principales, acentos importantes.
    onPrimary = PureBlack,                 // Color del texto y los iconos SOBRE el color primario (negro para contraste con el azul).

    secondary = NeonGreen,                 // Color secundario para acentos, interruptores, etc.
    onSecondary = PureBlack,               // Texto/iconos SOBRE el color secundario (negro para contraste con el verde).

    background = PureBlack,                // Fondo principal de las pantallas.
    onBackground = LightGreyText,          // Color principal del texto SOBRE el fondo.

    surface = LightGreyCard,               // Color de las "superficies" como tarjetas (Cards), menús, etc.
    onSurface = LightGreyText,             // Color del texto SOBRE esas superficies.

    secondaryContainer = DarkGrey,         // Un color para contenedores que necesiten destacar menos que el primario.
    onSecondaryContainer = LightGreyText   // Texto sobre ese contenedor.
)

// --- Mantenemos un tema claro de respaldo---
private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    secondary = NeonGreen,
    background = Color(0xFFF0F0F0),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun LabxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Lo dejamos en false para usar siempre nuestro tema.
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Forzamos el tema oscuro para que coincida con tu paleta.
        true -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Color de la barra de estado superior.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
