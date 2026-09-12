package heizige.kk.kedge.overlays

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class KedgeMenuItem(
    val label: String,
    val icon: ImageVector? = null,
    val enabled: Boolean = true,
    val destructive: Boolean = false,
    val onClick: () -> Unit,
)

@Composable
fun KedgeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<KedgeMenuItem>,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        offset = offset,
    ) {
        items.forEach { item ->
            KedgeDropdownItem(item = item, onDismissRequest = onDismissRequest)
        }
    }
}

@Composable
fun KedgeDropdownItem(
    item: KedgeMenuItem,
    onDismissRequest: (() -> Unit)? = null,
) {
    val contentColor = kedgeMenuContentColor(item.destructive)
    DropdownMenuItem(
        text = { Text(item.label, color = contentColor) },
        leadingIcon = item.icon?.let {
            { Icon(imageVector = it, contentDescription = null, tint = contentColor) }
        },
        enabled = item.enabled,
        onClick = {
            item.onClick()
            onDismissRequest?.invoke()
        },
    )
}

@Composable
fun KedgeListPopup(
    show: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    items: List<KedgeMenuItem>,
) {
    KedgeDialog(
        show = show,
        onDismissRequest = onDismissRequest,
        title = title,
    ) {
        items.forEach { item ->
            val contentColor = kedgeMenuContentColor(item.destructive)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = item.enabled) {
                        item.onClick()
                        onDismissRequest()
                    }
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (item.icon != null) {
                    Icon(item.icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(22.dp))
                }
                Text(item.label, color = contentColor, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun kedgeMenuContentColor(destructive: Boolean): Color {
    if (destructive) {
        return when (LocalKedgeStyle.current) {
            KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.error
            KedgeStyle.Miuix -> MiuixTheme.colorScheme.error
        }
    }
    return when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.onSurface
        KedgeStyle.Miuix -> MiuixTheme.colorScheme.onSurface
    }
}
