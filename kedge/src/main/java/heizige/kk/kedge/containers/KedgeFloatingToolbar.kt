package heizige.kk.kedge.containers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.HorizontalFloatingToolbar as MdHorizontalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.FloatingToolbar as MiuixFloatingToolbar

/**
 * 悬浮工具条，**按风格分流**。
 *
 * MD3Exp 下用 MD3 的 `HorizontalFloatingToolbar`。Miuix 下用 Miuix 的
 * `FloatingToolbar`（圆角 + 外边距是 Miuix 自己那套）。
 *
 * Miuix 的 `FloatingToolbar` 没有 MD3 的展开/收起动画，所以 [expanded] 在 Miuix
 * 分支被忽略——这三处调用点都是常显式展开（`expanded = true`）或只需要一个浮层，
 * 语义不丢，只是少了收缩动画。
 *
 * @param contentAlignment Miuix 分支的内容对齐；MD3 分支由 MD3 自己处理。
 */
@Composable
fun KedgeFloatingToolbar(
    expanded: Boolean = true,
    modifier: Modifier = Modifier,
    contentAlignment: androidx.compose.ui.Alignment = androidx.compose.ui.Alignment.CenterStart,
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdHorizontalFloatingToolbar(
            expanded = expanded,
            modifier = modifier,
        ) {
            content()
        }

        KedgeStyle.Miuix -> MiuixFloatingToolbar(
            modifier = modifier,
            cornerRadius = 28.dp,
            outSidePadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            showDivider = false,
        ) {
            content()
        }
    }
}