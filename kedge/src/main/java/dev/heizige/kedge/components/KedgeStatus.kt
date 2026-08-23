package dev.heizige.kedge.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme

enum class KedgeStatusLevel {
    Info,
    Success,
    Notice,
    Warning,
    Error,
}

@Composable
fun KedgeStatusTag(
    label: String,
    modifier: Modifier = Modifier,
    level: KedgeStatusLevel = KedgeStatusLevel.Info,
) {
    val colors = kedgeStatusColors(level)
    KedgeSurface(
        modifier = modifier,
        color = colors.container,
        contentColor = colors.content,
        shape = CircleShape,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = colors.content,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun KedgeWarningCard(
    message: String,
    modifier: Modifier = Modifier,
    level: KedgeStatusLevel = KedgeStatusLevel.Warning,
    onClick: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = kedgeStatusColors(level)
    KedgeSurface(
        modifier = modifier.clickable(enabled = onClick != null) { onClick?.invoke() },
        color = colors.container,
        contentColor = colors.content,
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = colors.content,
                style = MaterialTheme.typography.bodyMedium,
            )
            action?.invoke()
        }
    }
}

private data class KedgeStatusColors(
    val container: Color,
    val content: Color,
)

@Composable
private fun kedgeStatusColors(level: KedgeStatusLevel): KedgeStatusColors {
    val isMiuix = LocalKedgeStyle.current == KedgeStyle.Miuix
    if (isMiuix) {
        return when (level) {
            KedgeStatusLevel.Info -> KedgeStatusColors(MiuixTheme.colorScheme.secondaryContainer, MiuixTheme.colorScheme.onSecondaryContainer)
            KedgeStatusLevel.Success -> KedgeStatusColors(Color(0xFFE4F5E8), Color(0xFF188038))
            KedgeStatusLevel.Notice -> KedgeStatusColors(MiuixTheme.colorScheme.tertiaryContainer, MiuixTheme.colorScheme.onTertiaryContainer)
            KedgeStatusLevel.Warning -> KedgeStatusColors(Color(0xFFFFF0DB), Color(0xFFF5A623))
            KedgeStatusLevel.Error -> KedgeStatusColors(MiuixTheme.colorScheme.errorContainer, MiuixTheme.colorScheme.onErrorContainer)
        }
    }
    return when (level) {
        KedgeStatusLevel.Info -> KedgeStatusColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        KedgeStatusLevel.Success -> KedgeStatusColors(Color(0xFFE4F5E8), Color(0xFF188038))
        KedgeStatusLevel.Notice -> KedgeStatusColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        KedgeStatusLevel.Warning -> KedgeStatusColors(Color(0xFFFFF0DB), Color(0xFFB06000))
        KedgeStatusLevel.Error -> KedgeStatusColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
    }
}
