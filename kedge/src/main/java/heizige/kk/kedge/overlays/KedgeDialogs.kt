package heizige.kk.kedge.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import heizige.kk.kedge.components.KedgeButton
import heizige.kk.kedge.components.KedgeButtonVariant
import heizige.kk.kedge.components.KedgeCard
import heizige.kk.kedge.components.KedgeTextButton
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.window.WindowDialog as MiuixWindowDialog

@Composable
fun KedgeDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    summary: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!show) return

    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> Dialog(onDismissRequest = onDismissRequest) {
            KedgeCard(modifier = modifier) {
                if (title != null) Text(text = title)
                if (summary != null) Text(text = summary)
                content()
            }
        }

        KedgeStyle.Miuix -> MiuixWindowDialog(
            show = show,
            modifier = modifier,
            title = title,
            summary = summary,
            onDismissRequest = onDismissRequest,
        ) {
            Column { content() }
        }
    }
}

@Composable
fun KedgeAlertDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    text: String? = null,
    confirmText: String = "OK",
    onConfirm: () -> Unit,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null,
) {
    if (!show) return

    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(title) },
            text = text?.let { { Text(it) } },
            confirmButton = {
                TextButton(onClick = onConfirm) { Text(confirmText) }
            },
            dismissButton = dismissText?.let {
                {
                    TextButton(onClick = onDismiss ?: onDismissRequest) { Text(it) }
                }
            },
        )

        KedgeStyle.Miuix -> MiuixWindowDialog(
            show = show,
            title = title,
            summary = text,
            onDismissRequest = onDismissRequest,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (dismissText != null) {
                    KedgeTextButton(onClick = onDismiss ?: onDismissRequest) {
                        MiuixText(dismissText)
                    }
                }
                KedgeButton(onClick = onConfirm, variant = KedgeButtonVariant.Primary) {
                    MiuixText(confirmText)
                }
            }
        }
    }
}
