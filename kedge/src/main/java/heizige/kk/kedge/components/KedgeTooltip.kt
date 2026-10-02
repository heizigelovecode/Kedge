package heizige.kk.kedge.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import heizige.kk.khromia.components.TextTooltip
import top.yukonga.miuix.kmp.basic.TooltipBox as MiuixTooltipBox
import androidx.compose.material3.TooltipBox as MdTooltipBox
import androidx.compose.material3.TooltipDefaults as MdTooltipDefaults
import androidx.compose.material3.rememberTooltipState as rememberMdTooltipState

/**
 * 纯文字 Tooltip 的统一入口，按当前风格在 MD3 / Miuix 之间分流。
 *
 * MD3 走 Khromia 的 [TextTooltip]（`PlainTooltip` + CircleShape + harmonizeWithPrimary
 * 配色 + 0.8 alpha）；Miuix 走 Miuix 自带的 `TooltipBox(text = ...)`，用 Miuix 自己的
 * `TooltipDefaults` 配色、圆角、insideMargin 和 `textStyles.body2` 字体，形态与
 * KernelSU 一致。
 *
 * 之所以包一层而不是让调用点自己 `when`：两边的 `TooltipBox` 接收者类型不同
 * （Material3 的 `TooltipScope` vs Miuix 的 `TooltipScope`），同一个 lambda 在两边
 * 编译不出来，收口在这里才能让调用点写成一行。
 *
 * Miuix 分支用 Miuix 那个带 `text: String` 的便捷重载，它内部已经用
 * `MiuixTheme.textStyles.body2` 排版并限制两行省略；MD3 分支保持 Khromia 原样不动。
 */
@Composable
fun KedgeTextTooltipBox(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> MiuixTooltipBox(
            text = text,
            modifier = modifier,
            content = content,
        )

        KedgeStyle.MD3Exp -> MdTooltipBoxWithText(text = text, modifier = modifier, content = content)
    }
}

/** MD3 分支单独抽出来只为挂 [ExperimentalMaterial3Api] 的 OptIn。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MdTooltipBoxWithText(
    text: String,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    MdTooltipBox(
        modifier = modifier,
        positionProvider = MdTooltipDefaults.rememberTooltipPositionProvider(),
        tooltip = { TextTooltip(text) },
        state = rememberMdTooltipState(),
        content = content,
    )
}