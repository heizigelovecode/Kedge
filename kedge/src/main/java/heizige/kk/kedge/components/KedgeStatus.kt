package heizige.kk.kedge.components

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
import heizige.kk.kedge.theme.KedgeColors
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
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
    val colors = KedgeStatusDefaults.colors(level)
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
    val colors = KedgeStatusDefaults.colors(level)
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

/** Semantic status colors shared by status tags, warning cards, and host apps. */
data class KedgeStatusColors(
    val container: Color,
    val content: Color,
)

/**
 * Theme-aware status palette.
 *
 * Host apps use this when an icon or an inline surface needs to match Kedge's
 * status components without introducing a second, hard-coded color system.
 */
object KedgeStatusDefaults {
    @Composable
    fun colors(level: KedgeStatusLevel): KedgeStatusColors = kedgeStatusColors(level)
}

@Composable
private fun kedgeStatusColors(level: KedgeStatusLevel): KedgeStatusColors {
    val isMiuix = LocalKedgeStyle.current == KedgeStyle.Miuix
    // 成功/警告在 M3 和 Miuix 里都没有语义槽位，只能写死颜色；写死就得自己分明暗，
    // 否则暗色主题下会甩出一张奶油色卡片配深色文字（用户实际踩到过）。
    val dark = KedgeColors.isDark
    val success = if (dark) {
        KedgeStatusColors(Color(0xFF16301F), Color(0xFF7BD69B))
    } else {
        KedgeStatusColors(Color(0xFFE4F5E8), Color(0xFF188038))
    }
    val warning = if (dark) {
        KedgeStatusColors(Color(0xFF3A2B10), Color(0xFFFFC46B))
    } else {
        KedgeStatusColors(Color(0xFFFFF0DB), if (isMiuix) Color(0xFFF5A623) else Color(0xFFB06000))
    }
    if (isMiuix) {
        return when (level) {
            KedgeStatusLevel.Info -> KedgeStatusColors(MiuixTheme.colorScheme.secondaryContainer, MiuixTheme.colorScheme.onSecondaryContainer)
            KedgeStatusLevel.Success -> success
            KedgeStatusLevel.Notice -> KedgeStatusColors(MiuixTheme.colorScheme.tertiaryContainer, MiuixTheme.colorScheme.onTertiaryContainer)
            KedgeStatusLevel.Warning -> warning
            KedgeStatusLevel.Error -> KedgeStatusColors(MiuixTheme.colorScheme.errorContainer, MiuixTheme.colorScheme.onErrorContainer)
        }
    }
    return when (level) {
        KedgeStatusLevel.Info -> KedgeStatusColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        KedgeStatusLevel.Success -> success
        KedgeStatusLevel.Notice -> KedgeStatusColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        KedgeStatusLevel.Warning -> warning
        KedgeStatusLevel.Error -> KedgeStatusColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
    }
}
