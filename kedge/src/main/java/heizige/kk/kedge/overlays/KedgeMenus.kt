package heizige.kk.kedge.overlays

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.overlay.OverlayListPopup
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

/**
 * 插槽版下拉菜单，签名对齐 MD3 `DropdownMenu`。
 *
 * 原来的 [KedgeDropdownMenu] 收 `items: List<KedgeMenuItem>`（自己把菜单项摊平），
 * 跟 MD3 `DropdownMenu(expanded, onDismissRequest) { DropdownMenuItem(...) }` 的
 * 形态对不上，所以业务里 17 处 `DropdownMenu` 一直直接用 MD3 的——而 MD3
 * `DropdownMenu` 在 Miuix 下完全没有 Miuix 分支，等于这些菜单全是 MD3 观感。
 *
 * - MD3Exp：原样透传 MD3 `DropdownMenu`；
 * - Miuix：`OverlayListPopup` + `ListPopupColumn`，对齐 KernelSU
 *   `RebootListPopupMiuix.kt` 的写法。
 *
 * @param offset 仅 MD3Exp 分支生效；Miuix 的弹层定位由 `popupPositionProvider` 决定。
 */
@Composable
fun KedgeDropdownMenuSlots(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    // ColumnScope 与 MD3 DropdownMenu / Miuix ListPopupColumn 一致，两边都能直接用
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            offset = offset,
        ) {
            content()
        }

        KedgeStyle.Miuix -> OverlayListPopup(
            show = expanded,
            popupModifier = modifier,
            // MD3 DropdownMenu 不给背景加遮罩，这里也不加，否则整屏会暗一档。
            enableWindowDim = false,
            onDismissRequest = onDismissRequest,
        ) {
            ListPopupColumn { content() }
        }
    }
}

/**
 * 插槽版菜单项，签名对齐 MD3 `DropdownMenuItem`。
 *
 * Miuix 0.9.3 没有 ListPopupItem 这种现成组件（KernelSU 也是自己拼一行），
 * 所以这里用 clickable 的 Row 拼：图标 + 文字，删除态用 error 色。
 */
@Composable
fun KedgeDropdownItemSlot(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> DropdownMenuItem(
            text = text,
            onClick = onClick,
            modifier = modifier,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            enabled = enabled,
        )

        KedgeStyle.Miuix -> Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                leadingIcon()
            }
            Box(modifier = Modifier.weight(1f)) {
                text()
            }
            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}

