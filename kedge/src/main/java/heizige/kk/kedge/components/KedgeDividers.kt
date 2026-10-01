package heizige.kk.kedge.components

import androidx.compose.material3.HorizontalDivider as MdHorizontalDivider
import androidx.compose.material3.DividerDefaults as MdDividerDefaults
import androidx.compose.material3.VerticalDivider as MdVerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.DividerDefaults as MiuixDividerDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.HorizontalDivider as MiuixHorizontalDivider
import top.yukonga.miuix.kmp.basic.VerticalDivider as MiuixVerticalDivider

/**
 * 横向分隔线，**按风格分流**。
 *
 * Miuix 的分隔线用的是自己那套更淡的描边色；MD3 的 `HorizontalDivider` 在 Miuix
 * 主题下仍取 MD3 调色板，视觉上会明显偏重。`thickness` 为 `null` 时用各风格的默认粗细。
 */
@Composable
fun KedgeHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: androidx.compose.ui.unit.Dp? = null,
    color: Color? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> MiuixHorizontalDivider(
            modifier = modifier,
            thickness = thickness ?: MiuixDividerDefaults.Thickness,
            color = color ?: MiuixTheme.colorScheme.outline,
        )

        KedgeStyle.MD3Exp -> MdHorizontalDivider(
            modifier = modifier,
            thickness = thickness ?: MdDividerDefaults.Thickness,
            color = color ?: MdDividerDefaults.color,
        )
    }
}

/**
 * 纵向分隔线，**按风格分流**。参数含义同 [KedgeHorizontalDivider]。
 */
@Composable
fun KedgeVerticalDivider(
    modifier: Modifier = Modifier,
    thickness: androidx.compose.ui.unit.Dp? = null,
    color: Color? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> MiuixVerticalDivider(
            modifier = modifier,
            thickness = thickness ?: MiuixDividerDefaults.Thickness,
            color = color ?: MiuixTheme.colorScheme.outline,
        )

        KedgeStyle.MD3Exp -> MdVerticalDivider(
            modifier = modifier,
            thickness = thickness ?: MdDividerDefaults.Thickness,
            color = color ?: MdDividerDefaults.color,
        )
    }
}