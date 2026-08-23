package dev.heizige.kedge.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface as MdSurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
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
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val cardContent: @Composable ColumnScope.() -> Unit = {
                Column(modifier = Modifier.padding(contentPadding), content = content)
            }
            if (onClick != null) {
                MdCard(onClick = onClick, modifier = modifier, content = cardContent)
            } else {
                MdCard(modifier = modifier, content = cardContent)
            }
        }

        KedgeStyle.Miuix -> MiuixCard(
            modifier = modifier,
            insideMargin = contentPadding,
            onClick = onClick,
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
