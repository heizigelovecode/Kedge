package heizige.kk.kedge.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 把 Miuix 的调色板桥接成 Material3 的 [ColorScheme]。
 *
 * 背景：Miuix 体系里 `MiuixTheme` 与 `MaterialTheme` 是两套独立主题，业务代码里
 * 大量直接读 `MaterialTheme.colorScheme.*`。Kedge 的 Miuix 分支原本只包了
 * `MiuixTheme`，这些读数就退回 MD3 默认配色（紫/蓝那套），于是 Miuix 界面里
 * 到处漏出 MD3 颜色。这里按角色一一对应，让两种写法取到同一套颜色。
 *
 * 命名约定：Miuix 没有 MD3 的 primary/secondary/tertiary 三分法，
 * 这三个槽统一映射到 Miuix 的 primary / primaryVariant / tertiary，
 * 容器槽用 Miuix 的 `*Container` 角色。
 */
@Composable
@ReadOnlyComposable
private fun miuixToMaterialScheme(dark: Boolean): ColorScheme {
    val c = MiuixTheme.colorScheme
    return if (dark) {
        darkColorScheme(
            primary = c.primary,
            onPrimary = c.onPrimary,
            primaryContainer = c.primaryVariant,
            onPrimaryContainer = c.onPrimaryVariant,
            secondary = c.primaryVariant,
            onSecondary = c.onPrimaryVariant,
            secondaryContainer = c.primaryVariant,
            onSecondaryContainer = c.onPrimaryVariant,
            // Miuix 没有独立的 tertiary 槽，退回 primary。
            tertiary = c.primary,
            onTertiary = c.onPrimary,
            tertiaryContainer = c.tertiaryContainer,
            onTertiaryContainer = c.onTertiaryContainer,
            background = c.background,
            onBackground = c.onBackground,
            surface = c.surface,
            onSurface = c.onSurface,
            surfaceVariant = c.surfaceVariant,
            onSurfaceVariant = c.onSurfaceVariantSummary,
            surfaceContainerLowest = c.surface,
            surfaceContainerLow = c.surfaceContainer,
            surfaceContainer = c.surfaceContainer,
            surfaceContainerHigh = c.surfaceContainerHigh,
            surfaceContainerHighest = c.surfaceContainerHighest,
            surfaceBright = c.surfaceContainerHighest,
            surfaceDim = c.surfaceVariant,
            outline = c.outline,
            outlineVariant = c.dividerLine,
            inverseSurface = c.onSurface,
            inverseOnSurface = c.surface,
            scrim = c.windowDimming,
            error = c.error,
            onError = c.onError,
            errorContainer = c.errorContainer,
            onErrorContainer = c.onErrorContainer,
        )
    } else {
        lightColorScheme(
            primary = c.primary,
            onPrimary = c.onPrimary,
            primaryContainer = c.primaryVariant,
            onPrimaryContainer = c.onPrimaryVariant,
            secondary = c.primaryVariant,
            onSecondary = c.onPrimaryVariant,
            secondaryContainer = c.primaryVariant,
            onSecondaryContainer = c.onPrimaryVariant,
            // Miuix 没有独立的 tertiary 槽，退回 primary。
            tertiary = c.primary,
            onTertiary = c.onPrimary,
            tertiaryContainer = c.tertiaryContainer,
            onTertiaryContainer = c.onTertiaryContainer,
            background = c.background,
            onBackground = c.onBackground,
            surface = c.surface,
            onSurface = c.onSurface,
            surfaceVariant = c.surfaceVariant,
            onSurfaceVariant = c.onSurfaceVariantSummary,
            surfaceContainerLowest = c.surface,
            surfaceContainerLow = c.surfaceContainer,
            surfaceContainer = c.surfaceContainer,
            surfaceContainerHigh = c.surfaceContainerHigh,
            surfaceContainerHighest = c.surfaceContainerHighest,
            surfaceBright = c.surfaceContainerHighest,
            surfaceDim = c.surfaceVariant,
            outline = c.outline,
            outlineVariant = c.dividerLine,
            inverseSurface = c.onSurface,
            inverseOnSurface = c.surface,
            scrim = c.windowDimming,
            error = c.error,
            onError = c.onError,
            errorContainer = c.errorContainer,
            onErrorContainer = c.onErrorContainer,
        )
    }
}

/**
 * 在 Miuix 主题内同步一份等价的 `MaterialTheme`。
 *
 * 只提供配色，不改字体/形状/elevation —— 那些仍由各组件自己按 style 分支决定，
 * 避免和已有的双风格实现打架。
 */
@Composable
internal fun MiuixMaterialThemeBridge(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val scheme = miuixToMaterialScheme(darkTheme)
    androidx.compose.material3.MaterialTheme(
        colorScheme = scheme,
        content = content,
    )
}

/** 供 KedgeColors 等在 Miuix 下取值时复用，避免重复构造。 */
internal val MiuixBridgeSurfaceContainer: Color
    @Composable @ReadOnlyComposable get() = MiuixTheme.colorScheme.surfaceContainer
