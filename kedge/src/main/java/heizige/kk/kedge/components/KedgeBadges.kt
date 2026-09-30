package heizige.kk.kedge.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge as MdBadge
import androidx.compose.material3.BadgeDefaults as MdBadgeDefaults
import androidx.compose.material3.BadgedBox as MdBadgedBox
import androidx.compose.material3.LocalContentColor as MdLocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.basic.Surface as MiuixSurface
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.Badge as MiuixBadge
import top.yukonga.miuix.kmp.basic.BadgeDefaults as MiuixBadgeDefaults
import top.yukonga.miuix.kmp.basic.BadgedBox as MiuixBadgedBox
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun KedgeBadge(
    modifier: Modifier = Modifier,
    /**
     * MD3 配色，只在 MD3Exp 分支生效；Miuix 用自己的 primary/surface 体系。
     * 收下这两个参数是为了让 MD3 调用点（`Badge(containerColor = …)`）能整体换成
     * Kedge 组件而不丢自定义配色。
     *
     * 注意：它们必须排在 `content` **前面**，因为 `KedgeBadge { Text("3") }` 这种
     * 尾随 lambda 写法绑定的是最后一个参数。
     */
    containerColor: Color? = null,
    contentColor: Color? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdBadge(
            modifier = modifier,
            containerColor = containerColor ?: MdBadgeDefaults.containerColor,
            contentColor = contentColor ?: MdLocalContentColor.current,
            content = content,
        )

        // 必须把颜色透传给 Miuix Badge：它的 `BadgeDefaults.containerColor` 默认是
        // `colorScheme.error`（红橙）。之前这里不传，Miuix 下所有 badge 一律变成
        // 红橙色——探索市场精选卡上的「优选 / 免费 / v1.1.0」就是这么变丑的。
        KedgeStyle.Miuix -> MiuixBadge(
            modifier = modifier,
            containerColor = containerColor ?: MiuixBadgeDefaults.containerColor,
            contentColor = contentColor ?: MiuixBadgeDefaults.contentColor,
            content = content,
        )
    }
}

@Composable
fun KedgeBadgedBox(
    badge: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdBadgedBox(badge = { badge() }, modifier = modifier) { content() }
        KedgeStyle.Miuix -> MiuixBadgedBox(badge = { badge() }, modifier = modifier) { content() }
    }
}

@Composable
fun KedgeTag(
    text: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> AssistChip(
            onClick = onClick ?: {},
            label = { Text(text) },
            modifier = modifier,
            leadingIcon = leadingIcon,
            enabled = onClick != null,
        )

        KedgeStyle.Miuix -> Surface(
            modifier = modifier,
            shape = CircleShape,
            color = MiuixTheme.colorScheme.secondaryVariant,
            contentColor = MiuixTheme.colorScheme.onSecondaryVariant,
            onClick = onClick ?: {},
            enabled = onClick != null,
        ) {
            CompositionLocalProvider(MdLocalContentColor provides MiuixTheme.colorScheme.onSecondaryVariant) {
                MiuixText(
                    text = text,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = MiuixTheme.colorScheme.onSecondaryVariant,
                )
            }
        }
    }
}

/**
 * 双风格筛选标签，签名对齐 MD3 `FilterChip`。
 *
 * Miuix 0.9.3 没有 chip 组件（KernelSU 也没用），所以按 Miuix 的观感自己拼：
 * 胶囊形状 + 内部 `surfaceContainer`，选中态用 `primaryContainer` 着色。
 *
 * @param shape 仅 MD3Exp 使用；Miuix 固定胶囊。
 * @param colors 仅 MD3Exp 使用。
 */
@Composable
fun KedgeFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    shape: Shape? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> androidx.compose.material3.FilterChip(
            selected = selected,
            onClick = onClick,
            label = label,
            modifier = modifier,
            enabled = enabled,
            leadingIcon = leadingIcon,
            shape = shape ?: androidx.compose.material3.FilterChipDefaults.shape,
        )

        KedgeStyle.Miuix -> MiuixSurface(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            shape = CircleShape,
            color = if (selected) {
                MiuixTheme.colorScheme.primaryContainer
            } else {
                MiuixTheme.colorScheme.surfaceContainer
            },
            contentColor = if (selected) {
                MiuixTheme.colorScheme.onPrimaryContainer
            } else {
                MiuixTheme.colorScheme.onSurfaceContainer
            },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (selected) {
                    MiuixText(
                        text = "✓",
                        color = MiuixTheme.colorScheme.onPrimaryContainer,
                        style = MiuixTheme.textStyles.body2,
                        modifier = Modifier.size(16.dp),
                    )
                }
                if (leadingIcon != null) {
                    leadingIcon()
                }
                CompositionLocalProvider(
                    androidx.compose.material3.LocalTextStyle provides
                        MiuixTheme.textStyles.body2,
                ) {
                    label()
                }
            }
        }
    }
}
