package heizige.kk.kedge.adaptive

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem

/**
 * 底部导航栏项。[KedgeBottomBar] 的插槽类型。
 *
 * MD3 的 `NavigationBarItem` 把 `icon` 定义成 `@Composable () -> Unit`，而调用点通常
 * 只持有一个 `ImageVector`（`NavigationBarItem(icon = palette)` 这种写法在 MD3 下
 * 反而编译不过，所以现存的 MD3 调用点都是 `icon = { Icon(palette, null) }`）。
 * 这里统一收成 ImageVector，让两种风格的调用点可以逐字照抄。
 */
data class KedgeNavItem(
    val selected: Boolean,
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

/**
 * 底部导航栏，**按风格分流**：Miuix 下用 Miuix 的 `NavigationBar`（自带 Miuix 的
 * 指示条、圆角与字阶），MD3Exp 下保持原生 `NavigationBar`。
 *
 * 用途：带页签的一级页面（图片生成、提示词、工作区详情等）。这些页面此前直接用
 * `androidx.compose.material3.NavigationBar`，在 Miuix 下会整条漏出 MD3 形态。
 */
@Composable
fun KedgeBottomBar(
    items: List<KedgeNavItem>,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> MiuixNavigationBar(modifier = modifier) {
            items.forEach { item ->
                // Miuix 的 NavigationBarItem 直接收 ImageVector + String，
                // 不像 MD3 要传 @Composable 插槽。
                MiuixNavigationBarItem(
                    selected = item.selected,
                    onClick = item.onClick,
                    icon = item.icon,
                    label = item.label,
                )
            }
        }

        KedgeStyle.MD3Exp -> NavigationBar(modifier = modifier) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = item.selected,
                    onClick = item.onClick,
                    icon = { androidx.compose.material3.Icon(item.icon, contentDescription = null) },
                    label = { Text(item.label) },
                )
            }
        }
    }
}