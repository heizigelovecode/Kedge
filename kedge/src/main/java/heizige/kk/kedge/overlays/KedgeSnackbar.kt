package heizige.kk.kedge.overlays

import androidx.compose.material3.SnackbarDuration as MdSnackbarDuration
import androidx.compose.material3.SnackbarHost as MdSnackbarHost
import androidx.compose.material3.SnackbarHostState as MdSnackbarHostState
import androidx.compose.material3.SnackbarResult as MdSnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import kotlinx.coroutines.CoroutineScope
import top.yukonga.miuix.kmp.basic.Snackbar as MiuixSnackbar
import top.yukonga.miuix.kmp.basic.SnackbarDuration as MiuixSnackbarDuration
import top.yukonga.miuix.kmp.basic.SnackbarHost as MiuixSnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState as MiuixSnackbarHostState
import top.yukonga.miuix.kmp.basic.SnackbarResult as MiuixSnackbarResult
import kotlinx.coroutines.launch

/**
 * Snackbar 的时长，与 MD3 的 `SnackbarDuration` 对齐。
 *
 * Miuix 的 `SnackbarDuration` 用 `Short`/`Long`/`Indefinite`，语义与 MD3 一致，
 * 所以这里做一层薄封装，调用点只依赖本类型。
 */
@Immutable
enum class KedgeSnackbarDuration {
    Short,
    Long,
    Indefinite,
}

/**
 * Snackbar 的展示结果。
 *
 * MD3 与 Miuix 各有自己的 `SnackbarResult`；这里统一成
 * [Dismissed]/[ActionPerformed] 两个值，调用点不再需要 `import` 具体是哪一个。
 */
@Immutable
enum class KedgeSnackbarResult {
    Dismissed,
    ActionPerformed,
}

/**
 * 双风格 Snackbar 状态，封装 [showSnackbar]。
 *
 * MD3 与 Miuix 的 API 形状本就一致（message + actionLabel + withDismissAction
 * + duration -> result），所以这里只在构造时选对应实现，展示逻辑由调用点共用。
 *
 * 用法与 MD3 的 `SnackbarHostState` 一致，`remember` 时不需要传参。
 */
@Stable
class KedgeSnackbarHostState internal constructor(
    private val style: KedgeStyle,
) {
    internal val mdState = MdSnackbarHostState()
    internal val miuixState = MiuixSnackbarHostState()

    /**
     * 展示一条 Snackbar，返回用户是否点了 action。
     *
     * @param message 正文
     * @param actionLabel action 文案；`null` 表示没有 action
     * @param withDismissAction 是否显示关闭按钮
     * @param duration 停留时长，默认对齐 MD3 的 [MdSnackbarDuration.Short]
     */
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: KedgeSnackbarDuration = KedgeSnackbarDuration.Short,
    ): KedgeSnackbarResult = when (style) {
        KedgeStyle.Miuix -> when (
            miuixState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                withDismissAction = withDismissAction,
                duration = when (duration) {
                    KedgeSnackbarDuration.Short -> MiuixSnackbarDuration.Short
                    KedgeSnackbarDuration.Long -> MiuixSnackbarDuration.Long
                    KedgeSnackbarDuration.Indefinite -> MiuixSnackbarDuration.Indefinite
                },
            )
        ) {
            MiuixSnackbarResult.ActionPerformed -> KedgeSnackbarResult.ActionPerformed
            else -> KedgeSnackbarResult.Dismissed
        }

        KedgeStyle.MD3Exp -> when (
            mdState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                withDismissAction = withDismissAction,
                duration = when (duration) {
                    KedgeSnackbarDuration.Short -> MdSnackbarDuration.Short
                    KedgeSnackbarDuration.Long -> MdSnackbarDuration.Long
                    KedgeSnackbarDuration.Indefinite -> MdSnackbarDuration.Indefinite
                },
            )
        ) {
            MdSnackbarResult.ActionPerformed -> KedgeSnackbarResult.ActionPerformed
            else -> KedgeSnackbarResult.Dismissed
        }
    }
}

/** 记住一个跟随当前风格的 [KedgeSnackbarHostState]，风格切换时自动换实现。 */
@Composable
fun rememberKedgeSnackbarHostState(): KedgeSnackbarHostState {
    val style = LocalKedgeStyle.current
    return remember(style) { KedgeSnackbarHostState(style) }
}

/**
 * Snackbar 宿主，**按风格分流**：Miuix 下用 Miuix 的 `SnackbarHost`
 * （Miuix 的胶囊形、圆角与字阶），MD3Exp 下保持原生。
 *
 * 用途：需要 action 的轻反馈，例如「已删除 —— 撤销」。这类反馈不能用 Toast，
 * 因为 Toast 不支持 action 按钮。
 */
@Composable
fun KedgeSnackbarHost(
    state: KedgeSnackbarHostState,
    modifier: Modifier = Modifier,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> MiuixSnackbarHost(
            state = state.miuixState,
            modifier = modifier,
        ) { data ->
            // Miuix 的 Snackbar 自己按 SnackbarVisuals.withDismissAction 决定
            // 是否画关闭按钮，这里不用再传。
            MiuixSnackbar(data = data)
        }

        KedgeStyle.MD3Exp -> MdSnackbarHost(
            hostState = state.mdState,
            modifier = modifier,
        )
    }
}