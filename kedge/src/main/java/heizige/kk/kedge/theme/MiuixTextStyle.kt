package heizige.kk.kedge.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle

/**
 * 在 Miuix 下把 MD3 的 `LocalTextStyle` 指向指定的 Miuix 字阶，让插槽里**没写 style**
 * 的裸 `Text(...)` 也能拿到 Miuix 的字号字重。
 *
 * 为什么需要：`KedgeTheme` 的 Miuix 分支只桥接配色，`MaterialTheme.typography` 和
 * `LocalTextStyle` 都还是 MD3 的。所以凡是「容器提供样式、内容写裸 Text」的地方
 * （OptionItem 的标题/摘要、弹窗的按钮与正文等），Miuix 下都会漏出 MD3 字阶。
 *
 * 只影响**没显式写 style** 的 Text；写了 `style =` 的仍以显式值为准，那种情况要
 * 换用 [KedgeTextStyles]。
 *
 * 用法：`MiuixTextStyleScope(KedgeTextStyles.title()) { … }`
 */
@Composable
fun MiuixTextStyleScope(
    style: TextStyle,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalTextStyle provides style, content = content)
}