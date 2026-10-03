package heizige.kk.kedge.components

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.onClick
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.adaptive.KedgeTopAppBar
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.KedgeTextStyles
import heizige.kk.kedge.theme.LocalKedgeStyle
import kotlin.math.hypot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.SearchBarDefaults as MiuixSearchBarDefaults
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
 * Miuix 分支的遮罩不走 KSU 那条 200ms 的 surface 淡入淡出，而是改成 420ms 的**圆形揭幕**：
 * 圆心钉在**屏幕顶边、水平居中**，半径从 0 扩到盖满整屏（见 [scrimWipe]）。
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

        /** 展开态药丸与取消键的下外边距（KSU `padding(bottom = 6.dp)`）。 */
    val BarBottomPadding = 6.dp

    val bar: AnimationSpec<Dp> = tween(BarDurationMillis, easing = LinearOutSlowInEasing)

    /** MD3 `SearchBarState` 推进的是 Float 进度，所以另给一份同参数的 Float spec。 */
    val barProgress: AnimationSpec<Float> = tween(BarDurationMillis, easing = LinearOutSlowInEasing)
    /**
 * MD3 分支的内容淡入淡出时长。
 *
 * 只给 `rememberContainedSearchBarState` 用 —— MD3 的全屏 Dialog 自己管遮罩与内容的
 * 淡入淡出。Miuix 分支不用它：那边的遮罩走 [scrimWipe] 的**圆形揭幕**。
 */
val scrim: AnimationSpec<Float> = tween(ScrimDurationMillis, easing = FastOutSlowInEasing)

    /**
     * Miuix 遮罩揭幕的时长。
     *
     * 单独一个常量，不复用 [ScrimDurationMillis]：200ms（KSU 的淡入淡出时长）实测
     * 太快，录屏里只剩 4~6 帧，看着就是「瞬间出现、没有动画」。圆形半径要在屏幕上
     * 走一遍，需要足够时长才看得出它在扩。
     */
    const val ScrimWipeDurationMillis = 420

    /** 遮罩揭幕动画的时长，和药丸位移同频。 */
    val scrimWipe: AnimationSpec<Float> =
        tween(ScrimWipeDurationMillis, easing = FastOutSlowInEasing)

    /**
     * 揭幕圆周的羽化带宽 ÷ 屏高。
     *
     * 圆周不是硬边，而是「遮罩色 → 全透明」的一段过渡带，宽度按屏高等比，换不同分辨率
     * 的观感一致。
     */
    const val ScrimFeatherFraction = 0.10f

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
 * Miuix 搜索输入框，行为与视觉照搬 `top.yukonga.miuix.kmp.basic.InputField`，但**自己持有
 * [androidx.compose.ui.text.input.TextFieldValue]**。
 *
 * 为什么不直接用 Miuix 的 `InputField`：它只暴露 `query: String` 重载，内部交给
 * `BasicTextField(value: String, …)`。那个重载每次重组都会用 `TextFieldValue(query)`
 * 重建一个新值，默认 `selection` 在末尾、**合成区为 null** —— 中文输入法打字时 IME 侧
 * 一直有拼音合成区，每敲一个字母就被冲掉一次，于是拼音和已上屏的字一起进框、光标被复位
 * 到开头（用户实测反馈）。Miuix 的 `TextField` 有 `TextFieldValue` 重载能避开，但
 * `InputField` 没有，只能在这里自己接 `BasicTextField`。
 *
 * 外观（胶囊底色、前置放大镜、label 充当 placeholder、末尾清除键）全部按 Miuix 原版的
 * 度量复刻，所以视觉上与原版一致。
 */
@Composable
private fun KedgeMiuixSearchInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val currentOnQueryChange by rememberUpdatedState(onQueryChange)
    val currentOnSearch by rememberUpdatedState(onSearch)
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

    // 关键：持有 TextFieldValue，IME 的合成区与光标才不会在重组里被丢掉。
    val textFieldValue = rememberKedgeTextFieldValue(query)

    val leadingIcon: @Composable () -> Unit = {
        Icon(
            modifier = Modifier.padding(
                start = MiuixSearchBarDefaults.LeadingIconStartPadding,
                end = MiuixSearchBarDefaults.LeadingIconEndPadding,
            ),
            imageVector = Icons.Rounded.Search,
            tint = MiuixTheme.colorScheme.onSurfaceContainerHigh,
            contentDescription = "Search",
        )
    }

    val actualTrailingIcon: @Composable () -> Unit = {
        trailingIcon?.invoke() ?: run {
            AnimatedVisibility(
                visible = textFieldValue.value.text.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier.padding(
                        start = MiuixSearchBarDefaults.TrailingIconStartPadding,
                        end = MiuixSearchBarDefaults.TrailingIconEndPadding,
                    ),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { textFieldValue.value = androidx.compose.ui.text.input.TextFieldValue(""); currentOnQueryChange("") },
                        imageVector = Icons.Rounded.Close,
                        tint = MiuixTheme.colorScheme.onSurfaceContainerHighest,
                        contentDescription = "Clear",
                    )
                }
            }
        }
    }

    BasicTextField(
        value = textFieldValue.value,
        onValueChange = {
            textFieldValue.value = it
            currentOnQueryChange(it.text)
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) currentOnExpandedChange(true) }
            .semantics {
                onClick {
                    focusRequester.requestFocus()
                    true
                }
            },
        enabled = enabled,
        singleLine = true,
        textStyle = MiuixTheme.textStyles.main.copy(fontWeight = FontWeight.Medium),
        cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { currentOnSearch(textFieldValue.value.text) },
        ),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.background(
                    color = MiuixTheme.colorScheme.surfaceContainerHigh,
                    shape = CircleShape,
                ),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    leadingIcon()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = MiuixSearchBarDefaults.InputFieldMinHeight),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        // label 充当 placeholder：有字了就让位给输入内容（与 Miuix 同款）。
                        if (textFieldValue.value.text.isEmpty()) {
                            Text(
                                text = label,
                                style = MiuixTheme.textStyles.main.copy(
                                    fontSize = MiuixSearchBarDefaults.InputFieldFontSize,
                                    fontWeight = FontWeight.Medium,
                                ),
                                color = MiuixTheme.colorScheme.onSurfaceContainerHigh,
                            )
                        }
                        // 不能给 fillMaxSize()：那会把父 Box 的可用高度吃满、外层 Row 被撑成
                        // 整屏高，结果真实输入框（光标）贴到顶部、而上面那个 placeholder
                        // 因为 CenterStart 对齐被顶到屏幕中间，看着就像「搜索框占满全屏」。
                        // Miuix 原版这里也是不撑高度的普通 Box。
                        Box { innerTextField() }
                    }
                    actualTrailingIcon()
                }
            }
        },
    )

    // 与 Miuix 原版一致：展开时主动要焦点；收起时清掉查询并放掉焦点。
    LaunchedEffect(expanded) {
        if (expanded) {
            focusRequester.requestFocus()
        } else {
            if (textFieldValue.value.text.isNotEmpty()) {
                currentOnQueryChange("")
            }
            focusManager.clearFocus()
        }
    }
}

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

    /**
     * 折叠药丸左边缘在窗口内的 x，也就是它实际的左右留白，由 `onGloballyPositioned` 量。
     *
     * 展开态的真实输入框画在根节点的 overlay 层里，拿不到调用点给 `KedgeSearchBar` 的
     * 那层 `padding(horizontal = …)`。所以这里把折叠态量出来的 inset 喂给它，展开和折叠
     * 的左右留白就必然一致 —— 不管各页面调用时写的是 8dp 还是 16dp。
     */
    var collapsedBarInset: Dp by mutableStateOf(0.dp)

    fun expand() {
        if (phase == SearchPhase.Collapsed) phase = SearchPhase.Expanding
    }

    fun collapse() {
        if (phase == SearchPhase.Expanded || phase == SearchPhase.Expanding) {
            phase = SearchPhase.Collapsing
        }
    }

    /**
     * 预测性返回手势是否正在驱动收起。
     *
     * 为 true 时相位机**不跑自己的补间**：半径与药丸位移全部由
     * `PredictiveBackHandler` 逐帧 `snapTo` 手指进度，收尾（提交跑完 / 取消弹回）
     * 也归它管。相位仍然照常切到 [SearchPhase.Collapsing]，这样正文换回、结果区撤走
     * 的时机和补间路径完全一致。
     */
    var predictive by mutableStateOf(false)
        private set

    /** 手势开始：接管收起，返回 `false` 表示已经折叠、没什么可接管的。 */
    fun beginPredictiveCollapse(): Boolean {
        if (phase == SearchPhase.Collapsed) return false
        predictive = true
        if (phase == SearchPhase.Expanded || phase == SearchPhase.Expanding) {
            phase = SearchPhase.Collapsing
        }
        return true
    }

    /** 手势提交且剩余补间跑完 → 真正折叠。 */
    fun onPredictiveCollapseCommitted() {
        predictive = false
        onCollapseComplete()
    }

    /** 手势取消且弹回补间跑完 → 退回展开态。 */
    fun onPredictiveCollapseCancelled() {
        predictive = false
        if (phase == SearchPhase.Collapsing) phase = SearchPhase.Expanded
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
 * @param onActiveChange 展开态变化回调。`true` 在展开擦除**走完**后才回调，`false` 在收起
 *   擦除**开始**时回调 —— 两个时机都让调用方的 `if (!active) { 页面正文 }` 跟着遮罩边界
 *   走：正文要一直留在场上让遮罩去盖，收起时也要从第一帧起就在遮罩底下等着被露出来。
 * @param expandedContent 全屏结果区。
 * @param cancelLabel 取消键文案，**只在 Miuix 分支生效**（MD3 沿用 KSU 的做法：展开后
 *   leadingIcon 变成返回箭头，没有文字取消键）。Kedge 是库、拿不到宿主的 `R`，所以由调用方
 *   传入；传 `null` 时退回系统文案 `android.R.string.cancel`。
 * @param showResultsWhenEmpty 关键词为空时是否仍然展示 [expandedContent]。默认 `false`
 *   ——「不输入就先不展示」，免得一展开就摊开一整份没过滤的列表。只有当那份列表本身就是
 *   主 UI（表情选择器、模型选择器）才该传 `true`。
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
    showResultsWhenEmpty: Boolean = false,
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

    // Miuix 下外层不留白：折叠药丸的位置完全由调用点给的 modifier 决定，展开态那边
    // 量同一个 inset。MD3 分支不经过这个 overlay，保持 16dp。
    val horizontalPadding = when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> 0.dp
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
                showResultsWhenEmpty = showResultsWhenEmpty,
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
                showResultsWhenEmpty = showResultsWhenEmpty,
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
    showResultsWhenEmpty: Boolean,
) {
    val density = LocalDensity.current
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val overlayState = LocalKedgeSearchOverlayState.current
    val phaseState = remember { MiuixSearchPhaseState() }
    val currentOnActiveChange by rememberUpdatedState(onActiveChange)
    // `active` 必须活读，不能直接闭包进 overlayContent：那个 lambda 只在 phase 变化时
    // 重新 attach（见下方 DisposableEffect），外面单独把 active 置 false 的话，挂在槽里的
    // 还是旧闭包里的那个 true，结果区会赖着不走。
    val activeState by rememberUpdatedState(active)
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
                // 展开态输入框的左右留白跟着折叠药丸走，见 collapsedBarInset。
                phaseState.collapsedBarInset =
                    with(density) { coordinates.positionInWindow().x.toDp() }
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
        KedgeMiuixSearchInputField(
            query = "",
            onQueryChange = {},
            onSearch = {},
            expanded = false,
            onExpandedChange = {},
            label = placeholder,
            enabled = false,
            trailingIcon = null,
            // 满宽即可，不再自带左右内边距：折叠态的留白完全由调用点传给 KedgeSearchBar 的
            // modifier 决定（各页面不一样，有 8dp 也有 16dp），药丸若自己再叠一层就会
            // 比下方卡片缩进得更多。展开态那边用 phaseState.collapsedBarInset 量回同一个值。
            modifier = Modifier.fillMaxWidth(),
        )
    }

    // 外部把 active 置 true 走同一套展开流程。
    LaunchedEffect(active) {
        if (active) phaseState.expand() else phaseState.collapse()
    }

    val barTopPadding = remember { Animatable(0.dp, Dp.VectorConverter) }
    // 遮罩揭幕进度：**0 = 无遮罩，1 = 整屏遮罩**。单一含义，方向由动画的目标值决定
    // （展开 0→1，收起 1→0），不要按相位反向解读 —— 之前那么写导致收起时终点停在 1，
    // 下一次展开 `animateTo(1f)` 起点已是 1，动画被整体跳过，遮罩瞬间铺满。
    val scrimProgress = remember { Animatable(0f) }

    // 系统返回收起，等价于 KSU `SuperSearchBar` 的 `NavigationBackHandler(isBackEnabled = expanded)`。
    // 展开途中也要拦：那时结果区还没挂上、取消键还没 enable，放行的话返回会直接走导航把整页弹走，
    // 搜索框只是被 `DisposableEffect.onDispose` 顺手摘掉，视觉上像「返回没反应又换了个页」。
    //
    // 用 PredictiveBackHandler 而不是 BackHandler：手势进度 0→1 直接映射成收起进度 0→1 ——
    // 圆形遮罩跟着手指往回收，药丸跟着手指往下滑；松手提交才把剩下的补间跑完，中途划回去
    // 则原路弹回展开态。相位仍然在手势开始时就切到 Collapsing，正文换回 / 结果区撤走的
    // 时机和补间路径完全一致。
    PredictiveBackHandler(enabled = !phaseState.isCollapsed) { backEvents ->
        if (!phaseState.beginPredictiveCollapse()) return@PredictiveBackHandler
        // 手势可能在补间跑一半时开始，从当前位置接着走。
        val fromBar = barTopPadding.value
        val toBar = phaseState.collapsedBarY
        val expandedBarTop = statusBarTop + KedgeSearchMotion.ExpandedBarTopInset
        try {
            backEvents.collect { event ->
                val progress = event.progress.coerceIn(0f, 1f)
                scrimProgress.snapTo(1f - progress)
                barTopPadding.snapTo(fromBar + (toBar - fromBar) * progress)
            }
            // 手势提交：从当前进度把剩下那段补完。
            coroutineScope {
                val wipe = launch { scrimProgress.animateTo(0f, KedgeSearchMotion.scrimWipe) }
                barTopPadding.animateTo(toBar, KedgeSearchMotion.bar)
                wipe.join()
            }
            phaseState.onPredictiveCollapseCommitted()
            // KSU 在 COLLAPSING -> COLLAPSED 时清空关键词。
            latestOnValueChange("")
        } catch (e: CancellationException) {
            // 手势取消：原路弹回展开态。
            coroutineScope {
                val wipe = launch { scrimProgress.animateTo(1f, KedgeSearchMotion.scrimWipe) }
                barTopPadding.animateTo(expandedBarTop, KedgeSearchMotion.bar)
                wipe.join()
            }
            phaseState.onPredictiveCollapseCancelled()
            throw e
        }
    }

    LaunchedEffect(phaseState.phase) {
        when (phaseState.phase) {
            SearchPhase.Expanding -> {
                // 起点钉在折叠药丸的实测 y（KSU 的 `max(offsetY, 0.dp)`），再滑到状态栏下方。
                barTopPadding.snapTo(phaseState.collapsedBarY)
                // 揭幕与位移同时起跑，**两者都走完**再进 Expanded。
                // 这里必须 join：早先用 `launch { 揭幕 }` + `位移.animateTo()`，位移 300ms
                // 走完就调 onExpandComplete()，phase 的 LaunchedEffect key 一变，整棵协程树
                // 被取消，420ms 的揭幕在 300ms 处被截断 —— 表现就是「动画没播完/没播」。
                coroutineScope {
                    val wipe = launch {
                        scrimProgress.snapTo(0f)
                        scrimProgress.animateTo(1f, KedgeSearchMotion.scrimWipe)
                    }
                    barTopPadding.animateTo(
                        statusBarTop + KedgeSearchMotion.ExpandedBarTopInset,
                        KedgeSearchMotion.bar,
                    )
                    wipe.join()
                }
                phaseState.onExpandComplete()
            }

            SearchPhase.Collapsing -> {
                // 预测性返回手势接管时进度归手指，这里让位 —— 收尾（提交跑完 / 取消弹回）
                // 由 PredictiveBackHandler 自己负责，见上面那段。
                if (!phaseState.predictive) {
                    // 收起：圆半径**缩回**同一个圆心（1→0），内容从外向内重新可见。同样要
                    // join，否则揭幕会被相位切换取消掉。
                    coroutineScope {
                        val wipe = launch { scrimProgress.animateTo(0f, KedgeSearchMotion.scrimWipe) }
                        barTopPadding.animateTo(phaseState.collapsedBarY, KedgeSearchMotion.bar)
                        wipe.join()
                    }
                    phaseState.onCollapseComplete()
                    // KSU 在 COLLAPSING -> COLLAPSED 时清空关键词。
                    latestOnValueChange("")
                }
            }

            SearchPhase.Collapsed -> {
                // 回到折叠态：药丸停在折叠位、遮罩完全撤掉。等价于 KSU 那个
                // `animateDpAsState(target = max(offsetY, 0.dp))` 持续跟随。
                barTopPadding.snapTo(phaseState.collapsedBarY)
                scrimProgress.snapTo(0f)
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
            // 擦除进度在**组合阶段**读（而不是只在 drawWithContent 里读），这样每帧都会
            // 让整层重组、重建 draw lambda，红画就不依赖 draw 阶段的失效机制。
            val wipeProgress = scrimProgress.value
            // 遮罩吃掉「落在空白处」的触摸。展开/收起途中不接的话，触摸会一路穿到下面
            // 页面的控件上 —— 典型表现是点取消后那 300ms 内取消键已 `enabled = false` 且在
            // 做退出动画，右上角没有子节点吃事件，于是同位置的页面 IconButton 替它挨了一下。
            // 折叠态必须反过来完全不接，药丸还要靠下面页面的点击展开。
            val scrimTouch = if (phase == SearchPhase.Collapsed) {
                Modifier
            } else {
                Modifier.pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent().changes.forEach { it.consume() }
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                // 遮罩单独一层、压在内容**下面**：命中测试自上而下，先命中上面的搜索框与
                // 结果区，它们都放过才落到这里被吃掉，所以既不会误伤结果区里的滚动/点击，
                // 也不会漏到页面正文。
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(scrimTouch)
                        .drawWithContent {
                            // 圆形揭幕遮罩（见 [KedgeSearchMotion.scrimWipe]）：遮罩是一个
                            // 从**屏幕顶边、水平居中**向外扩散的圆，圆内是遮罩色、圆外露出
                            // 页面正文，边界上带一段羽化带。展开半径 0 → 盖满整屏，收起原路
                            // 缩回同一个圆心 —— 同一个 progress 正反都成立。
                            val progress = wipeProgress
                            if (progress <= 0f) return@drawWithContent
                            val feather = size.height * KedgeSearchMotion.ScrimFeatherFraction
                            val center = Offset(size.width / 2f, 0f)
                            // 半径至少要够到最远的那个角才是整屏，再叠一段羽化带宽，让
                            // 展开到底时羽化边已经推出屏外，四角不会留一圈半透明渐变。
                            val maxRadius = maxOf(
                                hypot(center.x, center.y),
                                hypot(size.width - center.x, center.y),
                                hypot(center.x, size.height - center.y),
                                hypot(size.width - center.x, size.height - center.y),
                            )
                            val radius = progress * (maxRadius + feather)
                            if (radius <= 0f) return@drawWithContent
                            // 羽化带骑在圆周上：inner 以内实心，inner → radius 渐隐到透明。
                            val innerRatio = ((radius - feather) / radius).coerceIn(0f, 1f)
                            val base = surface
                            drawRect(
                                brush = Brush.radialGradient(
                                    *arrayOf(
                                        0f to base,
                                        innerRatio to base,
                                        1f to Color.Transparent,
                                    ),
                                    center = center,
                                    radius = radius,
                                ),
                            )
                        },
                )
                Column(modifier = Modifier.fillMaxSize()) {
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
                                KedgeMiuixSearchInputField(
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
                                        // 左右留白量自折叠药丸，保证展开/折叠两态一致，
                                        // 也和下方卡片的缩进对齐。
                                        .padding(
                                            start = phaseState.collapsedBarInset,
                                            end = phaseState.collapsedBarInset,
                                        )
                                        .padding(bottom = KedgeSearchMotion.BarBottomPadding),
                                )
                            }
                        }
                        AnimatedVisibility(
                            // 这里**故意**不加固定宽度：KSU 原版就是让取消键
                            // shrinkHorizontally 收缩、把腾出的宽度交给 weight(1f)
                            // 的输入框，于是收起过程中输入框会长到与折叠药丸同宽，
                            // 末尾交接才无缝。锁死宽度反而会在末尾留下跳变。
                            // KSU 在展开动画开始时就组合取消按钮，让它与搜索框同步从右侧进入。
                            // 只有完全展开后才允许点击，避免动画期间误触发收起。
                            visible = phase == SearchPhase.Expanding || phase == SearchPhase.Expanded,
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
                        // 和调用方的 `if (!searchExpanded) { 页面正文 }` 吃**同一个**布尔量：
                        // 正文出 / 结果进由同一次状态写入驱动，落在同一帧，不会有一帧
                        // 两份列表同时在场（那边的 sharedElement 也只会注册一份）。
                        // `phase == Expanded` 只负责挡掉展开途中 —— 外部一进页就把 active
                        // 置 true 时（SearchPage），结果区也不能在擦除走完之前冒出来。
                        visible = activeState && phase == SearchPhase.Expanded &&
                            (showResultsWhenEmpty || valueState.value.isNotBlank()),
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
        }

        // 挂 / 摘。离开页面（导航走、弹窗关掉）时 onDispose 会把全屏层摘掉，
        // 否则它会带着旧的 phaseState 一直留在根节点上 —— 换页后搜索框还在。
        DisposableEffect(overlayState, phase) {
            overlayState.attach(overlayContent)
            onDispose { overlayState.detach(overlayContent) }
        }
    }

    // 把内部相位翻译成对外的布尔量：
    // - true 拖到擦除**走完**（phase 变 Expanded）才报。之前在 Expanding 就报，调用方
    //   的 `if (!searchExpanded)` 在动画开始的同一帧就把页面正文抽掉了，遮罩擦的是一张
    //   空的、和遮罩同色的页面 —— 表现就是「动画还没到内容那里内容就消失了」。正文留在
    //   场上，遮罩才有东西可盖；等到擦除盖满整屏再换，正文出 / 结果进是无缝的。
    // - false 在收起动画**开始**（phase 变 Collapsing）就报。正文从第一帧起就在遮罩底下，
    //   收起的擦除才是「逐步把正文露出来」，而不是对着一张空白页面回退。
    // 结果区那边吃的是同一个布尔量（见 results 的 `visible`），所以正文出/进和结果区
    // 进/出永远落在同一帧。收起时会有约 200ms 的收尾淡出与正文短暂共存 —— 结果区整段
    // 都在 NoHeroTransition 里（不注册 sharedElement），只有正文那边注册，不会撞 key。
    LaunchedEffect(phaseState.phase) {
        when (phaseState.phase) {
            SearchPhase.Expanded -> currentOnActiveChange?.invoke(true)
            SearchPhase.Collapsing, SearchPhase.Collapsed -> currentOnActiveChange?.invoke(false)
            SearchPhase.Expanding -> Unit
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
    showResultsWhenEmpty: Boolean,
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
            // 同 Miuix 分支：关键词为空时不摊开列表。
            if (showResultsWhenEmpty || value.isNotBlank()) {
                expandedContent()
            }
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
