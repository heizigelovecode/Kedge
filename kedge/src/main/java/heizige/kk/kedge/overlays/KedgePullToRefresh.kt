package heizige.kk.kedge.overlays

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.pulltorefresh.PullToRefreshBox as MdPullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.PullToRefresh as MiuixPullToRefresh
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState as miuixRememberPullToRefreshState

/**
 * 下拉刷新容器，**按风格分流**。
 *
 * MD3Exp 用 MD3 的 `PullToRefreshBox`；Miuix 用 Miuix 的 `PullToRefresh`
 * （Miuix 自带的拉动容器，形态与 MD3 不一样）。
 *
 * 两套 API 都由 `isRefreshing` / `onRefresh` 驱动、内部各自 `remember` 状态，
 * 所以调用点不用关心风格差异。
 */
@Composable
fun KedgePullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdPullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = modifier,
            state = rememberPullToRefreshState(),
        ) {
            content()
        }

        KedgeStyle.Miuix -> MiuixPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = modifier,
            pullToRefreshState = miuixRememberPullToRefreshState(),
            content = { content() },
        )
    }
}
