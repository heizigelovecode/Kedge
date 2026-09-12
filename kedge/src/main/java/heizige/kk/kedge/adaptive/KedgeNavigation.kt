package heizige.kk.kedge.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TwoRowsTopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.components.KedgeIconButton
import heizige.kk.kedge.components.KedgeIconButtonVariant
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarDisplayMode
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationRail as MiuixNavigationRail
import top.yukonga.miuix.kmp.basic.NavigationRailItem as MiuixNavigationRailItem
import top.yukonga.miuix.kmp.basic.SmallTopAppBar as MiuixSmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TopAppBar as MiuixTopAppBar
import top.yukonga.miuix.kmp.basic.rememberNavigationRailState
import top.yukonga.miuix.kmp.theme.LocalContentColor as MiuixLocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun KedgeTopBarBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    KedgeIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = KedgeIconButtonVariant.Standard,
    ) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KedgeTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    centered: Boolean = false,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    titleContent: (@Composable () -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val actualTitle = titleContent ?: { Text(title) }
            if (centered) {
                CenterAlignedTopAppBar(
                    title = actualTitle,
                    modifier = modifier,
                    navigationIcon = navigationIcon,
                    actions = actions,
                )
            } else {
                TopAppBar(
                    title = actualTitle,
                    modifier = modifier,
                    navigationIcon = navigationIcon,
                    actions = actions,
                )
            }
        }

        KedgeStyle.Miuix -> MiuixTopBarContentColor {
            MiuixSmallTopAppBar(
                title = title,
                modifier = modifier,
                subtitle = subtitle.orEmpty(),
                navigationIcon = navigationIcon,
                actions = actions,
            )
        }
    }
}

/**
 * Miuix 顶栏不提供 Material 的 `LocalContentColor`，业务传入的 Material `Icon`
 * 会退回默认黑色，在暗色背景下不可见；这里统一注入两套内容色。
 */
@Composable
private fun MiuixTopBarContentColor(content: @Composable () -> Unit) {
    val color = MiuixTheme.colorScheme.onSurface
    CompositionLocalProvider(
        LocalContentColor provides color,
        MiuixLocalContentColor provides color,
        content = content,
    )
}

/**
 * Collapsing app-bar behavior unified across Kedge styles: Material3
 * `exitUntilCollapsed` on MD3Exp, miuix's exit-until-collapsed behavior on Miuix.
 * Attach [nestedScrollConnection] to the scrollable content that drives the bar.
 */
class KedgeScrollBehavior internal constructor(
    val nestedScrollConnection: NestedScrollConnection,
    internal val material: TopAppBarScrollBehavior?,
    internal val miuix: top.yukonga.miuix.kmp.basic.ScrollBehavior?,
)

/** Exit-until-collapsed behavior matching the active [KedgeStyle]. */
@Composable
fun rememberKedgeExitUntilCollapsedScrollBehavior(): KedgeScrollBehavior =
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
            KedgeScrollBehavior(behavior.nestedScrollConnection, material = behavior, miuix = null)
        }

        KedgeStyle.Miuix -> {
            val behavior = MiuixScrollBehavior()
            KedgeScrollBehavior(behavior.nestedScrollConnection, material = null, miuix = behavior)
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KedgeLargeTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    titleContent: (@Composable () -> Unit)? = null,
    scrollBehavior: KedgeScrollBehavior? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> LargeFlexibleTopAppBar(
            title = titleContent ?: { Text(title) },
            modifier = modifier,
            subtitle = if (subtitle.isNullOrBlank()) null else {
                { Text(subtitle) }
            },
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior?.material,
        )

        KedgeStyle.Miuix -> MiuixTopBarContentColor {
            MiuixTopAppBar(
                title = title,
                modifier = modifier,
                largeTitle = title,
                subtitle = subtitle.orEmpty(),
                navigationIcon = navigationIcon,
                actions = actions,
                scrollBehavior = scrollBehavior?.miuix,
            )
        }
    }
}

/**
 * Two-row top app bar with an optional subtitle line under the title.
 *
 * MD3Exp maps to Material3 [TwoRowsTopAppBar] with configurable collapsed/expanded
 * heights; Miuix falls back to the large Miuix top app bar, which ignores the height
 * overrides. Pass a non-default [windowInsets] (e.g. `WindowInsets(0.dp)`) to drop the
 * status-bar padding on pages that already consume the system bars themselves — this is
 * what removes the phantom gap above tool-style pages like a terminal instance list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KedgeTwoRowsTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    titleContent: (@Composable () -> Unit)? = null,
    collapsedHeight: Dp = Dp.Unspecified,
    expandedHeight: Dp = Dp.Unspecified,
    windowInsets: WindowInsets? = null,
    scrollBehavior: KedgeScrollBehavior? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> TwoRowsTopAppBar(
            title = { titleContent?.invoke() ?: Text(title) },
            modifier = modifier,
            subtitle = if (subtitle.isNullOrBlank()) null else {
                { Text(subtitle) }
            },
            navigationIcon = navigationIcon,
            actions = actions,
            collapsedHeight = collapsedHeight,
            expandedHeight = expandedHeight,
            windowInsets = windowInsets ?: TopAppBarDefaults.windowInsets,
            scrollBehavior = scrollBehavior?.material,
        )

        KedgeStyle.Miuix -> MiuixTopBarContentColor {
            MiuixTopAppBar(
                title = title,
                modifier = modifier,
                largeTitle = title,
                subtitle = subtitle.orEmpty(),
                navigationIcon = navigationIcon,
                actions = actions,
                scrollBehavior = scrollBehavior?.miuix,
            )
        }
    }
}

@Composable
fun <T> KedgeBottomBar(
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    navigationItems: List<KedgeNavigationItem<T>>,
    modifier: Modifier = Modifier,
) {
    KedgeNavigationBar(
        selectedItem = selectedItem,
        onItemSelected = onItemSelected,
        navigationItems = navigationItems,
        modifier = modifier,
    )
}

@Composable
fun <T> KedgeNavigationBar(
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    navigationItems: List<KedgeNavigationItem<T>>,
    modifier: Modifier = Modifier,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> NavigationBar(modifier = modifier) {
            navigationItems.forEach { item ->
                val selected = selectedItem == item.key
                NavigationBarItem(
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
                )
            }
        }

        KedgeStyle.Miuix -> MiuixNavigationBar(
            modifier = modifier,
            mode = NavigationBarDisplayMode.IconAndText,
        ) {
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
}

@Composable
fun <T> KedgeNavigationRail(
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    navigationItems: List<KedgeNavigationItem<T>>,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    header: @Composable (() -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> NavigationRail(
            modifier = modifier,
            header = header?.let { { it() } },
        ) {
            navigationItems.forEach { item ->
                val selected = selectedItem == item.key
                NavigationRailItem(
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
                )
            }
        }

        KedgeStyle.Miuix -> {
            val state = if (expanded) rememberNavigationRailState() else null
            MiuixNavigationRail(
                modifier = modifier,
                state = state,
                header = header?.let { { it() } },
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
        }
    }
}

data class KedgeBreadcrumbItem(
    val label: String,
    val icon: ImageVector? = null,
    val onClick: (() -> Unit)? = null,
)

@Composable
fun KedgeBreadcrumbBar(
    items: List<KedgeBreadcrumbItem>,
    modifier: Modifier = Modifier,
) {
    val isMiuix = LocalKedgeStyle.current == KedgeStyle.Miuix
    val containerColor = if (isMiuix) MiuixTheme.colorScheme.secondaryVariant else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isMiuix) MiuixTheme.colorScheme.onSecondaryVariant else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items.forEachIndexed { index, item ->
            if (index > 0) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp),
                )
            }
            Row(
                modifier = Modifier
                    .clickable(enabled = item.onClick != null) { item.onClick?.invoke() }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (item.icon != null) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                if (isMiuix) {
                    MiuixText(text = item.label, color = contentColor)
                } else {
                    Text(text = item.label, color = contentColor, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
