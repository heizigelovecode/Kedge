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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
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
import top.yukonga.miuix.kmp.layout.BottomSheetDefaults
import top.yukonga.miuix.kmp.window.WindowBottomSheet as MiuixWindowBottomSheet
import heizige.kk.kedge.theme.KedgeTextStyles
import heizige.kk.kedge.theme.MiuixTextStyleScope

/*
 * 两个入口共用的背景说明（不是 KDoc，避免悬空 KDoc；两个常量的 KDoc 见下）。
 *
 * Kedge 两个 Miuix 底部弹层入口的 `insideMargin`：**纵向恒取库默认值，横向按对应 MD3
 * 分支那层容器的内缩取值**（两条路径的 MD3 容器不同，所以不能共用一个常量）。
 *
 * 为什么横向必须显式给值：Miuix 把 `insideMargin` 施加在 sheet 表面**内部**
 * （`top.yukonga.miuix.kmp.layout.BottomSheetContentLayout` 的
 * `.padding(horizontal = insideMargin.width)`，库默认
 * `BottomSheetDefaults.insideMargin = DpSize(24.dp, 0.dp)`），拖拽把手、标题行、调用方
 * 内容全都被无条件缩进 24dp；不改的话，调用方按 MD3 调好的 padding 在 Miuix 下会凭空
 * 多出 24dp（16 → 40、24 → 48）。这里的横向值 + 调用方 padding 必须等于 MD3 侧的内缩 +
 * 同一份调用方 padding。
 *
 * 纵向为什么读库常量而不是写死 0.dp：库默认是 0.dp，读常量是为了将来库若给 sheet
 * 底部加内边距时能自动跟随，不用回来改这个文件。
 */

/**
 * [KedgeModalBottomSheet] 的 Miuix 分支**缺省**横向内缩：对齐该函数 MD3 分支那层
 * `KedgeCard` 的默认 `contentPadding = PaddingValues(16.dp)`（`KedgeContainers.kt`），
 * 让两个分支对同一调用方给出相同的贴边距离（ExploreMarketPage / MediaFilterSheet 用的
 * 就是这个缺省值）。
 *
 * 它只是缺省值：调用点的 MD3 对位容器不是 KedgeCard 时，用
 * [KedgeModalBottomSheet] 的 `miuixInsideMarginWidth` 按调用点覆盖（`GreetingPage` 的
 * MD3 侧走 Khromia `PrimaryBottomSheet`＝零内缩，那里显式传 0.dp）。
 */
private val MiuixModalSheetInsideMarginWidth = 16.dp

/**
 * [KedgePrimaryBottomSheet] 的 Miuix 分支横向内缩：MD3 侧走 KhatKit 的
 * `PrimaryBottomSheet` → Khromia `PrimaryBottomSheet`，其内容容器是
 * `Column(Modifier.fillMaxWidth())`（Khromia `components/BottomSheet.kt`），横向零内缩，
 * 所以这里保持 0.dp。该入口自身的 MD3Exp 分支在生产里没有调用者（按风格分流的
 * `PrimaryBottomSheet.kt` 已经把 Miuix 全部分到本入口），所以横向以 Khromia 那侧为准。
 */
private val MiuixPrimarySheetInsideMarginWidth = 0.dp

/**
 * MD3Exp 走 `ModalBottomSheet` + `KedgeCard`；Miuix 走 `MiuixWindowBottomSheet`，其
 * `insideMargin` 的横向分量按调用点取值，纵向则恒取库默认
 * `BottomSheetDefaults.insideMargin.height`。
 *
 * @param miuixInsideMarginWidth 仅 Miuix 分支生效的横向内缩，缺省
 *   [MiuixModalSheetInsideMarginWidth] = 16dp（与该函数 MD3 分支那层 KedgeCard 的
 *   `contentPadding` 对齐）。调用点的 MD3 对位容器不是 KedgeCard 时按调用点覆盖
 *   （`GreetingPage` 的 MD3 侧走 Khromia `PrimaryBottomSheet`＝零内缩，那里显式传 0.dp），
 *   使「Miuix 容器内缩 + 调用方 padding」等于「MD3 容器内缩 + 同一份调用方 padding」。
 *   MD3 分支不受本参数影响。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KedgeModalBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    miuixInsideMarginWidth: Dp = MiuixModalSheetInsideMarginWidth,
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
            // 横向按调用点的 [miuixInsideMarginWidth] 取值（缺省 16dp = MD3 分支那层
            // KedgeCard 的 contentPadding），抵消库无条件的 24dp 左右内部缩进；纵向恒取库默认。
            insideMargin = DpSize(
                width = miuixInsideMarginWidth,
                height = BottomSheetDefaults.insideMargin.height,
            ),
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
 * MD3Exp 分支用 KedgeCard 包裹以对齐现有观感，但**该分支在生产里没有调用者**：按风格
 * 分流的 `PrimaryBottomSheet.kt` 已经把 MD3 全部交给 Khromia、Miuix 全部交给本入口。
 * 真正对上的是 Miuix 分支（本函数）与 Khromia 的 `PrimaryBottomSheet`：后者内容容器零
 * 横向内缩，所以 Miuix 分支把 `insideMargin` 横向保持 0.dp
 * （见 [MiuixPrimarySheetInsideMarginWidth]）——拖拽把手与标题行在 Miuix 里是居中的，不受影响。
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
            // 见 [MiuixPrimarySheetInsideMarginWidth]：横向 0.dp 与 Khromia PrimaryBottomSheet
            // 的零内缩一致，同时抵消 Miuix 无条件施加的 24dp 左右内部缩进。
            insideMargin = DpSize(
                width = MiuixPrimarySheetInsideMarginWidth,
                height = BottomSheetDefaults.insideMargin.height,
            ),
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
