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
 * @param leadingContent 前置插槽（MD3 收到 `HorizontalFloatingToolbar` 的
 *   `RowScope` 版本，Miuix 分支排在内容前面）。
 */
@Composable
fun KedgeFloatingToolbar(
    expanded: Boolean = true,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdHorizontalFloatingToolbar(
            expanded = expanded,
            modifier = modifier,
            // MD3 的签名是 `RowScope.() -> Unit`，Kedge 收窄成无接收者版本，
            // 这里包一层适配
            leadingContent = leadingContent?.let { slot -> { slot() } },
        ) {
            content()
        }

        KedgeStyle.Miuix -> MiuixFloatingToolbar(
            modifier = modifier,
            cornerRadius = 28.dp,
            outSidePadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            showDivider = false,
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            ) {
                leadingContent?.invoke()
                content()
            }
        }
    }
}