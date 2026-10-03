package heizige.kk.kedge.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar as MdSearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.adaptive.KedgeTopAppBar
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.KedgeTextStyles
import heizige.kk.kedge.theme.LocalKedgeStyle
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.InputField as MiuixSearchInputField
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 药丸 → 全屏的展开动效参数。
 *
 * 逐项照抄 KernelSU `SuperSearchBar`（`SearchStatus.SearchPager` + `SearchBar`），
 * 这样两套风格下「点一下药丸 → 搜索框长成全屏」的手感完全一致：
 *
 * | 动效            | 参数                                | KSU 出处                       |
 * |-----------------|-------------------------------------|--------------------------------|
 * | 搜索框纵向位移  | `tween(300, LinearOutSlowInEasing)` | `SearchPagerTopPadding`        |
 * | 遮罩 surface    | `tween(200, FastOutSlowInEasing)`   | `SearchPagerSurfaceAlpha`      |
 * | 结果区淡入淡出  | `tween(200, FastOutSlowInEasing)`   | `AnimatedVisibility(fadeIn())` |
 * | 取消键          | 横向展开 + 从右滑入                | `expandHorizontally` + `slideInHorizontally` |
 * | 清除键          | 淡入 + 放大                         | `fadeIn() + scaleIn()`         |
 *
 * MD3 分支不再吃 MD3 自带的 600ms/350ms + 100ms delay（那套是给 docked 搜索用的），
 * 而是通过 [rememberContainedSearchBarState] 把同样的时长灌进 `SearchBarState`，
 * 于是 `ExpandedFullScreenContainedSearchBar` 的形变/淡入都按这里的时间线走。
 */
internal object KedgeSearchMotion {

    /** 搜索框从折叠位置移到状态栏下方的时长（KSU 300ms）。 */
    const val BarDurationMillis = 300

    /** 遮罩淡入淡出 / 结果区淡入淡出的时长（KSU 200ms）。 */
    const val ScrimDurationMillis = 200

    /** 展开态搜索框距状态栏底部的间距（KSU `systemBarsPadding + 5.dp`）。 */
    val ExpandedBarTopInset = 5.dp

    /** 展开态药丸的左右外边距（KSU `SearchBarDefaults.InsideMargin` 的 12dp）。 */
    val BarHorizontalPadding = 12.dp

    /** 展开态药丸与取消键的下外边距（KSU `padding(bottom = 6.dp)`）。 */
    val BarBottomPadding = 6.dp

    val bar: AnimationSpec<Dp> = tween(BarDurationMillis, easing = LinearOutSlowInEasing)

    /** MD3 `SearchBarState` 推进的是 Float 进度，所以另给一份同参数的 Float spec。 */
    val barProgress: AnimationSpec<Float> = tween(BarDurationMillis, easing = LinearOutSlowInEasing)
    val scrim: AnimationSpec<Float> = tween(ScrimDurationMillis, easing = FastOutSlowInEasing)

    val contentFadeIn: EnterTransition = fadeIn(tween(ScrimDurationMillis, easing = FastOutSlowInEasing))
    val contentFadeOut: ExitTransition = fadeOut(tween(ScrimDurationMillis, easing = FastOutSlowInEasing))

    val cancelEnter: EnterTransition =
        expandHorizontally(tween(BarDurationMillis, easing = LinearOutSlowInEasing)) +
            slideInHorizontally(tween(BarDurationMillis, easing = LinearOutSlowInEasing)) { it }
    val cancelExit: ExitTransition =
        shrinkHorizontally(tween(BarDurationMillis, easing = LinearOutSlowInEasing)) +
            slideOutHorizontally(tween(BarDurationMillis, easing = LinearOutSlowInEasing)) { it }

    val clearEnter: EnterTransition = fadeIn() + scaleIn()
    val clearExit: ExitTransition = fadeOut() + scaleOut()
}

/** KSU `SearchStatus.Status` 的等价物：折叠药丸与全屏搜索之间需要一个中间态。 */
private enum class SearchPhase { Collapsed, Expanding, Expanded, Collapsing }

/**
 * Miuix 分支的展开状态机（照搬 KSU `SearchStatus`）。
 *
 * 为什么要显式区分 `Expanding` / `Collapsing`：
 * - 展开时搜索框要从**折叠药丸所在的真实 y 坐标**滑到状态栏下方，收起时再滑回去，
 *   中间态决定了这趟补间的起点/终点；
 * - 收起期间全屏遮罩必须继续留在组合里（药丸往下滑、遮罩淡出），动画走完才卸载，
 *   否则全屏层会「啪」地一下消失，看不到回弹。
 */
@Stable
private class MiuixSearchPhaseState {

    var phase: SearchPhase by mutableStateOf(SearchPhase.Collapsed)
        private set

    /** 折叠药丸顶边在窗口内的 y（px），由 `onGloballyPositioned` 喂进来。 */
    var collapsedBarY: Dp by mutableStateOf(0.dp)

    fun expand() {
        if (phase == SearchPhase.Collapsed) phase = SearchPhase.Expanding
    }

    fun collapse() {
        if (phase == SearchPhase.Expanded || phase == SearchPhase.Expanding) {
            phase = SearchPhase.Collapsing
        }
    }

    val isCollapsed: Boolean get() = phase == SearchPhase.Collapsed

    val isOverlayMounted: Boolean get() = phase != SearchPhase.Collapsed

    /** 结果区只在完全展开后出现，和 KSU 的 `visible = searchStatus.isExpand()` 一致。 */
    val showContent: Boolean get() = phase == SearchPhase.Expanded

    /** 收起动画走完 → 真正折叠（此时才清空关键词，KSU 在 `onAnimationComplete` 里做）。 */
    fun onCollapseComplete() {
        if (phase == SearchPhase.Collapsing) phase = SearchPhase.Collapsed
    }

    fun onExpandComplete() {
        if (phase == SearchPhase.Expanding) phase = SearchPhase.Expanded
    }
}

/**
 * KSU 式的药丸搜索框：点一下折叠药丸，展开成铺满全屏的搜索页。
 *
 * 交互与 KernelSU `SuperSearchBar` 一致（展开/收起动效见 [KedgeSearchMotion]）：
 * 折叠态只画一个药丸；展开态是全屏 surface 遮罩，顶部搜索框 + 取消键，下面是
 * [expandedContent]；取消键、返回键收起时搜索框滑回折叠药丸的位置、遮罩淡出、
 * 关键词清空。
 *
 * MD3 与 Miuix 走各自平台的全屏宿主（MD3 是 `ExpandedFullScreenContainedSearchBar`
 * 的 Dialog，Miuix 是同 window 的 Popup 遮罩），但**动效参数、收起语义、清空时机完全一致**。
 *
 * @param value 当前关键词，外部持有。
 * @param onValueChange 关键词变化。收起动画结束时会被调成 `""`（KSU 同款清空时机）。
 * @param active 是否处于展开态，由外部持有。点药丸 → `true`，取消/返回 → `false`。
 * @param onActiveChange 展开态变化回调。注意 `false` 是在收起动画**结束后**才回调的，
 *   这样调用方可以在整个收起过程里继续把结果区留在全屏槽内。
 * @param expandedContent 全屏结果区。
 * @param cancelLabel 取消键文案，**只在 Miuix 分支生效**（MD3 沿用 KSU 的做法：展开后
 *   leadingIcon 变成返回箭头，没有文字取消键）。Kedge 是库、拿不到宿主的 `R`，所以由调用方
 *   传入；传 `null` 时退回系统文案 `android.R.string.cancel`。
 */
@Composable
fun KedgeSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    active: Boolean = false,
    onActiveChange: ((Boolean) -> Unit)? = null,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    expandedContent: @Composable () -> Unit = {},
    cancelLabel: String? = null,
) {
    // 清除键用 KSU 的 fadeIn + scaleIn / fadeOut + scaleOut，而不是「有文字就替换图标」。
    val actualTrailingIcon: @Composable (() -> Unit)? = trailingIcon ?: {
        AnimatedVisibility(
            visible = value.isNotEmpty(),
            enter = KedgeSearchMotion.clearEnter,
            exit = KedgeSearchMotion.clearExit,
        ) {
            IconButton(onClick = { onValueChange("") }) {
                Icon(Icons.Rounded.Close, contentDescription = null)
            }
        }
    }

    val horizontalPadding = when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> 4.dp
        else -> 16.dp
    }

    // The MD3 collapsed field sits flush against whatever is above it, so give it the
    // standard 16dp of breathing room on top; the Miuix bar carries its own spacing.
    val topPadding = when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> 0.dp
        else -> 16.dp
    }

    // The caller modifier is consumed exactly once, by the outer box; the inner bars fill it.
    Box(
        modifier = modifier
            .padding(top = topPadding)
            .padding(horizontal = horizontalPadding),
    ) {
        when (LocalKedgeStyle.current) {
            KedgeStyle.MD3Exp -> Md3SearchBarBridge(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                active = active,
                onActiveChange = onActiveChange,
                enabled = enabled,
                trailingIcon = actualTrailingIcon,
                expandedContent = expandedContent,
            )

            KedgeStyle.Miuix -> MiuixSearchBarOverlay(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                enabled = enabled,
                trailingIcon = actualTrailingIcon,
                active = active,
                onActiveChange = onActiveChange,
                expandedContent = expandedContent,
                cancelLabel = cancelLabel ?: stringResource(android.R.string.cancel),
            )
        }
    }
}

/**
 * Miuix 分支的 KSU 式全屏搜索。
 *
 * 结构照搬 KernelSU `SuperSearchBar` + `SearchStatus.SearchPager`：
 * - 全屏遮罩**常驻**在 [KedgeSearchOverlayLayer]（同 window），折叠时只是
 *   `surface` alpha = 0、药丸停在折叠位置、取消键与结果区都不组合。没有任何 window
 *   的创建/销毁，因此没有窗口动画 —— 这是它比 Dialog/Popup 丝滑的根本原因。
 * - 折叠位那个药丸是「假」的（KSU 的 `SearchBarFake`，`enabled = false`），只负责占位；
 *   全屏层里那个真输入框正好盖在它上面，点真输入框由 Miuix `InputField` 自己的
 *   `onFocusChanged` -> `onExpandedChange` 触发展开，和 KSU 一样。
 * - 假药丸在展开期间 `alpha = 0`，所以收起时屏幕上不会同时出现两个搜索框。
 */
@Composable
private fun MiuixSearchBarOverlay(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    trailingIcon: @Composable (() -> Unit)?,
    active: Boolean,
    onActiveChange: ((Boolean) -> Unit)?,
    expandedContent: @Composable () -> Unit,
    cancelLabel: String,
) {
    val density = LocalDensity.current
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val overlayState = LocalKedgeSearchOverlayState.current
    val phaseState = remember { MiuixSearchPhaseState() }
    val currentOnActiveChange by rememberUpdatedState(onActiveChange)
    val latestOnValueChange by rememberUpdatedState(onValueChange)

    // 折叠位假药丸：不可编辑、不可交互，只负责占位并把真实的 y 喂给状态机。
    // KSU 的 `SearchBarFake` 就是这个角色（`InputField(enabled = false)`）。
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // KSU 各页面里的 `Modifier.alpha(if (searchStatus.isCollapsed()) 1f else 0f)`，
            // 放在 onGloballyPositioned 之前：alpha = 0 不影响布局，y 依然量得到。
            .graphicsLayer { alpha = if (phaseState.isOverlayMounted) 0f else 1f }
            .onGloballyPositioned { coordinates ->
                phaseState.collapsedBarY =
                    with(density) { coordinates.positionInWindow().y.toDp() }
            }
            // KSU 各页面里紧跟 alpha 的那段：
            //   `.then(if (searchStatus.isCollapsed()) Modifier.pointerInput(Unit) {
            //       detectTapGestures { onSearchStatusChange(... EXPANDING) } } else Modifier)`
            // 折叠态全屏层里的真实输入框是不组合的（见下），所以这个「假」药丸就是唯一的
            // 点击入口 —— 少了它就点不开。展开/收起途中不接点击。
            .then(
                if (enabled && phaseState.isCollapsed) {
                    Modifier.pointerInput(Unit) { detectTapGestures { phaseState.expand() } }
                } else {
                    Modifier
                },
            ),
    ) {
        MiuixSearchInputField(
            query = "",
            onQueryChange = {},
            onSearch = {},
            expanded = false,
            onExpandedChange = {},
            label = placeholder,
            enabled = false,
            trailingIcon = null,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    // 外部把 active 置 true 走同一套展开流程。
    LaunchedEffect(active) {
        if (active) phaseState.expand() else phaseState.collapse()
    }

    val barTopPadding = remember { Animatable(0.dp, Dp.VectorConverter) }
    val scrimAlpha = remember { Animatable(0f) }

    LaunchedEffect(phaseState.phase) {
        when (phaseState.phase) {
            SearchPhase.Expanding -> {
                // 起点钉在折叠药丸的实测 y（KSU 的 `max(offsetY, 0.dp)`），再滑到状态栏下方。
                barTopPadding.snapTo(phaseState.collapsedBarY)
                // KSU：遮罩淡入比药丸位移快，且取消键在位移期间不可点。
                launch { scrimAlpha.animateTo(1f, KedgeSearchMotion.scrim) }
                barTopPadding.animateTo(
                    statusBarTop + KedgeSearchMotion.ExpandedBarTopInset,
                    KedgeSearchMotion.bar,
                )
                phaseState.onExpandComplete()
            }

            SearchPhase.Collapsing -> {
                launch { scrimAlpha.animateTo(0f, KedgeSearchMotion.scrim) }
                barTopPadding.animateTo(phaseState.collapsedBarY, KedgeSearchMotion.bar)
                phaseState.onCollapseComplete()
                // KSU 在 COLLAPSING -> COLLAPSED 时清空关键词。
                latestOnValueChange("")
            }

            SearchPhase.Collapsed -> {
                // 回到折叠态：药丸停在折叠位、遮罩全透明。等价于 KSU 那个
                // `animateDpAsState(target = max(offsetY, 0.dp))` 持续跟随。
                barTopPadding.snapTo(phaseState.collapsedBarY)
                scrimAlpha.snapTo(0f)
            }

            SearchPhase.Expanded -> Unit
        }
    }

    // 全屏层挂到根节点的 overlay 槽（同 window，位置同 KSU 的 popupHost）。
    // 内容全部经 rememberUpdatedState 取，所以这个 lambda 身份稳定，只在挂/摘时换。
    if (overlayState != null) {
        val phase = phaseState.phase
        val valueState = rememberUpdatedState(value)
        val onValueChangeState = rememberUpdatedState(onValueChange)
        val placeholderState = rememberUpdatedState(placeholder)
        val enabledState = rememberUpdatedState(enabled)
        val trailingIconState = rememberUpdatedState(trailingIcon)
        val expandedContentState = rememberUpdatedState(expandedContent)
        val cancelLabelState = rememberUpdatedState(cancelLabel)
        val imeBottomState = rememberUpdatedState(imeBottom)
        val statusBarTopState = rememberUpdatedState(statusBarTop)

        val overlayContent: @Composable () -> Unit = {
            val surface = MiuixTheme.colorScheme.surface
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(surface.copy(alpha = scrimAlpha.value)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = barTopPadding.value),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // KSU：`if (!searchStatus.isCollapsed()) { expandBar(...) }` —— 真实输入框
                    // **只在非折叠态组合**。折叠态组合它的话，Miuix `InputField` 里
                    // `LaunchedEffect(expanded) { focusRequester.requestFocus() }` 会立刻
                    // 抢焦点弹键盘，等于一进页面就自动搜索。
                    if (phase != SearchPhase.Collapsed) {
                        Box(modifier = Modifier.weight(1f)) {
                            MiuixSearchInputField(
                                query = valueState.value,
                                onQueryChange = { onValueChangeState.value(it) },
                                onSearch = { phaseState.collapse() },
                                expanded = true,
                                onExpandedChange = { if (it) phaseState.expand() },
                                label = placeholderState.value,
                                enabled = enabledState.value,
                                trailingIcon = trailingIconState.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = KedgeSearchMotion.BarHorizontalPadding)
                                    .padding(bottom = KedgeSearchMotion.BarBottomPadding),
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = phase == SearchPhase.Expanded,
                        enter = KedgeSearchMotion.cancelEnter,
                        exit = KedgeSearchMotion.cancelExit,
                    ) {
                        Text(
                            text = cancelLabelState.value,
                            style = KedgeTextStyles.footnote(),
                            fontWeight = FontWeight.Bold,
                            color = MiuixTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(
                                    start = 4.dp,
                                    end = 16.dp,
                                    top = KedgeSearchMotion.ExpandedBarTopInset,
                                    bottom = KedgeSearchMotion.BarBottomPadding,
                                )
                                .clickable(
                                    interactionSource = null,
                                    enabled = phase == SearchPhase.Expanded,
                                    indication = null,
                                ) {
                                    phaseState.collapse()
                                },
                        )
                    }
                }
                AnimatedVisibility(
                    visible = phase == SearchPhase.Expanded,
                    enter = KedgeSearchMotion.contentFadeIn,
                    exit = KedgeSearchMotion.contentFadeOut,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = imeBottomState.value),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) { expandedContentState.value() }
                }
            }
        }

        // 挂 / 摘。离开页面（导航走、弹窗关掉）时 onDispose 会把全屏层摘掉，
        // 否则它会带着旧的 phaseState 一直留在根节点上 —— 换页后搜索框还在。
        DisposableEffect(overlayState, phase) {
            overlayState.attach(overlayContent)
            onDispose { overlayState.detach(overlayContent) }
        }
    }

    // 把内部相位翻译成对外的布尔量：
    // - true 在**开始展开**就报，让调用方立刻把页面正文抽掉。结果区要到动画结束
    //   （phase 变 Expanded）才挂上，中间隔了整趟动画，不会出现「正文列表和结果列表
    //   同时在场」—— 那种情况下同一批 sharedElement key 会同时注册两份。
    // - false 在收起动画**结束后**报，调用方的 `if (!active) { 页面正文 }` 就不会在
    //   收起过程中提前把结果区抽走。
    LaunchedEffect(phaseState.phase) {
        when (phaseState.phase) {
            SearchPhase.Expanding, SearchPhase.Expanded -> currentOnActiveChange?.invoke(true)
            SearchPhase.Collapsed -> currentOnActiveChange?.invoke(false)
            SearchPhase.Collapsing -> Unit
        }
    }
}

/**
 * MD3 分支：`SearchBar`（折叠药丸）+ `ExpandedFullScreenContainedSearchBar`
 * （全屏 Dialog），两者共用同一个 [androidx.compose.material3.SearchBarState]。
 *
 * 与 KernelSU `material/SearchBar.kt` 的 `SearchAppBar` 同构，差别只在动画时长换成
 * [KedgeSearchMotion]（MD3 自带的是 600ms/350ms + 100ms delay）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Md3SearchBarBridge(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    active: Boolean,
    onActiveChange: ((Boolean) -> Unit)?,
    enabled: Boolean,
    trailingIcon: @Composable (() -> Unit)?,
    expandedContent: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val searchBarState = rememberContainedSearchBarState(
        animationSpecForExpand = KedgeSearchMotion.barProgress,
        animationSpecForCollapse = KedgeSearchMotion.barProgress,
        animationSpecForContentFadeIn = KedgeSearchMotion.scrim,
        animationSpecForContentFadeOut = KedgeSearchMotion.scrim,
    )
    val textFieldState = rememberTextFieldState(value)
    val latestValue by rememberUpdatedState(value)
    val latestOnValueChange by rememberUpdatedState(onValueChange)
    val latestOnActiveChange by rememberUpdatedState(onActiveChange)

    // Text field -> external value.
    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .collect { text ->
                if (text != latestValue) latestOnValueChange(text)
            }
    }
    // External value -> text field.
    LaunchedEffect(value) {
        if (textFieldState.text.toString() != value) {
            textFieldState.setTextAndPlaceCursorAtEnd(value)
        }
    }

    // External active flag drives the expansion state.
    LaunchedEffect(active) {
        when {
            active && searchBarState.targetValue != SearchBarValue.Expanded ->
                searchBarState.animateToExpanded()

            !active && searchBarState.targetValue != SearchBarValue.Collapsed ->
                searchBarState.animateToCollapsed()
        }
    }
    // Internal expansion (user tapping the field) reports back through onActiveChange;
    // clearing on collapse mirrors KSU, where COLLAPSING -> COLLAPSED wipes searchText.
    LaunchedEffect(searchBarState) {
        var wasExpanded = searchBarState.currentValue != SearchBarValue.Collapsed
        snapshotFlow { searchBarState.currentValue }
            .drop(1)
            .collect { current ->
                val expandedNow = current != SearchBarValue.Collapsed
                if (wasExpanded && !expandedNow) latestOnValueChange("")
                wasExpanded = expandedNow
                latestOnActiveChange?.invoke(expandedNow)
            }
    }

    val inputField: @Composable () -> Unit = {
        val expandedNow = searchBarState.targetValue == SearchBarValue.Expanded
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            onSearch = {
                if (searchBarState.targetValue != SearchBarValue.Collapsed) {
                    scope.launch { searchBarState.animateToCollapsed() }
                }
            },
            enabled = enabled,
            placeholder = { Text(placeholder) },
            leadingIcon = {
                if (expandedNow) {
                    IconButton(onClick = { scope.launch { searchBarState.animateToCollapsed() } }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                } else {
                    Icon(Icons.Rounded.Search, contentDescription = null)
                }
            },
            trailingIcon = trailingIcon,
        )
    }

    MdSearchBar(
        state = searchBarState,
        inputField = inputField,
        modifier = Modifier.fillMaxWidth(),
    )
    ExpandedFullScreenContainedSearchBar(
        state = searchBarState,
        inputField = inputField,
        windowInsets = {
            SearchBarDefaults.fullScreenWindowInsets.only(
                WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            expandedContent()
        }
    }
}

@Composable
fun KedgeSearchTopAppBar(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    searchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    placeholder: String = "Search",
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    defaultContent: @Composable BoxScope.() -> Unit = {},
    searchContent: @Composable BoxScope.() -> Unit = {},
) {
    Column(modifier = modifier) {
        KedgeTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = {
                IconButton(onClick = { onSearchActiveChange(true) }) {
                    Icon(Icons.Rounded.Search, contentDescription = null)
                }
                actions()
            },
        )
        AnimatedVisibility(
            visible = searchActive,
            enter = KedgeSearchMotion.contentFadeIn,
            exit = KedgeSearchMotion.contentFadeOut,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                KedgeSearchBar(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = placeholder,
                    active = true,
                    onActiveChange = onSearchActiveChange,
                    expandedContent = { Box(modifier = Modifier.fillMaxSize()) { searchContent() } },
                )
            }
        }
        AnimatedVisibility(
            visible = !searchActive,
            enter = KedgeSearchMotion.contentFadeIn,
            exit = KedgeSearchMotion.contentFadeOut,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) { defaultContent() }
        }
    }
}
