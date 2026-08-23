package dev.heizige.kedge.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeColorSpec
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.ThemePaletteStyle

val LocalKedgeStyle = staticCompositionLocalOf { KedgeStyle.MD3Exp }

@Composable
fun KedgeTheme(
    style: KedgeStyle = KedgeStyle.MD3Exp,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = style == KedgeStyle.MD3Exp,
    md3ColorScheme: ColorScheme? = null,
    miuixSeedColor: Color? = null,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalKedgeStyle provides style) {
        when (style) {
            KedgeStyle.MD3Exp -> KedgeMaterialTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor,
                colorScheme = md3ColorScheme,
                content = content,
            )

            KedgeStyle.Miuix -> KedgeMiuixTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor,
                seedColor = miuixSeedColor,
                content = content,
            )
        }
    }
}

@Composable
private fun KedgeMaterialTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    colorScheme: ColorScheme?,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme ?: rememberKedgeColorScheme(darkTheme, dynamicColor),
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}

@Composable
private fun rememberKedgeColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
): ColorScheme {
    val context = LocalContext.current
    return when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme ->
            dynamicDarkColorScheme(context)

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            dynamicLightColorScheme(context)

        darkTheme -> remember { darkColorScheme() }
        else -> remember { lightColorScheme() }
    }
}

@Composable
private fun KedgeMiuixTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    seedColor: Color?,
    content: @Composable () -> Unit,
) {
    val colorSchemeMode = remember(darkTheme, dynamicColor) {
        when {
            dynamicColor && darkTheme -> ColorSchemeMode.MonetDark
            dynamicColor -> ColorSchemeMode.MonetLight
            darkTheme -> ColorSchemeMode.Dark
            else -> ColorSchemeMode.Light
        }
    }
    val controller = remember(colorSchemeMode, darkTheme, seedColor) {
        ThemeController(
            colorSchemeMode = colorSchemeMode,
            keyColor = seedColor,
            colorSpec = ThemeColorSpec.Spec2025,
            paletteStyle = ThemePaletteStyle.Expressive,
            isDark = darkTheme,
        )
    }

    MiuixTheme(controller = controller, content = content)
}

val currentKedgeStyle: KedgeStyle
    @Composable
    @ReadOnlyComposable
    get() = LocalKedgeStyle.current
