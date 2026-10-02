package heizige.kk.kedge.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme

@DslMarker
annotation class KedgeSegmentedListDsl

@KedgeSegmentedListDsl
class KedgeSegmentedListScope internal constructor() {
    internal val items = mutableListOf<SegmentedEntry>()

    fun item(
        key: Any? = null,
        visible: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        items += SegmentedEntry(key = key ?: items.size, visible = visible, content = content)
    }
}

@Immutable
internal data class SegmentedEntry(
    val key: Any,
    val visible: Boolean,
    val content: @Composable () -> Unit,
)

@KedgeSegmentedListDsl
class KedgeSegmentedListSlotsScope internal constructor() {
    internal val items = mutableListOf<SegmentedSlotEntry>()

    /**
     * 追加一项。[content] 收到的是该项在**可见项**里的下标与可见项总数，
     * 调用点据此自己算圆角（首项只圆上边 / 中间直角 / 末项只圆下边）。
     */
    fun item(
        key: Any? = null,
        visible: Boolean = true,
        content: @Composable (index: Int, count: Int) -> Unit,
    ) {
        items += SegmentedSlotEntry(key = key ?: items.size, visible = visible, content = content)
    }
}

@Immutable
internal data class SegmentedSlotEntry(
    val key: Any,
    val visible: Boolean,
    val content: @Composable (index: Int, count: Int) -> Unit,
)

/**
 * 插槽版分组列表：容器只负责「分组形态」——项间距，以及把 index/count 交给每一项；
 * 行的渲染与圆角全部由调用点决定。
 *
 * 与 [KedgeSegmentedList] 的分工：后者自带 [KedgeSurface] 背景与固定 16dp 圆角，
 * 只适合 [KedgeSegmentedListItem] 那种标题/摘要都是 String 的固定行。宿主在下面
 * 两种情况下应该用本组件：
 *
 * - 圆角策略要由宿主决定（例如可由用户在设置里改的圆角与间距）；
 * - 行里要塞输入框、错误色、自定义字阶这类 String 参数表达不了的内容。
 *
 * 容器**刻意不叠背景**：行本身多半已经自带底色与圆角（[KedgeOptionItem] 就是），
 * 再套一层 [KedgeSurface] 会变成两层底色、两层圆角。
 *
 * [title] 是插槽而非 String，且**没有项时也会渲染**——空分组仍要显示标题。
 * 它被放在项列**外面**、不参与 [itemGap]：调用点通常自己在 title 槽里带上下
 * padding，再叠一层项间距会多出一道缝。与 [KedgeSegmentedList]（title 在项列内、
 * 参与间距）刻意不同，迁移时别照抄那套间距。
 */
@Composable
fun KedgeSegmentedListSlots(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    itemGap: Dp,
    content: KedgeSegmentedListSlotsScope.() -> Unit,
) {
    val scope = KedgeSegmentedListSlotsScope().apply(content)
    val visibleItems = scope.items.filter { it.visible }
    Column(modifier = modifier) {
        title?.invoke()
        Column(verticalArrangement = Arrangement.spacedBy(itemGap)) {
            visibleItems.forEachIndexed { index, entry ->
                entry.content(index, visibleItems.size)
            }
        }
    }
}

@Composable
fun KedgeSegmentedList(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: KedgeSegmentedListScope.() -> Unit,
) {
    val scope = KedgeSegmentedListScope().apply(content)
    val visibleItems = scope.items.filter { it.visible }
    if (visibleItems.isEmpty()) return

    // Miuix keeps items glued together (0 gap, only outer corners rounded);
    // MD3Exp uses a subtle 2dp separation with soft inner corners.
    val itemGap = when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> 2.dp
        KedgeStyle.Miuix -> 0.dp
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(itemGap)) {
        if (title != null) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 16.dp, bottom = 6.dp),
                color = kedgeSegmentedTitleColor(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
        visibleItems.forEachIndexed { index, entry ->
            KedgeSegmentedItemContainer(index = index, count = visibleItems.size) {
                entry.content()
            }
        }
    }
}

@Composable
fun KedgeSegmentedListItem(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = kedgeSegmentedIconColor(),
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(14.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, color = kedgeSegmentedPrimaryTextColor(), style = MaterialTheme.typography.bodyLarge)
            if (summary != null) {
                Text(text = summary, color = kedgeSegmentedSecondaryTextColor(), style = MaterialTheme.typography.bodySmall)
            }
        }
        trailingContent?.invoke()
    }
}

@Composable
fun KedgeSegmentedSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    KedgeSegmentedListItem(
        title = title,
        summary = summary,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        onClick = { onCheckedChange(!checked) },
        trailingContent = { KedgeSwitch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled) },
    )
}

@Composable
fun KedgeSegmentedCheckboxItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    KedgeSegmentedListItem(
        title = title,
        summary = summary,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        onClick = { onCheckedChange(!checked) },
        trailingContent = { KedgeCheckbox(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled) },
    )
}

@Composable
fun KedgeSegmentedRadioItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    KedgeSegmentedListItem(
        title = title,
        summary = summary,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        trailingContent = { KedgeRadioButton(selected = selected, onClick = onClick, enabled = enabled) },
    )
}

@Composable
fun KedgeSegmentedTextField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = kedgeSegmentedIconColor(), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(14.dp))
            }
            Text(text = title, color = kedgeSegmentedPrimaryTextColor(), style = MaterialTheme.typography.bodyLarge)
        }
        KedgeTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            placeholder = placeholder,
            enabled = enabled,
            singleLine = true,
        )
    }
}

@Composable
private fun KedgeSegmentedItemContainer(
    index: Int,
    count: Int,
    content: @Composable () -> Unit,
) {
    val radius = 16.dp
    // Miuix keeps middle items perfectly square (only the group's first/last
    // item carries the outer radius); MD3Exp softens inner seams with 5dp.
    val inner = if (LocalKedgeStyle.current == KedgeStyle.Miuix) 0.dp else 5.dp
    val shape = RoundedCornerShape(
        topStart = if (index == 0) radius else inner,
        topEnd = if (index == 0) radius else inner,
        bottomStart = if (index == count - 1) radius else inner,
        bottomEnd = if (index == count - 1) radius else inner,
    )
    KedgeSurface(
        modifier = Modifier.fillMaxWidth(),
        color = kedgeSegmentedContainerColor(),
        shape = shape,
        content = content,
    )
}

@Composable
private fun kedgeSegmentedContainerColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.surfaceContainer
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.surfaceContainerHigh
}

@Composable
private fun kedgeSegmentedTitleColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.primary
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.primary
}

@Composable
private fun kedgeSegmentedIconColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.primary
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.primary
}

@Composable
private fun kedgeSegmentedPrimaryTextColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.onSurface
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.onSecondaryContainer
}

@Composable
private fun kedgeSegmentedSecondaryTextColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.onSurfaceVariant
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.onSurfaceVariantSummary
}
