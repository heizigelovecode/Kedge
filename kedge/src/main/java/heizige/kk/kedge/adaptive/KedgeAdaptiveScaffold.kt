package heizige.kk.kedge.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarDisplayMode
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationRail as MiuixNavigationRail
import top.yukonga.miuix.kmp.basic.NavigationRailItem as MiuixNavigationRailItem
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.basic.rememberNavigationRailState

@Immutable
data class KedgeNavigationItem<T>(
    val key: T,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val enabled: Boolean = true,
    val contentDescription: String? = label,
    val badge: (@Composable () -> Unit)? = null,
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <T> KedgeAdaptiveScaffold(
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    navigationItems: List<KedgeNavigationItem<T>>,
    modifier: Modifier = Modifier,
    expandableMiuixRail: Boolean = true,
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> KedgeMaterialAdaptiveScaffold(
            selectedItem = selectedItem,
            onItemSelected = onItemSelected,
            navigationItems = navigationItems,
            modifier = modifier,
            content = content,
        )

        KedgeStyle.Miuix -> KedgeMiuixAdaptiveScaffold(
            selectedItem = selectedItem,
            onItemSelected = onItemSelected,
            navigationItems = navigationItems,
            modifier = modifier,
            expandableRail = expandableMiuixRail,
            content = content,
        )
    }
}

@Composable
private fun <T> KedgeMaterialAdaptiveScaffold(
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    navigationItems: List<KedgeNavigationItem<T>>,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            navigationItems.forEach { item ->
                val selected = selectedItem == item.key
                item(
                    selected = selected,
                    onClick = { onItemSelected(item.key) },
                    enabled = item.enabled,
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.icon,
                            contentDescription = item.contentDescription,
                        )
                    },
                    label = { Text(item.label) },
                    badge = item.badge,
                )
            }
        },
        content = content,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun <T> KedgeMiuixAdaptiveScaffold(
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    navigationItems: List<KedgeNavigationItem<T>>,
    modifier: Modifier,
    expandableRail: Boolean,
    content: @Composable () -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val useRail = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val railState = if (expandableRail) rememberNavigationRailState() else null

    // The large top app bar lives OUTSIDE this scaffold and already consumes the
    // status bar inset, so only bottom/horizontal insets apply to the content.
    MiuixScaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
        bottomBar = {
            if (!useRail) {
                MiuixNavigationBar(mode = NavigationBarDisplayMode.IconAndText) {
                    navigationItems.forEach { item ->
                        val selected = selectedItem == item.key
                        MiuixNavigationBarItem(
                            selected = selected,
                            onClick = { onItemSelected(item.key) },
                            icon = if (selected) item.selectedIcon else item.icon,
                            label = item.label,
                            enabled = item.enabled,
                            badge = item.badge,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        if (useRail) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                MiuixNavigationRail(
                    state = railState,
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    navigationItems.forEach { item ->
                        val selected = selectedItem == item.key
                        MiuixNavigationRailItem(
                            selected = selected,
                            onClick = { onItemSelected(item.key) },
                            icon = if (selected) item.selectedIcon else item.icon,
                            label = item.label,
                            enabled = item.enabled,
                            badge = item.badge,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .widthIn(min = 0.dp),
                ) {
                    content()
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                content()
            }
        }
    }
}
