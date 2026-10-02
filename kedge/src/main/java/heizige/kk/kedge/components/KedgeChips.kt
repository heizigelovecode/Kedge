package heizige.kk.kedge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip as MdAssistChip
import androidx.compose.material3.AssistChipDefaults as MdAssistChipDefaults
import androidx.compose.material3.Text as MdText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 可点击的辅助标签（筛选/跳转用），**按风格分流**。
 *
 * MD3Exp 下用原生 [MdAssistChip]。Miuix 没有 Chip 组件，这里用胶囊形态：
 * `surfaceContainer` 底 + `onSurfaceVariantSummary` 字色，圆角 50（与
 * [KedgeSingleChoiceSegmentedRow] 的分段胶囊同源）。
 *
 * @param label 标签文案。收 String 而不是插槽，和 Miuix 的 Text 保持一致；
 *   调用点若需要自定义内容用 [content]。
 */
@Composable
fun KedgeAssistChip(
    onClick: () -> Unit,
    label: String? = null,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    /** MD3 配色，只在 MD3Exp 分支生效。 */
    containerColor: Color? = null,
    labelColor: Color? = null,
    content: (@Composable () -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdAssistChip(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            label = content ?: { MdText(label.orEmpty()) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            colors = MdAssistChipDefaults.assistChipColors(
                containerColor = containerColor ?: MdAssistChipDefaults.assistChipColors().containerColor,
                labelColor = labelColor ?: MdAssistChipDefaults.assistChipColors().labelColor,
            ),
        )

        KedgeStyle.Miuix -> Row(
            modifier = modifier
                .clip(RoundedCornerShape(50))
                .background(MiuixTheme.colorScheme.surfaceContainer)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()
            if (content != null) {
                content()
            } else {
                MiuixText(
                    text = label.orEmpty(),
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = 1,
                )
            }
            trailingIcon?.invoke()
        }
    }
}
