package heizige.kk.kedge.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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

/**
 * **sheet 内**（或任何"想让搜索层留在自己边界里"的容器内）的局部搜索层槽。
 *
 * 与 [LocalKedgeSearchOverlayState] 的区别只有落点：
 * - 根部那个槽挂在 Activity 根节点（同 window 的最上层），sheet 是独立窗口时它永远在
 *   sheet **下面**（Miuix `WindowBottomSheet` 与 Khromia `BasicBottomSheet` 都会建窗口），
 *   于是搜索层画在 sheet 的背景上、sheet 里看不到 —— 用户报的就是这个；
 * - 本槽由**调用点自己**在 sheet 的内容区里 provide，展开层因此是调用点子树里的一个普通
 *   `Box`，天然落在 sheet 的布局范围内（被 sheet 的内容槽约束，不需要任何 window）。
 *
 * 只在 sheet 内容区 provide 即可，`KedgeSearchBar` 会优先用它；**不 provide 的地方
 * （页面级调用点）行为逐字不变**，仍然走根部槽 / MD3 的全屏 Dialog。
 */
val LocalKedgeSearchLocalHost = staticCompositionLocalOf<KedgeSearchLocalHost?> { null }

/**
 * [LocalKedgeSearchLocalHost] 的状态：与 [KedgeSearchOverlayState] 同构（挂/摘一份内容），
 * 外加一个「本槽在窗口里的 y」—— 药丸的 y 是窗口坐标，展开时的起飞/落点要换算成槽内相对
 * 坐标，否则收起动画会往 sheet 外面跑。
 */
@Stable
class KedgeSearchLocalHost internal constructor() {

    /** 当前局部层内容；`null` 表示没有搜索框处于展开过程。 */
    var content: (@Composable () -> Unit)? by mutableStateOf(null)
        private set

    /** 本槽顶边在窗口内的 y（dp），由 [KedgeSearchLocalLayer] 量出来喂进来。 */
    var topInWindow: Dp by mutableStateOf(0.dp)

    internal fun attach(content: @Composable () -> Unit) {
        if (this.content !== content) this.content = content
    }

    /** 只摘掉自己挂上去的那一份，别把后来者覆盖掉。 */
    internal fun detach(content: @Composable () -> Unit) {
        if (this.content === content) this.content = null
    }
}

@Composable
fun rememberKedgeSearchLocalHost(): KedgeSearchLocalHost = remember { KedgeSearchLocalHost() }

/**
 * **sheet 内容区的搜索宿主**：把 `content` 与展开层放进同一个 `Box`，展开层用
 * `matchParentSize()` ⇒ 恰好是 `content` 占的那块区域（sheet 给内容槽多大就是多大），
 * 因此展开态**完全落在 sheet 内**，也天然被圆角/边界约束。
 *
 * 布局中立：外层 `Box` 没有 `fillMaxSize()`，尺寸仍由 `content` 决定，所以给不含搜索框的
 * sheet 包一层也不会改变它的高度（`matchParentSize` 的子节点不参与父节点测量）。
 *
 * 用法（在 sheet 的内容槽里包一层即可，调用点自己的 `KedgeSearchBar` 会自动走子树内展开）：
 * ```
 * PrimaryBottomSheet(...) { onDismiss ->
 *     KedgeSearchSheetHost { content(onDismiss) }
 * }
 * ```
 *
 * ⚠️ 必须包在**传给 sheet 的 content lambda 内部**：Compose 的 CompositionLocal 只向子树
 * 传播，而 sheet（Miuix `WindowBottomSheet` / Khromia `BasicBottomSheet`）会把内容重新组合到
 * 自己的窗口/子组合里 —— 包在 lambda 外面，provide 覆盖不到窗口里的那份内容。
 */
@Composable
fun KedgeSearchSheetHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val host = rememberKedgeSearchLocalHost()
    Box(modifier = modifier) {
        CompositionLocalProvider(LocalKedgeSearchLocalHost provides host) {
            content()
        }
        KedgeSearchLocalLayer(host, Modifier.matchParentSize())
    }
}

/**
 * 放在 sheet 内容区的那个 `Box` 里（建议 `Modifier.matchParentSize()`），展开层就画在本
 * `Box` 的边界内。
 *
 * 自己量一次窗口 y 存进 [KedgeSearchLocalHost.topInWindow]，供 [KedgeSearchBar] 把药丸的
 * 窗口坐标换算成槽内坐标。
 */
@Composable
fun KedgeSearchLocalLayer(
    host: KedgeSearchLocalHost,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                host.topInWindow = with(density) { coordinates.positionInWindow().y.toDp() }
            },
    ) {
        host.content?.invoke()
    }
}
