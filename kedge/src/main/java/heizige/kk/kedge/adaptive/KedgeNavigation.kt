package heizige.kk.kedge.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material3.TopAppBarColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import top.yukonga.miuix.kmp.basic.TopAppBarDefaults as MiuixTopBarDefaults
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.SmallTopAppBar as MiuixSmallTopAppBar
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
    scrollBehavior: top.yukonga.miuix.kmp.basic.ScrollBehavior? = null,
    /**
     * MD3 配色，**只在 MD3Exp 分支生效**。收下它是为了让挂在毛玻璃上的调用点
     * （聊天抽屉的顶栏）能把 containerColor 设成 Transparent，让毛玻璃透出来；
     * Miuix 分支的底色由 `KedgeMiuixCustomTitleBar` 按 backdrop 自己决定。
     */
    colors: TopAppBarColors? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val actualTitle = titleContent ?: { Text(title) }
            val actualColors = colors ?: TopAppBarDefaults.topAppBarColors()
            if (centered) {
                CenterAlignedTopAppBar(
                    title = actualTitle,
                    modifier = modifier,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = actualColors,
                )
            } else {
                TopAppBar(
                    title = actualTitle,
                    modifier = modifier,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = actualColors,
                )
            }
        }

        KedgeStyle.Miuix -> {
            // 与大标题栏一致：页面级 backdrop 存在时底色透明，露出内容产生毛玻璃。
            val backdrop = LocalKedgePageBackdrop.current
            MiuixTopBarContentColor {
                if (titleContent == null) {
                    // 无副标题：SmallTopAppBar 即可，标题就是普通单行标题。
                    KedgeBlurredBar(backdrop = backdrop) {
                        MiuixSmallTopAppBar(
                            title = title,
                            modifier = modifier,
                            color = if (backdrop != null) {
                                Color.Transparent
                            } else {
                                MiuixTheme.colorScheme.surface
                            },
                            navigationIcon = navigationIcon,
                            actions = actions,
                            scrollBehavior = scrollBehavior,
                        )
                    }
                } else if (titleContent != null) {
                    // Miuix 的 SmallTopAppBar 标题只收字符串，塞不进自定义标题槽。
                    // 调用方（如 ChatPage 的搜索 morph）传了 titleContent 就自绘栏，
                    // 度量沿用 Miuix SmallTopAppBar，保证与字符串标题版观感一致。
                    KedgeMiuixCustomTitleBar(
                        modifier = modifier,
                        titleContent = titleContent,
                        navigationIcon = navigationIcon,
                        actions = actions,
                    )
                } else {
                    KedgeBlurredBar(backdrop = backdrop) {
                        MiuixSmallTopAppBar(
                            title = title,
                            modifier = modifier,
                            color = if (backdrop != null) {
                                Color.Transparent
                            } else {
                                MiuixTheme.colorScheme.surface
                            },
                            subtitle = subtitle.orEmpty(),
                            navigationIcon = navigationIcon,
                            actions = actions,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Miuix 风格、支持自定义标题槽的顶栏。
 *
 * Miuix 的 `SmallTopAppBar` / `TopAppBar` 标题参数是 `String`，无法承载
 * 「标题 morph 成搜索框」这类自定义内容，因此调用方传了 `titleContent` 时
 * 用这个自绘版本。度量（状态栏 inset、50dp 中心高、16dp 图标内边距、26dp 标题内边距）
 * 与 Miuix `SmallTopAppBar` 保持一致。
 */
@Composable
private fun KedgeMiuixCustomTitleBar(
    modifier: Modifier = Modifier,
    titleContent: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    large: Boolean = false,
) {
    val backdrop = LocalKedgePageBackdrop.current
    KedgeBlurredBar(backdrop = backdrop) {
        MiuixTopBarContentColor {
            // 底色必须画在 Row 本身：早先画在外层 Box 上，Box 的 inset padding
            // 会把 Row 整体下推，两者高度不同步就会在标题区露出一条与页面背景
            // 不同色的边（视觉上像「顶栏和背景配色反了」）。
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        if (backdrop != null) {
                            Color.Transparent
                        } else {
                            MiuixTheme.colorScheme.surface
                        },
                    )
                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                    // 高度跟随内容：标题可能一行也可能两行，不能写死。
                    // 大标题栏给足最小高度（对齐 Miuix TopAppBar 的折叠高度）。
                    .heightIn(
                        min = if (large) {
                            MiuixTopBarDefaults.CollapsedHeight + 48.dp
                        } else {
                            MiuixTopBarDefaults.SmallTopAppBarCenterHeight
                        },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.padding(start = MiuixTopBarDefaults.NavigationIconPadding),
                ) {
                    navigationIcon()
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = MiuixTopBarDefaults.TitlePadding),
                ) {
                    titleContent()
                }
                Row(
                    modifier = Modifier.padding(end = MiuixTopBarDefaults.ActionIconPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions,
                )
            }
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

/** [KedgeScrollBehavior] 的 Miuix 侧行为，供 KhatKit 自绘顶栏时消费。 */
val KedgeScrollBehavior.miuixScrollBehavior: top.yukonga.miuix.kmp.basic.ScrollBehavior?
    get() = miuix

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
    translucentTopBar: Boolean = true,
    backdrop: top.yukonga.miuix.kmp.blur.LayerBackdrop? = LocalKedgePageBackdrop.current,
    /**
     * 标题透明度（0=隐藏）。Miuix 分支同时作用于折叠后的小标题与大标题；
     * 对应 KernelSU `AboutMiuix.kt` 里 `titleColor` 随滚动淡入的效果。
     */
    titleAlpha: Float = 1f,
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
            // 大标题折叠要用「本屏那个」Miuix 行为，否则顶栏观察的是一个没人喂
            // 滚动的实例，大标题永远不收缩。优先取显式传入的，回退到
            // KedgePageScaffold 下发的 LocalKedgePageScrollBehavior。
            val effectiveScroll = scrollBehavior ?: LocalKedgePageScrollBehavior.current
            // 毛玻璃：backdrop 必须由「不含本顶栏」的 KedgeBlurSurface 提供并显式传入。
            // 模糊生效时底色必须透明，否则模糊无从透出。
            KedgeBlurredBar(backdrop = backdrop) {
              if (titleContent != null) {
                // Miuix 的 TopAppBar 标题只收字符串，塞不进自定义标题槽
                // （ChatPage 的标题 morph 成搜索框就走这条路）。
                KedgeMiuixCustomTitleBar(
                    modifier = modifier,
                    titleContent = titleContent,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    large = true,
                )
              } else MiuixTopAppBar(
                    title = title,
                    modifier = modifier,
                    // 无 backdrop 时与页面背景同色（KernelSU 的顶栏/背景不分色），
                    // 不做半透明，否则顶栏会比背景亮一档，看着像配色反了。
                    color = if (backdrop != null) {
                        Color.Transparent
                    } else {
                        MiuixTheme.colorScheme.surface
                    },
                    titleColor = MiuixTheme.colorScheme.onSurface.copy(alpha = titleAlpha.coerceIn(0f, 1f)),
                    largeTitle = title,
                    largeTitleColor = MiuixTheme.colorScheme.onSurface.copy(alpha = titleAlpha.coerceIn(0f, 1f)),
                    subtitle = subtitle.orEmpty(),
                    navigationIcon = navigationIcon,
                    actions = actions,
                    scrollBehavior = effectiveScroll?.miuix,
                )
            }
        }
    }
}

/**
 * 小标题栏（无折叠大标题），照搬 KernelSU `AboutMiuix.kt` 用的 `SmallTopAppBar`。
 *
 * 与 [KedgeLargeTopAppBar] 的区别：没有 `largeTitle`，标题不会折叠成两行，
 * 也不会因为 default `largeTitle = title` 而在两个位置同时出现。
 * 关于页顶栏用它，标题再配合 [titleAlpha] 随滚动淡入。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KedgeSmallTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    titleAlpha: Float = 1f,
    /**
     * 顶栏底色透明度；非 null 时跳过毛玻璃，直接用带 alpha 的底色。
     * 对应 lyricon `AboutScreen.kt:118`。
     */
    barColorAlpha: Float? = null,
    scrollBehavior: KedgeScrollBehavior? = null,
    backdrop: top.yukonga.miuix.kmp.blur.LayerBackdrop? = LocalKedgePageBackdrop.current,
) {
    val alpha = titleAlpha.coerceIn(0f, 1f)
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> TopAppBar(
            title = {
                Text(
                    text = title,
                    modifier = Modifier.graphicsLayer { this.alpha = alpha },
                )
            },
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = if (barColorAlpha != null) {
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MiuixTheme.colorScheme.surface.copy(alpha = barColorAlpha.coerceIn(0f, 1f)),
                )
            } else {
                TopAppBarDefaults.topAppBarColors()
            },
        )

        KedgeStyle.Miuix -> MiuixTopBarContentColor {
            val effectiveScroll = scrollBehavior ?: LocalKedgePageScrollBehavior.current
            val barColor = if (barColorAlpha != null) {
                MiuixTheme.colorScheme.surface.copy(alpha = barColorAlpha.coerceIn(0f, 1f))
            } else if (backdrop != null) {
                Color.Transparent
            } else {
                MiuixTheme.colorScheme.surface
            }
            val bar: @Composable () -> Unit = {
                MiuixSmallTopAppBar(
                    title = title,
                    modifier = modifier,
                    titleColor = MiuixTheme.colorScheme.onSurface.copy(alpha = alpha),
                    color = barColor,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    scrollBehavior = effectiveScroll?.miuix,
                )
            }
            if (barColorAlpha != null) bar() else KedgeBlurredBar(backdrop = backdrop) { bar() }
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
