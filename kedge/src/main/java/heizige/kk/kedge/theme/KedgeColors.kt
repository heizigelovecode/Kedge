package heizige.kk.kedge.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 跟着 [LocalKedgeStyle] 走的配色入口。
 *
 * Miuix 模式下 [KedgeTheme] 只包了 `MiuixTheme`，压根没有 `MaterialTheme`，
 * 所以业务代码直接读 `MaterialTheme.colorScheme` 拿到的是 Compose 的兜底
 * **亮色**方案 —— 暗色主题下卡片会白得发光。页面里凡是要颜色的地方都走这里。
 */
object KedgeColors {
    val primary: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.primary }, { MiuixTheme.colorScheme.primary })

    val onPrimary: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.onPrimary }, { MiuixTheme.colorScheme.onPrimary })

    val primaryContainer: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.primaryContainer },
            { MiuixTheme.colorScheme.primaryContainer },
        )

    val onPrimaryContainer: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.onPrimaryContainer },
            { MiuixTheme.colorScheme.onPrimaryContainer },
        )

    val secondaryContainer: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.secondaryContainer },
            { MiuixTheme.colorScheme.secondaryContainer },
        )

    val onSecondaryContainer: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.onSecondaryContainer },
            { MiuixTheme.colorScheme.onSecondaryContainer },
        )

    val secondary: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.secondary }, { MiuixTheme.colorScheme.secondary })

    val onSecondary: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.onSecondary }, { MiuixTheme.colorScheme.onSecondary })

    val tertiary: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.tertiary },
            { MiuixTheme.colorScheme.tertiaryContainer },
        )

    val onTertiary: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.onTertiary },
            { MiuixTheme.colorScheme.onTertiaryContainer },
        )

    val tertiaryContainer: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.tertiaryContainer },
            { MiuixTheme.colorScheme.tertiaryContainer },
        )

    val onTertiaryContainer: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.onTertiaryContainer },
            { MiuixTheme.colorScheme.onTertiaryContainer },
        )

    val error: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.error }, { MiuixTheme.colorScheme.error })

    val onError: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.onError }, { MiuixTheme.colorScheme.onError })

    val errorContainer: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.errorContainer },
            { MiuixTheme.colorScheme.errorContainer },
        )

    val onErrorContainer: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.onErrorContainer },
            { MiuixTheme.colorScheme.onErrorContainer },
        )

    val background: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.background }, { MiuixTheme.colorScheme.background })

    val onBackground: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.onBackground }, { MiuixTheme.colorScheme.onBackground })

    val surface: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.surface }, { MiuixTheme.colorScheme.surface })

    val onSurface: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.onSurface }, { MiuixTheme.colorScheme.onSurface })

    val surfaceVariant: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.surfaceVariant },
            { MiuixTheme.colorScheme.surfaceVariant },
        )

    /** 次要文字。Miuix 那边叫 onSurfaceVariantSummary（列表项的副标题色）。 */
    val onSurfaceVariant: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.onSurfaceVariant },
            { MiuixTheme.colorScheme.onSurfaceVariantSummary },
        )

    val surfaceContainer: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.surfaceContainer },
            { MiuixTheme.colorScheme.surfaceContainer },
        )

    val surfaceContainerHigh: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.surfaceContainerHigh },
            { MiuixTheme.colorScheme.surfaceContainerHigh },
        )

    val surfaceContainerHighest: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.surfaceContainerHighest },
            { MiuixTheme.colorScheme.surfaceContainerHighest },
        )

    val outline: Color
        @Composable get() = pick({ MaterialTheme.colorScheme.outline }, { MiuixTheme.colorScheme.outline })

    /** 分割线。M3 的 outlineVariant ≈ Miuix 的 dividerLine。 */
    val outlineVariant: Color
        @Composable get() = pick(
            { MaterialTheme.colorScheme.outlineVariant },
            { MiuixTheme.colorScheme.dividerLine },
        )

    /**
     * 当前是不是暗色。按生效方案的背景亮度判断，比 `isSystemInDarkTheme()` 靠谱：
     * 主题可以被显式指定成跟系统相反。
     */
    val isDark: Boolean
        @Composable get() = background.luminance() < 0.5f
}

/** 只读生效那一套的颜色：另一套主题可能压根没被 provide。 */
@Composable
private inline fun pick(
    md3: @Composable () -> Color,
    miuix: @Composable () -> Color,
): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> md3()
    KedgeStyle.Miuix -> miuix()
}
