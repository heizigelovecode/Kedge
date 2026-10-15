package heizige.kk.kedge.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge as MdBadge
import androidx.compose.material3.BadgeDefaults as MdBadgeDefaults
import androidx.compose.material3.BadgedBox as MdBadgedBox
import androidx.compose.material3.FilterChipDefaults as MdFilterChipDefaults
import androidx.compose.material3.LocalContentColor as MdLocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import top.yukonga.miuix.kmp.basic.Surface as MiuixSurface
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.Badge as MiuixBadge
import top.yukonga.miuix.kmp.basic.BadgeDefaults as MiuixBadgeDefaults
import top.yukonga.miuix.kmp.basic.BadgedBox as MiuixBadgedBox
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun KedgeBadge(
    modifier: Modifier = Modifier,
    /**
     * MD3 配色，只在 MD3Exp 分支生效；Miuix 用自己的 primary/surface 体系。
     * 收下这两个参数是为了让 MD3 调用点（`Badge(containerColor = …)`）能整体换成
     * Kedge 组件而不丢自定义配色。
     *
     * 注意：它们必须排在 `content` **前面**，因为 `KedgeBadge { Text("3") }` 这种
     * 尾随 lambda 写法绑定的是最后一个参数。
     */
    containerColor: Color? = null,
    contentColor: Color? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdBadge(
            modifier = modifier,
            containerColor = containerColor ?: MdBadgeDefaults.containerColor,
            contentColor = contentColor ?: MdLocalContentColor.current,
            content = content,
        )

        // 必须把颜色透传给 Miuix Badge：它的 `BadgeDefaults.containerColor` 默认是
        // `colorScheme.error`（红橙）。之前这里不传，Miuix 下所有 badge 一律变成
        // 红橙色——探索市场精选卡上的「优选 / 免费 / v1.1.0」就是这么变丑的。
        KedgeStyle.Miuix -> MiuixBadge(
            modifier = modifier,
            containerColor = containerColor ?: MiuixBadgeDefaults.containerColor,
            contentColor = contentColor ?: MiuixBadgeDefaults.contentColor,
            content = content,
        )
    }
}

@Composable
fun KedgeBadgedBox(
    badge: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdBadgedBox(badge = { badge() }, modifier = modifier) { content() }
        KedgeStyle.Miuix -> MiuixBadgedBox(badge = { badge() }, modifier = modifier) { content() }
    }
}

@Composable
fun KedgeTag(
    text: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> AssistChip(
            onClick = onClick ?: {},
            label = { Text(text) },
            modifier = modifier,
            leadingIcon = leadingIcon,
            enabled = onClick != null,
        )

        KedgeStyle.Miuix -> Surface(
            modifier = modifier,
            shape = CircleShape,
            color = MiuixTheme.colorScheme.secondaryVariant,
            contentColor = MiuixTheme.colorScheme.onSecondaryVariant,
            onClick = onClick ?: {},
            enabled = onClick != null,
        ) {
            CompositionLocalProvider(MdLocalContentColor provides MiuixTheme.colorScheme.onSecondaryVariant) {
                MiuixText(
                    text = text,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = MiuixTheme.colorScheme.onSecondaryVariant,
                )
            }
        }
    }
}

/**
 * 双风格筛选标签，签名对齐 MD3 `FilterChip`。
 *
 * Miuix 0.9.3 没有 chip 组件（KernelSU 也没用），所以按 Miuix 的观感自己拼：
 * 胶囊形状 + 内部 `surfaceContainer`，**选中态直接取勾选框自己的那一组色**
 * （`CheckboxDefaults` 里 `checkedBackground = primary` / `checkedForeground = onPrimary`），
 * 于是胶囊底色和勾是同一块 primary，不会再出现「勾一个蓝、胶囊另一个 primaryContainer 蓝」
 * 的两档色。
 *
 * **勾只用选中时出现，且是 [AnimatedVisibility] 包的一枚对勾 [MiuixIcon]**：
 * Miuix 的 Checkbox 是一个 18dp 方框 + 自己那套勾选动画，塞进胶囊里只能靠「挂不挂载」
 * 切换，出现/消失都是硬切；换成图标后勾可以横向展开 + 淡入、收起时反向，胶囊宽度跟着
 * 动画走。**内容高度下限锁死 18dp**（= 勾的边长），否则未选中的「素胶囊」只剩文本，
 * body2 行高比 18dp 小，同一条轨道上会矮一两 dp。
 *
 * @param shape 仅 MD3Exp 使用；Miuix 固定胶囊。
 * @param containerColor 未选中容器色。null = 各风格默认（Miuix 是 `surfaceContainer`）。
 * @param selectedContainerColor 选中容器色。null = 各风格默认（Miuix 是 `primary`）。
 * @param labelColor 未选中标签色。null = 默认；Miuix 下仅在显式给值时才覆盖
 *   `LocalContentColor`，免得动到既有调用点的观感。
 * @param selectedLabelColor 选中标签色。null = 默认（Miuix 是 `onPrimary`）。
 * @param iconColor 未选中前置图标色。null = 默认（MD3 由 FilterChip 注入它自己的图标色）。
 * @param selectedIconColor 选中前置图标色。null = 默认。
 * @param contentPadding 内容内边距。null = 三个 chip 组件的统一默认
 *   [kedgeChipContentPadding]（左 6dp / 右 16dp，垂直仍取本组件 MD3 默认值）；Miuix 分支不使用
 *   （它有自己的 12dp/6dp）。
 * @param border 容器描边。null = [KedgeChipNoBorder]（0dp 透明 = 不画描边）；Miuix 分支不使用。
 * @param cornerRadius 静止圆角（MD3 专用）。给了就启用组件内部的按压圆角收缩
 *   `pressedCorner = max(静止 − 8dp, 8dp)`（见 [rememberKedgeChipShape]）；
 *   null = 用 MD3 自己的 `FilterChipDefaults.shape`，不做按压收缩。
 * @param interactionSource 按压状态源。null = 组件内部解析出**一枚**实例，
 *   **同一实例同时喂给圆角动画与 MD3 FilterChip 本体**（否则 MD3 收到 null 会自建一枚，
 *   圆角那支读的是另一枚 ⇒ 按下收不动圆角）；调用点若自己也在
 *   `collectIsPressedAsState()`，必须把同一个 source 传进来。Miuix 分支不使用。
 */
@Composable
fun KedgeFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    shape: Shape? = null,
    containerColor: Color? = null,
    selectedContainerColor: Color? = null,
    labelColor: Color? = null,
    selectedLabelColor: Color? = null,
    iconColor: Color? = null,
    selectedIconColor: Color? = null,
    contentPadding: PaddingValues? = null,
    border: BorderStroke? = null,
    cornerRadius: Dp? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            // MD3 的 FilterChip 会把**它自己的**图标色通过 `ProvideContentColorTextStyle`
            // 注入 `leadingIcon` 槽（在槽外层），所以这里要在槽内再套一层
            // CompositionLocalProvider —— 内层胜出。只有调用点显式给了 `iconColor` /
            // `selectedIconColor` 才套，没给就原样透传（= MD3 默认语义，零影响）。
            val stateIconColor = if (selected) selectedIconColor else iconColor
            val mdLeadingIcon: (@Composable () -> Unit)? =
                if (leadingIcon == null || stateIconColor == null) {
                    leadingIcon
                } else {
                    {
                        CompositionLocalProvider(MdLocalContentColor provides stateIconColor) {
                            leadingIcon()
                        }
                    }
                }
            // 按压源只解析一处：同一实例既喂圆角动画、也喂 MD3 FilterChip 本体。
            // 分开解析会各自 remember 一枚（各占一个 remember 槽）—— MD3 的私有 chip 实现在
            // `interactionSource == null` 时**自建一枚**：material3 1.5.0-alpha29 aar 的
            // ChipKt.class 里私有 Chip / SelectableChip 那三处
            // `invokestatic InteractionSourceKt.MutableInteractionSource:()`，行号表分别落在
            // Chip.kt:3200 / :3248 / :3303，与 alpha26 sources 同名行同源：
            // `val interactionSource = interactionSource ?: remember { MutableInteractionSource() }`。
            // 于是 press 事件只会进 MD3 那枚，圆角那支永远收不动。
            val chipInteraction = interactionSource ?: remember { MutableInteractionSource() }
            androidx.compose.material3.FilterChip(
                selected = selected,
                onClick = onClick,
                label = label,
                modifier = modifier,
                enabled = enabled,
                leadingIcon = mdLeadingIcon,
                shape = shape ?: if (cornerRadius != null) {
                    // 用户裁定 ②：按压圆角收缩落在组件内部（统一公式 max(静止−8dp, 8dp)），
                    // 调用点只给静止圆角这一个数。
                    rememberKedgeChipShape(cornerRadius, chipInteraction)
                } else {
                    androidx.compose.material3.FilterChipDefaults.shape
                },
                // 透传容器 / 标签配色；null 的槽位回落到 MD3 自己的默认值，
                // 于是既有 21 个调用点行为与之前逐帧一致（实测
                // `grep -rEn 'KedgeFilterChip[[:space:]]*\(' Android/Project/KhatKit --include=*.kt`
                // 命中 21 处；其中 20 处没传新参数，唯一显式传色的是 ExploreMarketPage.kt:2112）。
                colors = MdFilterChipDefaults.filterChipColors(
                    containerColor = containerColor
                        ?: MdFilterChipDefaults.filterChipColors().containerColor,
                    selectedContainerColor = selectedContainerColor
                        ?: MdFilterChipDefaults.filterChipColors().selectedContainerColor,
                    labelColor = labelColor
                        ?: MdFilterChipDefaults.filterChipColors().labelColor,
                    selectedLabelColor = selectedLabelColor
                        ?: MdFilterChipDefaults.filterChipColors().selectedLabelColor,
                ),
                // 下面三个槽 null 时回落到 t20 的统一默认值（未选中不画描边 / 左 6 右 16 /
                // 图标-文字 10dp）—— 显式传值时仍以调用点为准。
                border = border ?: KedgeChipNoBorder,
                horizontalArrangement = KedgeChipHorizontalArrangement,
                contentPadding = contentPadding
                    ?: kedgeChipContentPadding(MdFilterChipDefaults.ContentPadding),
                interactionSource = chipInteraction,
            )
        }

        KedgeStyle.Miuix -> MiuixSurface(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            shape = CircleShape,
            // 选中底色 = Miuix Checkbox 的 checkedBackgroundColor（就是 primary），
            // 文字色 = 它的 checkedForegroundColor（onPrimary）—— 两处同源，
            // 主题换 seed / 深浅色翻转时不会各自漂移。
            color = if (selected) {
                selectedContainerColor ?: MiuixTheme.colorScheme.primary
            } else {
                containerColor ?: MiuixTheme.colorScheme.surfaceContainer
            },
            contentColor = if (selected) {
                selectedLabelColor ?: MiuixTheme.colorScheme.onPrimary
            } else {
                labelColor ?: MiuixTheme.colorScheme.onSurfaceContainer
            },
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    // 高度下限 = 勾的边长：不选中时勾不占位，只剩文本，而 body2 行高
                    // 比 18dp 小 ⇒ 不给下限「素胶囊」会比「选中胶囊」矮一两 dp，
                    // 并排看就是高度不齐。
                    .heightIn(min = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 勾：选中时横向展开 + 淡入，取消时收拢 + 淡出。
                //
                // 间距不挂在 Row 的 `spacedBy` 上：那样收起后仍会留一个 6dp 空档。
                // 把 6dp 放在 AnimatedVisibility **内部**，收起时连间距一起收掉。
                AnimatedVisibility(
                    visible = selected,
                    enter = fadeIn() +
                        expandHorizontally(expandFrom = Alignment.Start),
                    exit = fadeOut() +
                        shrinkHorizontally(shrinkTowards = Alignment.Start),
                ) {
                    Box(modifier = Modifier.padding(end = 6.dp)) {
                        MiuixIcon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            // 勾只在选中时出现，而选中胶囊的底色就是 primary
                            // （见上），默认与胶囊文字同色；调用点给了 selectedLabelColor 就跟它走。
                            tint = selectedLabelColor ?: MiuixTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                if (leadingIcon != null) {
                    Box(modifier = Modifier.padding(end = 6.dp)) {
                        // 前置图标色同样只在调用点显式给值时才覆盖 material3 的
                        // LocalContentColor（Miuix 的 Surface 只 provide 自己的那套），
                        // 不显式给就维持原样，既有调用点零影响。
                        val stateLeadingIconColor = if (selected) selectedIconColor else iconColor
                        if (stateLeadingIconColor != null) {
                            CompositionLocalProvider(MdLocalContentColor provides stateLeadingIconColor) {
                                leadingIcon()
                            }
                        } else {
                            leadingIcon()
                        }
                    }
                }
                CompositionLocalProvider(
                    androidx.compose.material3.LocalTextStyle provides
                        MiuixTheme.textStyles.body2,
                ) {
                    // 标签色：只有调用点给当前状态显式传了 labelColor / selectedLabelColor
                    // 才覆盖 material3 的 LocalContentColor（它们桥接到 onBackground），
                    // 否则不动 —— 既有 20 个调用点一个都没传新参数，合成结果与之前一致。
                    val stateLabelColor = if (selected) selectedLabelColor else labelColor
                    if (stateLabelColor != null) {
                        CompositionLocalProvider(MdLocalContentColor provides stateLabelColor) {
                            label()
                        }
                    } else {
                        label()
                    }
                }
            }
        }
    }
}
