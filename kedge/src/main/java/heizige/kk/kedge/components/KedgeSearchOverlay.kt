package heizige.kk.kedge.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * 全屏搜索层的宿主槽 —— 就是 KernelSU `Scaffold(popupHost = ...)` 的等价物，只是
 * 提到了根节点，这样不管 `KedgeSearchBar` 被塞在哪个 LazyColumn item / Column /
 * bottom sheet 里，全屏层都能盖住整个 app。
 *
 * ## 为什么必须是同 window 的，不能用 Popup / Dialog
 *
 * KSU 的 `SearchPager` 一直挂在 popupHost 上，**折叠时也照样组合**，只有三个东西在变：
 * `drawBehind` 的 surface alpha、药丸的 top padding、以及取消键/结果区的可见性。
 * 整个展开过程里没有任何 window 被创建或销毁，所以只有内容动画。
 *
 * 换成独立窗口会连着坏三处：
 * - Dialog 自带平台 window enter/exit 动画（缩放 + 淡入），叠在内容动画上 -> 不丝滑；
 * - 收起时窗口还在往下滑，底下页面那个真药丸已经可见 -> 看到两个搜索框；
 * - 药丸起点靠 `positionInWindow()` 跨窗口换算，起点对不上就「弹起来又掉回去」。
 *
 * 同 window 就没有这三个问题：坐标天然可比、假药丸 alpha 淡出后画面上只剩一个搜索框。
 */
@Stable
class KedgeSearchOverlayState internal constructor() {

    /** 当前全屏层内容；`null` 表示没有搜索框处于展开过程。 */
    var content: (@Composable () -> Unit)? by mutableStateOf(null)
        private set

    internal fun attach(content: @Composable () -> Unit) {
        if (this.content !== content) this.content = content
    }

    /** 只摘掉自己挂上去的那一份，别把后来者覆盖掉。 */
    internal fun detach(content: @Composable () -> Unit) {
        if (this.content === content) this.content = null
    }
}

val LocalKedgeSearchOverlayState = staticCompositionLocalOf<KedgeSearchOverlayState?> { null }

@Composable
fun rememberKedgeSearchOverlayState(): KedgeSearchOverlayState = remember { KedgeSearchOverlayState() }

/**
 * 放在根节点 [Box] 的**最后一个子项**，全屏搜索层就会盖在所有页面之上。
 *
 * 折叠态这个 Box 自己是透明的、也没有 pointer input，所以不吃触摸；命中测试会落到
 * 下面的页面控件上 —— 这点和 KSU 一样（KSU 靠 popupHost 放在 Scaffold 内容之上、
 * 折叠态只画全透明 surface）。
 */
@Composable
fun KedgeSearchOverlayLayer(
    state: KedgeSearchOverlayState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        state.content?.invoke()
    }
}
