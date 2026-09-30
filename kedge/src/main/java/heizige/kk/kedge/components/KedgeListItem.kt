package heizige.kk.kedge.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.material3.ListItem as MdListItem

/**
 * 插槽版列表行，签名对齐 MD3 `ListItem`。
 *
 * 为什么不用 [KedgeOptionItem]：它要求 `onClick` 且标题槽叫 `titleContent`，而 MD3 的
 * `ListItem` 是「不可点 + `headlineContent`」的组合，调用点形态对不上。这里补一个
 * 参数结构一致的，才能让 MD3 调用点整体换成 Kedge 组件。
 *
 * - MD3Exp：原样透传给 MD3 `ListItem`，行为完全不变；
 * - Miuix：渲染成 Miuix 观感的独立圆角卡片行（`surfaceContainer` + 20dp 圆角），
 *   与设置页里其它 Miuix 列表项保持一致。
 *
 * @param colors MD3 配色，**只在 MD3Exp 分支生效**（Miuix 用自己的容器色）。
 */
@Composable
fun KedgeListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: (@Composable () -> Unit)? = null,
    supportingContent: (@Composable () -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    colors: ListItemColors? = null,
    tonalElevation: Dp? = null,
    shape: Shape? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdListItem(
            headlineContent = headlineContent,
            modifier = modifier,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            colors = colors ?: ListItemDefaults.colors(),
            tonalElevation = tonalElevation ?: ListItemDefaults.Elevation,
        )

        KedgeStyle.Miuix -> KedgeSurface(
            modifier = modifier.fillMaxWidth(),
            color = MiuixTheme.colorScheme.surfaceContainer,
            shape = shape ?: RoundedCornerShape(20.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingContent != null) {
                    leadingContent()
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    if (overlineContent != null) {
                        overlineContent()
                    }
                    headlineContent()
                    if (supportingContent != null) {
                        supportingContent()
                    }
                }
                if (trailingContent != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    trailingContent()
                }
            }
        }
    }
}
