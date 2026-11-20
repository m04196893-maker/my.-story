package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = GlowingEmber,
    onPrimary = OvenCharcoal,
    primaryContainer = Color(0xFF5E2016),
    onPrimaryContainer = Color(0xFFFFDAD2),
    secondary = WarmSaffron,
    onSecondary = OvenCharcoal,
    secondaryContainer = Color(0xFF4B3219),
    onSecondaryContainer = Color(0xFFFFDFBA),
    tertiary = BasilGreenLight,
    onTertiary = Color(0xFF0A2E14),
    tertiaryContainer = Color(0xFF1A4726),
    onTertiaryContainer = Color(0xFFC8F5D1),
    background = OvenCharcoal,
    onBackground = CreamInk,
    surface = OvenSlate,
    onSurface = CreamInk,
    surfaceVariant = OvenCard,
    onSurfaceVariant = MutedAsh,
    outline = Color(0xFF4E3E36),
)

private val LightColorScheme = lightColorScheme(
    primary = TomatoRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD4),
    onPrimaryContainer = Color(0xFF410903),
    secondary = BrickEmber,
    onSecondary = Color.White,
    secondaryContainer = ToastedCrust,
    onSecondaryContainer = Color(0xFF3E1D09),
    tertiary = BasilGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1F2D8),
    onTertiaryContainer = Color(0xFF0A2E14),
    background = MozzarellaCream,
    onBackground = EspressoInk,
    surface = DoughParchment,
    onSurface = EspressoInk,
    surfaceVariant = ToastedCrust,
    onSurfaceVariant = WarmTaupe,
    outline = OliveBorder,
)

private val FornoShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun FornoPizzaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FornoTypography,
        shapes = FornoShapes,
        content = content,
    )
}
