package heizige.kk.kedge.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixOverscrollFactory

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
        // 关键：把 MD3 的字阶槽位桥接到 Miuix 的 textStyles。不桥接的话，
        // 所有没显式写 style 的 Text(...) 都会拿到 MD3 默认字阶（Roboto + MD3 尺寸），
        // 与卡片/圆角/配色不搭——这类调用点有上千处，逐个改不现实。
        typography = miuixTypography(),
    ) {
        // Miuix 下必须显式给回弹效果：不提供时 LocalOverscrollFactory 没有
        // provider，LazyColumn 的 overscroll 变成"没有效果"，列表滚到头就是
        // 硬停，手感很别扭。这里用 Miuix 自带的 MiuixOverscrollFactory。
        CompositionLocalProvider(
            LocalOverscrollFactory provides MiuixOverscrollFactory,
            content = content,
        )
    }
}

/** 供 KedgeColors 等在 Miuix 下取值时复用，避免重复构造。 */
internal val MiuixBridgeSurfaceContainer: Color
    @Composable @ReadOnlyComposable get() = MiuixTheme.colorScheme.surfaceContainer


/**
 * 把 Miuix 的 [TextStyles] 映射成 MD3 的 [Typography]。
 *
 * 业务代码里大量 `Text(...)` 不写 `style`，默认走 `bodyLarge`；而 MD3 的其它槽位
 * （title / label / body 系列）也常被显式引用。把它们全部接到 Miuix 的字阶上，
 * Miuix 下就
 * 不会再漏出 MD3 的 Roboto/字号。MD3Exp 分支不走这里，保持原生 Typography。
 */
@Composable
private fun miuixTypography(): androidx.compose.material3.Typography {
    val t = MiuixTheme.textStyles
    return androidx.compose.material3.Typography(
        displayLarge = t.headline1,
        displayMedium = t.headline1,
        displaySmall = t.headline2,
        headlineLarge = t.headline1,
        headlineMedium = t.headline2,
        headlineSmall = t.subtitle,
        titleLarge = t.title1,
        titleMedium = t.title2,
        titleSmall = t.title3,
        bodyLarge = t.body1,
        bodyMedium = t.main,
        bodySmall = t.body2,
        labelLarge = t.button,
        labelMedium = t.footnote1,
        labelSmall = t.footnote2,
    )
}
