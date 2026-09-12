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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import heizige.kk.khromia.components.OptionItem as KhromiaOptionItem

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

        KedgeStyle.Miuix -> KedgeCard(
            modifier = modifier.fillMaxWidth(),
            onClick = onClick,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingContent != null) {
                    leadingContent()
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f),
                ) {
                    overlineContent?.invoke()
                    titleContent()
                    supportingContent?.invoke()
                }
                if (trailingContent != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    trailingContent()
                }
            }
        }
    }
}
