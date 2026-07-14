package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 1. Cyber Fusion (Default Cyberpunk)
private val CyberDarkColorScheme = darkColorScheme(
    primary = CyberBlue,
    secondary = CyberTeal,
    tertiary = CyberPurple,
    background = DeepObsidian,
    surface = DeepObsidian,
    surfaceContainer = DarkCard,
    surfaceContainerHigh = DarkCard,
    surfaceContainerLowest = DeepObsidian,
    onPrimary = Color.White,
    onSecondary = DeepObsidian,
    onBackground = TextWhite,
    onSurface = TextWhite,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextMuted
)

private val CyberLightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = CyberTeal,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceContainer = LightCard,
    surfaceContainerHigh = LightCard,
    surfaceContainerLowest = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1E202B),
    onSurface = Color(0xFF1E202B),
    surfaceVariant = LightCard,
    onSurfaceVariant = Color(0xFF535661)
)

// 2. Sunset Horizon (Warm/Fire vibe)
private val SunsetDarkColorScheme = darkColorScheme(
    primary = SunsetOrange,
    secondary = SunsetAmber,
    tertiary = SunsetRose,
    background = SunsetDarkBg,
    surface = SunsetDarkBg,
    surfaceContainer = SunsetDarkCard,
    surfaceContainerHigh = SunsetDarkCard,
    surfaceContainerLowest = SunsetDarkBg,
    onPrimary = Color.White,
    onSecondary = SunsetDarkBg,
    onBackground = Color(0xFFFFEBE6),
    onSurface = Color(0xFFFFEBE6),
    surfaceVariant = SunsetDarkCard,
    onSurfaceVariant = Color(0xFFC4A8A1)
)

private val SunsetLightColorScheme = lightColorScheme(
    primary = SunsetOrange,
    secondary = SunsetAmber,
    tertiary = SunsetRose,
    background = SunsetLightBg,
    surface = Color.White,
    surfaceContainer = SunsetLightCard,
    surfaceContainerHigh = SunsetLightCard,
    surfaceContainerLowest = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF2C1E1B),
    onSurface = Color(0xFF2C1E1B),
    surfaceVariant = SunsetLightCard,
    onSurfaceVariant = Color(0xFF70524C)
)

// 3. Forest Moss (Earthy/Nature vibe)
private val ForestDarkColorScheme = darkColorScheme(
    primary = ForestSage,
    secondary = ForestMint,
    tertiary = ForestEmerald,
    background = ForestDarkBg,
    surface = ForestDarkBg,
    surfaceContainer = ForestDarkCard,
    surfaceContainerHigh = ForestDarkCard,
    surfaceContainerLowest = ForestDarkBg,
    onPrimary = Color.White,
    onSecondary = ForestDarkBg,
    onBackground = Color(0xFFE8F5E9),
    onSurface = Color(0xFFE8F5E9),
    surfaceVariant = ForestDarkCard,
    onSurfaceVariant = Color(0xFFA2B5AA)
)

private val ForestLightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    secondary = ForestSage,
    tertiary = ForestEmerald,
    background = ForestLightBg,
    surface = Color.White,
    surfaceContainer = ForestLightCard,
    surfaceContainerHigh = ForestLightCard,
    surfaceContainerLowest = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1B2E21),
    onSurface = Color(0xFF1B2E21),
    surfaceVariant = ForestLightCard,
    onSurfaceVariant = Color(0xFF4C6B56)
)

// 4. Cosmic Nebula (Space vibe)
private val CosmicDarkColorScheme = darkColorScheme(
    primary = CosmicLavender,
    secondary = CosmicMagenta,
    tertiary = CosmicIndigo,
    background = CosmicDarkBg,
    surface = CosmicDarkBg,
    surfaceContainer = CosmicDarkCard,
    surfaceContainerHigh = CosmicDarkCard,
    surfaceContainerLowest = CosmicDarkBg,
    onPrimary = Color.White,
    onSecondary = CosmicDarkBg,
    onBackground = Color(0xFFF3E5F5),
    onSurface = Color(0xFFF3E5F5),
    surfaceVariant = CosmicDarkCard,
    onSurfaceVariant = Color(0xFFBCAAA4)
)

private val CosmicLightColorScheme = lightColorScheme(
    primary = Color(0xFF7B2CBF),
    secondary = CosmicLavender,
    tertiary = CosmicMagenta,
    background = CosmicLightBg,
    surface = Color.White,
    surfaceContainer = CosmicLightCard,
    surfaceContainerHigh = CosmicLightCard,
    surfaceContainerLowest = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF210F35),
    onSurface = Color(0xFF210F35),
    surfaceVariant = CosmicLightCard,
    onSurfaceVariant = Color(0xFF5E457F)
)

// 5. Mint Breeze (Arctic/Teal vibe)
private val MintDarkColorScheme = darkColorScheme(
    primary = MintTeal,
    secondary = MintIce,
    tertiary = MintAzure,
    background = MintDarkBg,
    surface = MintDarkBg,
    surfaceContainer = MintDarkCard,
    surfaceContainerHigh = MintDarkCard,
    surfaceContainerLowest = MintDarkBg,
    onPrimary = Color.White,
    onSecondary = MintDarkBg,
    onBackground = Color(0xFFE0F7FA),
    onSurface = Color(0xFFE0F7FA),
    surfaceVariant = MintDarkCard,
    onSurfaceVariant = Color(0xFF8FA1AB)
)

private val MintLightColorScheme = lightColorScheme(
    primary = MintAzure,
    secondary = MintTeal,
    tertiary = MintIce,
    background = MintLightBg,
    surface = Color.White,
    surfaceContainer = MintLightCard,
    surfaceContainerHigh = MintLightCard,
    surfaceContainerLowest = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0F2530),
    onSurface = Color(0xFF0F2530),
    surfaceVariant = MintLightCard,
    onSurfaceVariant = Color(0xFF465D69)
)

@Composable
fun MyApplicationTheme(
    appThemeId: String = "cyber_fusion",
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            if (darkTheme) {
                when (appThemeId) {
                    "sunset_horizon" -> SunsetDarkColorScheme
                    "forest_moss" -> ForestDarkColorScheme
                    "cosmic_nebula" -> CosmicDarkColorScheme
                    "mint_breeze" -> MintDarkColorScheme
                    else -> CyberDarkColorScheme
                }
            } else {
                when (appThemeId) {
                    "sunset_horizon" -> SunsetLightColorScheme
                    "forest_moss" -> ForestLightColorScheme
                    "cosmic_nebula" -> CosmicLightColorScheme
                    "mint_breeze" -> MintLightColorScheme
                    else -> CyberLightColorScheme
                }
            }
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
