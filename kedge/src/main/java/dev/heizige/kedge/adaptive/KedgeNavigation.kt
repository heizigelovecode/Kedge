package dev.heizige.kedge.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.components.KedgeIconButton
import dev.heizige.kedge.components.KedgeIconButtonVariant
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarDisplayMode
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationRail as MiuixNavigationRail
import top.yukonga.miuix.kmp.basic.NavigationRailItem as MiuixNavigationRailItem
import top.yukonga.miuix.kmp.basic.SmallTopAppBar as MiuixSmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TopAppBar as MiuixTopAppBar
import top.yukonga.miuix.kmp.basic.rememberNavigationRailState
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
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val titleContent: @Composable () -> Unit = { Text(title) }
            if (centered) {
                CenterAlignedTopAppBar(
                    title = titleContent,
                    modifier = modifier,
                    navigationIcon = navigationIcon,
                    actions = actions,
                )
            } else {
                TopAppBar(
                    title = titleContent,
                    modifier = modifier,
                    navigationIcon = navigationIcon,
                    actions = actions,
                )
            }
        }

        KedgeStyle.Miuix -> MiuixSmallTopAppBar(
            title = title,
            modifier = modifier,
            subtitle = subtitle.orEmpty(),
            navigationIcon = navigationIcon,
            actions = actions,
        )
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
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> LargeTopAppBar(
            title = { Text(title) },
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
        )

        KedgeStyle.Miuix -> MiuixTopAppBar(
            title = title,
            modifier = modifier,
            largeTitle = title,
            subtitle = subtitle.orEmpty(),
            navigationIcon = navigationIcon,
            actions = actions,
        )
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
