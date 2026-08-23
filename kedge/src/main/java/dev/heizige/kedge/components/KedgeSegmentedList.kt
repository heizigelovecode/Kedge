package dev.heizige.kedge.components

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
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
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

@Composable
fun KedgeSegmentedList(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: KedgeSegmentedListScope.() -> Unit,
) {
    val scope = KedgeSegmentedListScope().apply(content)
    val visibleItems = scope.items.filter { it.visible }
    if (visibleItems.isEmpty()) return

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
    val radius = 18.dp
    val inner = 5.dp
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
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.secondaryContainer
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
