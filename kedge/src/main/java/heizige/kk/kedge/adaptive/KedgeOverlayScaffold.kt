package heizige.kk.kedge.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.blur.LayerBackdrop

/**
 * 顶栏 / 底栏**叠在内容之上**的骨架，用于侧边栏（drawer）这类"内容贯通、
 * 栏浮在上面"的布局。
 *
 * 与 [KedgePageScaffold] 的区别：那个是上下平铺（content 只占中间），
 * 这个是三层重叠：
 *
 * ```
 * Box {
 *   KedgeBlurSurface { content }   // 铺满，backdrop 只录这一层
 *   topBar()                       // 叠在上面
 *   bottomBar()                    // 叠在上面
 * }
 * ```
 *
 * 层次不变的前提下顶/底栏才有毛玻璃可透：模糊采样的是它们**下面**的内容，
 * 而不是彼此。
 *
 * 关键约束：
 * - 顶栏/底栏**不能**放进 [KedgeBlurSurface] —— 那样它们会采样到含自己的
 *   图层，导致崩溃或糊成一片（KSU 的 `BlurredBar` 同样有这个要求）。
 * - [contentInsetTop] / [contentInsetBottom] 要**加在 content 上**，让首尾
 *   内容不被栏盖住。调用方传栏的实际高度（**含**状态栏/导航栏 insets）。
 *   这里不去自动测量：栏外面包着 `KedgeBlurredBar`，毛玻璃层上报的尺寸
 *   不可靠，实测会把 content 整体顶飞。
 *
 * @param backdrop 由调用方用 [rememberKedgeBlurBackdrop] 建立并下发 —— 栏要
 *   消费同一个 backdrop 才有模糊，所以不能在这里另建。每屏一个，多建会多录
 *   一份图层。传 null 则退化为不透明底色，层次关系不变。
 */
@Composable
fun KedgeOverlayScaffold(
    backdrop: LayerBackdrop?,
    modifier: Modifier = Modifier,
    contentInsetTop: Dp = 0.dp,
    contentInsetBottom: Dp = 0.dp,
    // 栏是叠在 Box 上的图层，收 BoxScope 让调用方能自己 align
    // （底栏需要 align(Alignment.BottomCenter)，否则会被摆在左上角）。
    topBar: @Composable BoxScope.() -> Unit = {},
    bottomBar: @Composable BoxScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        KedgeBlurSurface(backdrop = backdrop, modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = contentInsetTop, bottom = contentInsetBottom),
            ) {
                content()
            }
        }
        Box(modifier = Modifier.align(Alignment.TopCenter)) { topBar() }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) { bottomBar() }
    }
}

/**
 * 栏在毛玻璃下的底色：有 blur 时必须透明，否则模糊无从透出；
 * 没有 blur 时退回不透明底色，避免文字压在内容上看不清。
 */
@Composable
fun KedgeOverlayBarColor(backdrop: LayerBackdrop?, fallback: androidx.compose.ui.graphics.Color) =
    if (backdrop != null) androidx.compose.ui.graphics.Color.Transparent else fallback