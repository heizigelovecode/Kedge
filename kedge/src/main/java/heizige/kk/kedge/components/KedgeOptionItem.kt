package heizige.kk.kedge.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import heizige.kk.khromia.components.OptionItem as KhromiaOptionItem
import top.yukonga.miuix.kmp.theme.MiuixTheme
import heizige.kk.kedge.theme.MiuixTextStyleScope

/** 插槽版选项行：MD3Exp 走 Khromia OptionItem，Miuix 走 KedgeCard 行。 */
@Composable
fun KedgeOptionItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color.Unspecified,
    leadingContent: (@Composable () -> Unit)? = null,
    overlineContent: (@Composable () -> Unit)? = null,
    titleContent: @Composable () -> Unit,
    supportingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> KhromiaOptionItem(
            modifier = modifier,
            onClick = onClick,
            shape = shape,
            backgroundColor = if (backgroundColor == Color.Unspecified) {
                androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f)
            } else backgroundColor,
            leadingContent = leadingContent,
            overlineContent = overlineContent,
            titleContent = titleContent,
            supportingContent = supportingContent,
            trailingContent = trailingContent,
        )

        // 对齐 KernelSU / Miuix `BasicComponent`（Component.kt:60-200）的精确数值：
        // insideMargin 16dp、图标与文字之间 8dp、minHeight 56dp、标题/摘要之间不加间距。
        KedgeStyle.Miuix -> KedgeCard(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = KedgeComponentDefaults.MinHeight),
            // 内边距由 KedgeCard 的 contentPadding 统一承载；Row 里再加一次会变成
            // 16 + 16 = 32dp，看着比 KSU 松太多。
            contentPadding = KedgeComponentDefaults.InsideMargin,
            color = if (backgroundColor == Color.Unspecified) {
                MiuixTheme.colorScheme.surfaceContainer
            } else {
                backgroundColor
            },
            onClick = onClick,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingContent != null) {
                    leadingContent()
                    Spacer(modifier = Modifier.width(KedgeComponentDefaults.ActionSpacing))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    // 关键：调用点的 headline/supporting 里通常是**没写 style 的**
                    // `Text(...)`，它们取的是 MD3 的 `LocalTextStyle`（默认 bodyLarge）。
                    // Miuix 下不覆盖就会漏出 MD3 字号字重。这里换成 Miuix 的层级：
                    // 标题 headline1 + Medium，摘要 body2，与 BasicComponent 一致。
                    if (overlineContent != null) {
                        MiuixTextStyleScope(MiuixTheme.textStyles.body2) { overlineContent() }
                    }
                    MiuixTextStyleScope(
                        MiuixTheme.textStyles.headline1.copy(fontWeight = FontWeight.Medium)
                    ) { titleContent() }
                    if (supportingContent != null) {
                        MiuixTextStyleScope(MiuixTheme.textStyles.body2) { supportingContent() }
                    }
                }
                if (trailingContent != null) {
                    Spacer(modifier = Modifier.width(KedgeComponentDefaults.ActionSpacing))
                    trailingContent()
                }
            }
        }
    }
}

/** 设置项卡片的度量：内边距 / 图标与文字间距 / 最小高度。 */
internal object KedgeComponentDefaults {
    /** 卡片内边距，对齐库的 `BasicComponentDefaults.InsideMargin`。 */
    val InsideMargin = PaddingValues(16.dp)

    /** start/center/end 三栏之间的间隔。 */
    val ActionSpacing = 8.dp

    /** 最小高度，对齐库的 `BasicComponent` heightIn(min = 56.dp)。 */
    val MinHeight = 56.dp
}
