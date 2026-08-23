package dev.heizige.kedge.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge as MdBadge
import androidx.compose.material3.BadgedBox as MdBadgedBox
import androidx.compose.material3.LocalContentColor as MdLocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.Badge as MiuixBadge
import top.yukonga.miuix.kmp.basic.BadgedBox as MiuixBadgedBox
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun KedgeBadge(
    modifier: Modifier = Modifier,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdBadge(modifier = modifier, content = content)
        KedgeStyle.Miuix -> MiuixBadge(modifier = modifier, content = content)
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
