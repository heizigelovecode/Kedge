package heizige.kk.kedge.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import heizige.kk.khromia.components.SegmentedItem
import heizige.kk.khromia.components.SingleChoiceSegmentedRow
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 单选分段控件，**按风格分流**。
 *
 * MD3Exp 下直接用 Khromia 的 [SingleChoiceSegmentedRow]（共享圆角底板 + 高亮块）。
 *
 * Miuix 没有对应的分段按钮组件，只能自己拼：这里用一行胶囊（Miuix 的
 * `Pill` 圆角），选中项填 `primaryContainer`、未选中填 `surfaceContainerLow`，
 * 交互与 OptionItem 同源。
 *
 * 选中状态由调用方持有（[SegmentedItem.selected]），本组件只负责呈现与回调。
 *
 * @param fillWidth `true`（默认）时各项等分整行宽度，`false` 时按文案宽度排布并左对齐。
 *   同一个表单里如果单选和多选混用，等分和按宽度两种宽度并排会显得很不齐 —— 把单选也
 *   改成 `false` 即可与 [KedgeMultiChoiceSegmentedRow] 的观感统一。
 *
 * 用途：设置页里的成组互斥选项（颜色模式、TTS/ASR、提供商类型等）。
 */
@Composable
fun KedgeSingleChoiceSegmentedRow(
    items: List<SegmentedItem>,
    modifier: Modifier = Modifier,
    fillWidth: Boolean = true,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> SingleChoiceSegmentedRow(
            items = items,
            modifier = modifier,
        )

        KedgeStyle.Miuix -> MiuixSingleChoiceSegmentedRow(
            items = items,
            modifier = modifier,
            fillWidth = fillWidth,
        )
    }
}

/**
 * 多选分段控件，**按风格分流**。形态同 [KedgeSingleChoiceSegmentedRow]，
 * 但每项可独立勾选/取消。
 */
@Composable
fun KedgeMultiChoiceSegmentedRow(
    items: List<SegmentedItem>,
    modifier: Modifier = Modifier,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> heizige.kk.khromia.components.MultiChoiceSegmentedRow(
            items = items,
            modifier = modifier,
        )

        KedgeStyle.Miuix -> MiuixMultiChoiceSegmentedRow(
            items = items,
            modifier = modifier,
        )
    }
}

@Composable
private fun MiuixSingleChoiceSegmentedRow(
    items: List<SegmentedItem>,
    modifier: Modifier = Modifier,
    fillWidth: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            MiuixSegmentedPill(
                item = item,
                onClick = item.onClick,
                modifier = if (fillWidth) {
                    Modifier.weight(1f)
                } else {
                    Modifier.weight(1f, fill = false)
                },
            )
        }
    }
}

@Composable
private fun MiuixMultiChoiceSegmentedRow(
    items: List<SegmentedItem>,
    modifier: Modifier = Modifier,
) {
    // 多选按内容宽度排布，不强行等分——勾选项长度差异通常很大。
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            MiuixSegmentedPill(
                item = item,
                onClick = item.onClick,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

/** Miuix 风格的单个分段胶囊。 */
@Composable
private fun MiuixSegmentedPill(
    item: SegmentedItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container by animateColorAsState(
        targetValue = if (item.selected) {
            MiuixTheme.colorScheme.primaryContainer
        } else {
            MiuixTheme.colorScheme.surfaceContainer
        },
        label = "KedgeSegmentedContainer",
    )
    val content by animateColorAsState(
        targetValue = if (item.selected) {
            MiuixTheme.colorScheme.onPrimaryContainer
        } else {
            MiuixTheme.colorScheme.onSurfaceVariantSummary
        },
        label = "KedgeSegmentedContent",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .selectable(
                selected = item.selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides content) {
            Text(
                text = item.label,
                color = content,
                maxLines = 1,
            )
        }
    }
}
