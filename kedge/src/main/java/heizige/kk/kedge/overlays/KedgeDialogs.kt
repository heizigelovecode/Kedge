package heizige.kk.kedge.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.components.KedgeButton
import heizige.kk.kedge.components.KedgeButtonVariant
import heizige.kk.kedge.components.KedgeCard
import heizige.kk.kedge.components.KedgeTextButton
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import heizige.kk.khromia.components.AnimatedAlertDialog
import heizige.kk.khromia.components.AnimatedDialogWindow
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.window.WindowDialog as MiuixWindowDialog
import heizige.kk.kedge.theme.KedgeTextStyles
import heizige.kk.kedge.theme.MiuixTextStyleScope

@Composable
fun KedgeDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    summary: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> AnimatedDialogWindow(
            visible = show,
            onDismissRequest = onDismissRequest,
        ) {
            KedgeCard(modifier = modifier) {
                if (title != null) Text(text = title)
                if (summary != null) Text(text = summary)
                content()
            }
        }

        KedgeStyle.Miuix -> if (show) MiuixWindowDialog(
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
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> AnimatedAlertDialog(
            visible = show,
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

        KedgeStyle.Miuix -> if (show) MiuixWindowDialog(
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

/**
 * slot 版确认弹窗，签名对齐 MD3 `AlertDialog`。
 *
 * 与 [KedgeAlertDialog] 的区别：后者的 title/text 是 `String`，只能显示纯文本；
 * 这里全是 `@Composable` 插槽，调用方能塞进 Miuix `Text`、图标、自定义布局等。
 * app 侧的 `AppAlertDialog` 就是这个形状，直接换成 Kedge 组件即可双风格化。
 *
 * Miuix 分支下 title/text/icon 依次竖排（icon 在上），确认/取消按钮横排在右下，
 * 与 KernelSU 的弹窗一致；确认按钮用 [KedgeButton] Primary 以跟随 Miuix 配色。
 */
@Composable
fun KedgeAlertDialogSlots(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> AnimatedAlertDialog(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            icon = icon,
            title = title,
            text = text,
        )

        KedgeStyle.Miuix -> MiuixWindowDialog(
            show = true,
            onDismissRequest = onDismissRequest,
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) { icon() }
                }
                // 插槽里通常是调用方写的裸 Text(...)（没写 style），所以给每档
                // 容器套 Miuix 字阶，否则弹窗正文会是 MD3 字号。
                if (title != null) {
                    MiuixTextStyleScope(KedgeTextStyles.title()) {
                        Box(modifier = Modifier.fillMaxWidth()) { title() }
                    }
                }
                if (text != null) {
                    MiuixTextStyleScope(KedgeTextStyles.body()) {
                        Box(modifier = Modifier.fillMaxWidth()) { text() }
                    }
                }
                MiuixTextStyleScope(KedgeTextStyles.body()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (dismissButton != null) { dismissButton() }
                        confirmButton()
                    }
                }
            }
        }
    }
}
