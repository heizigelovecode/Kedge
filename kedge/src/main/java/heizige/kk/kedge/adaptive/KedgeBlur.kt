package heizige.kk.kedge.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.shader.isRenderEffectSupported
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 全局毛玻璃开关。默认值 true，宿主可在根部用 [ProvideKedgeBlur] 覆盖。
 *
 * 照搬 KernelSU 的 `LocalEnableBlur` 设计：关闭时所有模糊调用点自然退化为纯色，
 * 调用方无需写 if。
 */
val LocalKedgeEnableBlur = staticCompositionLocalOf { true }

/**
 * 在根部下发毛玻璃开关。
 *
 * 这里**只下发开关，不建立也不录制任何 [LayerBackdrop]**。原因：miuix 的
 * `textureBlur` 会去采样 backdrop，而 backdrop 录制的是挂在它上面的那棵子树。
 * 如果在根部录制整棵 app 树，顶栏就会「一边被录进 backdrop、一边又采样它」，
 * 形成自引用，实测直接崩在进入页面的瞬间。KernelSU 的做法是每屏一个 backdrop，
 * 只包住列表内容，顶栏作为兄弟节点采样，见 [rememberKedgeBlurBackdrop] +
 * [KedgeBlurSurface] + [KedgeBlurredBar] 的组合。
 */
@Composable
fun ProvideKedgeBlur(
    enabled: Boolean = LocalKedgeEnableBlur.current,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalKedgeEnableBlur provides enabled) {
        content()
    }
}

/**
 * 建立本屏的模糊层；开关关闭或系统不支持 RenderEffect 时返回 null，
 * 此时 [KedgeBlurredBar] 会退化为不透明底色。
 *
 * 每屏各自持有自己的 backdrop，与 KernelSU `rememberBlurBackdrop` 一致。
 */
@Composable
fun rememberKedgeBlurBackdrop(enabled: Boolean): LayerBackdrop? {
    if (!enabled || !isRenderEffectSupported()) return null
    val surfaceColor = MiuixTheme.colorScheme.surface
    return rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }
}

/**
 * 模糊录制区：把 [backdrop] 挂到这层 Box 上，让它录制 [content] 的绘制结果。
 *
 * 只能包住「会被顶栏盖住的那部分内容」（列表、页面主体），
 * **绝不能把采样它的顶栏一起包进来**。
 */
@Composable
fun KedgeBlurSurface(
    backdrop: LayerBackdrop?,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.then(
            if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier,
        ),
        content = content,
    )
}

/**
 * 页面级 backdrop：只由 [KedgePageScaffold] 下发，顶栏通过它取用。
 *
 * 不变量：提供者包住整个 Scaffold，而 [KedgeBlurSurface] 只包住 Scaffold 的 content 槽，
 * 因此顶栏永远在录制区之外，不会自引用。根部 [ProvideKedgeBlur] **不允许**下发它。
 */
val LocalKedgePageBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }

/**
 * 本屏的折叠滚动行为，仅 Miuix 风格下由 [KedgePageScaffold] 建立。
 *
 * 为什么必须由 Scaffold 建立并下发，而不是顶栏自己 `remember` 一个：
 * `Modifier.nestedScroll` 只会把滚动增量喂给**挂在同一 modifier 链上**的那个
 * 实例。Miuix 的大标题收缩完全由 `scrollBehavior.state` 驱动，所以
 * 「Scaffold 连接的实例」和「TopAppBar 观察的实例」必须是同一个，否则列表能滚
 * 但大标题纹丝不动（KernelSU 的做法就是把同一个 `MiuixScrollBehavior` 同时给
 * `Scaffold` 的 nestedScroll 和 `TopAppBar`）。
 *
 * MD3 风格下为 null：MD3 顶栏自己持有 `TopAppBarScrollBehavior`，由页面自己接
 * nestedScroll，不走这条通路。
 */
val LocalKedgePageScrollBehavior = staticCompositionLocalOf<KedgeScrollBehavior?> { null }

/**
 * 页面级毛玻璃 Scaffold：等价于 MD3 `Scaffold`，额外做两件事：
 *
 * 1. Miuix 风格且开关打开时，为本页建立一个 [LayerBackdrop]；
 * 2. 把它通过 [LocalKedgePageBackdrop] 下发给顶栏，同时只包住 content 槽做录制。
 *
 * 顶栏（消费者）与内容（录制者）因此是兄弟节点，与 KernelSU `SettingsMiuix.kt`
 * 的结构一致。页面代码只需把 `Scaffold(` 换成 `KedgePageScaffold(`；
 * 非 Miuix 风格时 backdrop 为 null，完全退化为原生 Scaffold 行为。
 */
@Composable
fun KedgePageScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    /**
     * MD3 的滚动行为。Miuix 下用它派生 Miuix 的折叠行为并自动接上 nestedScroll ——
     * 否则页面挂的是 MD3 连接，而 Miuix Scaffold 只消费 Miuix 连接，滚动手势会被吞掉
     * （表现为「页面滚不动」）。
     */
    md3ScrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val isMiuix = LocalKedgeStyle.current == KedgeStyle.Miuix
    // Miuix 折叠行为：本屏**唯一**一个实例，既接 nestedScroll 又下发给顶栏。
    // 以前这里是「看 md3ScrollBehavior != null 才建」，而顶栏那边又自己 remember
    // 了另一个，两边实例不同 -> 大标题永远不收缩。现在统一由这里下发。
    val pageBehavior = if (isMiuix) {
        rememberKedgeExitUntilCollapsedScrollBehavior()
    } else {
        null
    }
    val backdrop = if (isMiuix) {
        rememberKedgeBlurBackdrop(LocalKedgeEnableBlur.current)
    } else {
        null
    }
    CompositionLocalProvider(
        LocalKedgePageBackdrop provides backdrop,
        LocalKedgePageScrollBehavior provides pageBehavior,
    ) {
        if (isMiuix) {
            // Miuix 下用 Miuix Scaffold：它的 content 槽、overscroll 与
            // nestedScroll 消费都是 Miuix 原生实现。MD3 Scaffold 在 Miuix 模式
            // 下会让页面滚动失效（列表拿不到可滚动的嵌套连接）。
            MiuixPageScaffold(
                topBar = topBar,
                bottomBar = bottomBar,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = floatingActionButtonPosition,
                // 用 Miuix 的 nestedScroll 连接，否则大标题不跟随滚动折叠
                modifier = modifier.then(
                    pageBehavior?.let {
                        Modifier.nestedScroll(it.nestedScrollConnection)
                    } ?: Modifier,
                ),
                backdrop = backdrop,
                content = content,
            )
            return@CompositionLocalProvider
        }
        Scaffold(
            modifier = modifier,
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            containerColor = containerColor,
            contentColor = contentColor,
            contentWindowInsets = contentWindowInsets,
        ) { padding ->
            // 不做任何包装，直接把 content 交给调用方：
            // KedgeBlurSurface 是 Box，包一层会打断 ColumnScope/RowScope，
            // 让业务代码里的 Modifier.weight(1f) 失效 -> 页面滚不动。
            // 毛玻璃改为由各页自己在需要时用 KedgeBlurSurface 包裹内容区。
            content(padding)
        }
    }
}

/**
 * 顶栏毛玻璃包装：backdrop 可用时套 `textureBlur` 并让内容透出，
 * 否则原样渲染（调用方据 backdrop 是否为 null 决定底色透明度）。
 *
 * [backdrop] 必须显式传入，且必须来自**没有包住本组件**的 [KedgeBlurSurface]，
 * 否则就是自引用。默认 null 表示不模糊。
 *
 * 参数与 KernelSU `ui/util/BlurExt.kt` 的 `BlurredBar` 保持一致。
 */
@Composable
fun KedgeBlurredBar(
    backdrop: LayerBackdrop? = null,
    modifier: Modifier = Modifier,
    blurRadius: Float = 25f,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = if (backdrop != null) {
            modifier.textureBlur(
                backdrop = backdrop,
                shape = RectangleShape,
                blurRadius = blurRadius,
                colors = BlurColors(
                    blendColors = listOf(
                        BlendColorEntry(color = MiuixTheme.colorScheme.surface.copy(0.87f)),
                    ),
                ),
            )
        } else {
            modifier
        }
    ) {
        content()
    }
}

/**
 * Miuix 版页面骨架：等价于 Miuix `Scaffold`，但 topBar 已被 Kedge 的双风格
 * 顶栏包裹过（毛玻璃），这里只负责布局与滚动。
 */
@Composable
private fun MiuixPageScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    backdrop: LayerBackdrop? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    MiuixScaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        // Miuix 自带一个 internal 的 FabPosition value class，与 MD3 同名但类型不同，
        // 必须逐值映射（两边都是 Start/Center/End/EndOverlay）。
        floatingActionButtonPosition = when (floatingActionButtonPosition) {
            FabPosition.Start -> top.yukonga.miuix.kmp.basic.FabPosition.Start
            FabPosition.Center -> top.yukonga.miuix.kmp.basic.FabPosition.Center
            FabPosition.EndOverlay -> top.yukonga.miuix.kmp.basic.FabPosition.EndOverlay
            else -> top.yukonga.miuix.kmp.basic.FabPosition.End
        },
        contentWindowInsets = WindowInsets.systemBars
            .add(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Horizontal),
    ) { padding ->
        // Miuix Scaffold 内部是 `Box { content(contentPadding) }`（Scaffold.kt:180），
        // 这层 Box 在松散约束下 wrap content。所以这里用 fillMaxSize 的父节点
        // 兜住高度：父节点自身在 SubcomposeLayout 给定的紧约束下量到确定高度，
        // 业务侧的 LazyColumn(Modifier.fillMaxSize()) 就能拿到有效 maxHeight。
        //
        // layerBackdrop 也挂在这层：只有 content 槽被录进 backdrop，顶栏是它的
        // **兄弟节点**（在 topBar 槽里），因此顶栏采样时不会把自己录进去，
        // 不构成自引用。对齐 KernelSU `HomeMiuix.kt:108` 的 `Box(Modifier.layerBackdrop)`。
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier,
                ),
        ) {
            content(padding)
        }
    }
}

/** 顶栏底色：模糊生效时必须透明，否则模糊无从透出。 */
@Composable
fun rememberKedgeTopBarColor(backdrop: LayerBackdrop?): Color =
    if (backdrop != null) Color.Transparent else MiuixTheme.colorScheme.surface
