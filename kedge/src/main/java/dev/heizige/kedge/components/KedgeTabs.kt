package dev.heizige.kedge.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class KedgeTabItem<T>(
    val key: T,
    val label: String,
    val icon: ImageVector? = null,
    val enabled: Boolean = true,
)

@Composable
fun <T> KedgeTabRow(
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    items: List<KedgeTabItem<T>>,
    modifier: Modifier = Modifier,
) {
    KedgeSurface(
        modifier = modifier,
        color = kedgeSegmentContainerColor(),
        shape = CircleShape,
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                KedgeSegmentedButton(
                    selected = item.key == selectedItem,
                    onClick = { onItemSelected(item.key) },
                    enabled = item.enabled,
                    leadingIcon = item.icon,
                ) {
                    Text(item.label)
                }
            }
        }
    }
}

@Composable
fun KedgeToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    content: @Composable RowScope.() -> Unit,
) {
    KedgeSegmentedButton(
        selected = checked,
        onClick = { onCheckedChange(!checked) },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        content = content,
    )
}

@Composable
fun KedgeSegmentedButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val container by animateColorAsState(
        targetValue = if (selected) kedgeSegmentSelectedColor() else Color.Transparent,
        label = "KedgeSegmentedButtonContainer",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) kedgeSegmentSelectedContentColor() else kedgeSegmentContentColor(),
        label = "KedgeSegmentedButtonContent",
    )

    KedgeSurface(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        color = container,
        contentColor = contentColor,
        shape = CircleShape,
        border = if (selected) null else BorderStroke(1.dp, kedgeSegmentOutlineColor()),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor)
            }
            content()
        }
    }
}

@Composable
private fun kedgeSegmentContainerColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.secondaryContainer
}

@Composable
private fun kedgeSegmentSelectedColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.primaryContainer
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.primary.copy(alpha = 0.18f)
}

@Composable
private fun kedgeSegmentContentColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.onSurfaceVariant
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.onSecondaryContainer
}

@Composable
private fun kedgeSegmentSelectedContentColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.onPrimaryContainer
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.primary
}

@Composable
private fun kedgeSegmentOutlineColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.outline.copy(alpha = 0.25f)
}
