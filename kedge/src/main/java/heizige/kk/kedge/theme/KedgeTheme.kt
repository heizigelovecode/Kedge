package heizige.kk.kedge.theme

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

/**
 * 静态 Light/Dark 下 Miuix 自带值不能直接用的几个槽位，一并挂进 [ThemeController]。
 *
 * ## error
 * - 浅色 `errorContainer = #FDF6F4` 比页面底色（`surface = #F7F7F7`）还白 →
 *   错误卡片看着像一块没上色的白板；
 * - 深色 `errorContainer = #2E0603` 比页面底色（`surface = #000000`）还黑 →
 *   容器直接消失在背景里。
 *
 * KernelSU（`ui/component/miuix/WarningCard.kt`）的结论一样：只有动态取色（Monet）
 * 才回落到生成的 `errorContainer`，静态主题一律写死成「淡红容器 + 明确的红字」。
 *
 * ## 暗色的 surface 台阶
 * Miuix 暗色把 `background` / `surfaceVariant` / `surfaceContainer` /
 * `surfaceContainerHigh` 全压成同一个 `#242424`。而「卡片（`surfaceContainer`）
 * 里放一个输入框（`surfaceContainerHigh`）」是最常见的组合 —— 两者同色，输入框
 * 整块糊在卡片上完全看不见。按 Material 3 暗色的做法把台阶拉开。
 *
 * 挂进 `lightColors`/`darkColors` 之后，`MiuixTheme.colorScheme.*` 以及
 * `MiuixMaterialThemeBridge` 桥出去的 `MaterialTheme.colorScheme.*` 一次对齐。
 *
 * 注意：Miuix 的 Monet 分支（`colorsFromSeed`）不读这两个值，动态取色仍走生成色，
 * 与 KernelSU 的 `isDynamicColor` 分支行为一致。
 */
private val MiuixStaticLightColors = top.yukonga.miuix.kmp.theme.lightColorScheme(
    error = Color(0xFFF72727),
    errorContainer = Color(0xFFF8E2E2),
    onErrorContainer = Color(0xFFF72727),
)

private val MiuixStaticDarkColors = top.yukonga.miuix.kmp.theme.darkColorScheme(
    error = Color(0xFFF72727),
    errorContainer = Color(0xFF310808),
    onErrorContainer = Color(0xFFF72727),
    // #242424 → #303030 → #3C3C3C，每档差 12，与 M3 暗色的台阶密度一致。
    surfaceContainerHigh = Color(0xFF303030),
    surfaceContainerHighest = Color(0xFF3C3C3C),
)

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
            // 静态 Light/Dark 的 error 槽位换成 KSU 那组，见文件顶部注释。
            lightColors = MiuixStaticLightColors,
            darkColors = MiuixStaticDarkColors,
            keyColor = seedColor,
            colorSpec = ThemeColorSpec.Spec2025,
            // 调色板照搬 KernelSU（ui/theme/MiuixTheme.kt）：默认 TonalSpot。
            // 之前这里写的是 Expressive —— 它的浅色模式 surfaceContainer 几乎贴着
            // surface，于是卡片看着像没画底色（用户报「设置项背景太浅、颜色发灰」）。
            paletteStyle = ThemePaletteStyle.TonalSpot,
            isDark = darkTheme,
        )
    }

    MiuixTheme(controller = controller) {
        // 关键：Miuix 与 MaterialTheme 是两套独立主题。业务代码大量直接读
        // MaterialTheme.colorScheme.*，不桥接就会退回 MD3 默认配色，
        // 导致 Miuix 界面里到处漏出 MD3 颜色。
        MiuixMaterialThemeBridge(darkTheme = darkTheme) { content() }
    }
}

val currentKedgeStyle: KedgeStyle
    @Composable
    @ReadOnlyComposable
    get() = LocalKedgeStyle.current
