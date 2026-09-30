package heizige.kk.kedge.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card as MdCard
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults as MdCardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface as MdSurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CardDefaults as MiuixCardDefaults
import top.yukonga.miuix.kmp.basic.Surface as MiuixSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun KedgeSurface(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    shape: Shape? = null,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdSurface(
            modifier = modifier,
            color = if (color == Color.Unspecified) MaterialTheme.colorScheme.surface else color,
            contentColor = if (contentColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else contentColor,
            shape = shape ?: MaterialTheme.shapes.medium,
            shadowElevation = shadowElevation,
            border = border,
            content = content,
        )

        KedgeStyle.Miuix -> MiuixSurface(
            modifier = modifier,
            color = if (color == Color.Unspecified) MiuixTheme.colorScheme.surface else color,
            contentColor = if (contentColor == Color.Unspecified) MiuixTheme.colorScheme.onSurface else contentColor,
            shape = shape ?: RoundedCornerShape(16.dp),
            shadowElevation = shadowElevation,
            border = border,
            content = content,
        )
    }
}

@Composable
fun KedgeCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    color: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    shape: Shape? = null,
    onClick: (() -> Unit)? = null,
    /**
     * MD3 配色，**只在 MD3Exp 分支生效**。收下它是为了让 MD3 调用点
     * （`Card(colors = CardDefaults.cardColors(...))`）能整体换成 Kedge 组件而不丢
     * 自定义配色；Miuix 分支用自己那套 `surface` / `surfaceContainer`。
     */
    colors: CardColors? = null,
    /** MD3 边框，只在 MD3Exp 分支生效（见 [colors]）。 */
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val cardColors = when {
                colors != null -> colors
                color == Color.Unspecified && contentColor == Color.Unspecified -> MdCardDefaults.cardColors()
                contentColor == Color.Unspecified -> MdCardDefaults.cardColors(containerColor = color)
                else -> MdCardDefaults.cardColors(containerColor = color, contentColor = contentColor)
            }
            val cardContent: @Composable ColumnScope.() -> Unit = {
                Column(modifier = Modifier.padding(contentPadding), content = content)
            }
            if (onClick != null) {
                MdCard(
                    onClick = onClick,
                    modifier = modifier,
                    colors = cardColors,
                    shape = shape ?: MdCardDefaults.shape,
                    border = border,
                    content = cardContent,
                )
            } else {
                MdCard(
                    modifier = modifier,
                    colors = cardColors,
                    shape = shape ?: MdCardDefaults.shape,
                    border = border,
                    content = cardContent,
                )
            }
        }

        KedgeStyle.Miuix -> MiuixCard(
            // 用 Modifier.clip(shape) 而不是 Miuix 的 cornerRadius 参数：
            // Miuix Card 只支持单一 cornerRadius，承载不了 CardGroup 的分组圆角。
            modifier = modifier.then(if (shape != null) Modifier.clip(shape) else Modifier),
            // Miuix Card 只接受单一 cornerRadius（Dp），不支持任意 Shape。
            // 调用方（CardGroup 用 indexedShape 做分组圆角）传 Shape 时取其最大圆角近似。
            insideMargin = contentPadding,
            onClick = onClick,
            // 调用方没指定颜色时**不要**覆盖 Miuix Card 的默认色：它的默认是
            // surfaceContainer / onSurfaceContainer（KernelSU 的卡片就是这个色）。
            // 之前这里强制成 surface，和页面背景同色 -> 卡片在 Miuix 下完全隐形，
            // 表现为「设置项没有背景颜色和圆角」。
            colors = MiuixCardDefaults.defaultColors(
                color = if (color == Color.Unspecified) {
                    MiuixTheme.colorScheme.surfaceContainer
                } else {
                    color
                },
            ),
            content = content,
        )
    }
}

@Composable
fun KedgeColumnScope(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

@Composable
fun KedgeRowScope(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(12.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content,
    )
}

/**
 * 把任意 [Shape] 近似成 Miuix Card 的单一 [Dp] 圆角。
 *
 * Miuix 的 `Card` 只接受 `cornerRadius: Dp`，没有任意 Shape 的重载。
 * 这里取圆角矩形的最大角半径；非圆角矩形（如 CutCornerShape）也按其
 * 固定圆角量取近似值，保证 CardGroup 的分组圆角观感不丢。
 */

