package heizige.kk.kedge.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.components.KedgeButton
import heizige.kk.kedge.components.KedgeTextButton
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import heizige.kk.kedge.components.KedgeCard
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.window.WindowBottomSheet as MiuixWindowBottomSheet
import heizige.kk.kedge.theme.KedgeTextStyles
import heizige.kk.kedge.theme.MiuixTextStyleScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KedgeModalBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!show) return

    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = onDismissRequest,
                modifier = modifier,
                sheetState = sheetState,
            ) {
                KedgeCard {
                    Column(modifier = Modifier.imePadding()) {
                        content()
                    }
                }
            }
        }

        KedgeStyle.Miuix -> MiuixWindowBottomSheet(
            show = show,
            modifier = modifier,
            title = title,
            onDismissRequest = onDismissRequest,
        ) {
            Column { content() }
        }
    }
}

@Composable
fun KedgeBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    KedgeModalBottomSheet(
        show = show,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = title,
        content = content,
    )
}

/**
 * 带标题图标与确认/取消按钮行的底部弹层，签名对齐 app 侧（Khromia）的
 * `PrimaryBottomSheet`。
 *
 * [KedgeBottomSheet] 只有标题文本和内容，这里补上三样它在实际页面里需要的：
 * 标题左侧图标、底部按钮行、内容可滚动。`content` 收到的 `onDismiss` 是"关闭并
 * 收动画"的回调，MD3 的 `ModalBottomSheet` 不需要显式 dismiss（scrim 点击与
 * 返回手势会自动处理），所以该分支传的是 no-op。
 *
 * MD3Exp 分支用 KedgeCard 包裹以对齐现有观感；Miuix 分支交给
 * `MiuixWindowBottomSheet` 自带的标题栏与圆角。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KedgePrimaryBottomSheet(
    visible: Boolean,
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    imageVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
    dismissText: String? = null,
    content: @Composable () -> Unit,
) {
    if (!visible) return

    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                modifier = modifier,
                sheetState = sheetState,
            ) {
                KedgeCard {
                    Column(modifier = Modifier.imePadding()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (imageVector != null) {
                                Icon(
                                    imageVector = imageVector,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(Modifier.width(12.dp))
                            }
                            androidx.compose.material3.Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        content()
                        if (confirmText != null || dismissText != null) {
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                            ) {
                                if (dismissText != null) {
                                    KedgeTextButton(onClick = onDismiss) { Text(dismissText) }
                                }
                                if (confirmText != null && onConfirm != null) {
                                    KedgeButton(onClick = onConfirm) { Text(confirmText) }
                                }
                            }
                        }
                    }
                }
            }
        }

        KedgeStyle.Miuix -> MiuixWindowBottomSheet(
            show = visible,
            modifier = modifier,
            title = title,
            onDismissRequest = onDismiss,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    // 确认按钮行原本紧贴 sheet 底边：手势导航条会压住它，三键导航更会直接
                    // 裁掉下半截（用户截图里「添加」只露出上半）。这里补上安全区内边距。
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
            ) {
                content()
                if (confirmText != null || dismissText != null) {
                    Spacer(Modifier.height(12.dp))
                    // 按钮文案是裸 Text，给套上 Miuix 字阶（KSU 的弹层按钮同理）
                    MiuixTextStyleScope(KedgeTextStyles.body()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (dismissText != null) {
                                KedgeTextButton(onClick = onDismiss) {
                                    top.yukonga.miuix.kmp.basic.Text(dismissText)
                                }
                            }
                            if (confirmText != null && onConfirm != null) {
                                KedgeButton(onClick = onConfirm) {
                                    top.yukonga.miuix.kmp.basic.Text(confirmText)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
