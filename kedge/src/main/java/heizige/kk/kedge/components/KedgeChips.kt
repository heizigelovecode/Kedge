package heizige.kk.kedge.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip as MdAssistChip
import androidx.compose.material3.AssistChipDefaults as MdAssistChipDefaults
import androidx.compose.material3.FilterChipDefaults as MdFilterChipDefaults
import androidx.compose.material3.InputChip as MdInputChip
import androidx.compose.material3.InputChipDefaults as MdInputChipDefaults
import androidx.compose.material3.LocalContentColor as MdLocalContentColor
import androidx.compose.material3.LocalTextStyle as MdLocalTextStyle
import androidx.compose.material3.Text as MdText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.components.shape.AutoCornersShape
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

// ---------------------------------------------------------------------------------------------
// 三枚 chip（[KedgeAssistChip] / [KedgeInputChip] / `KedgeFilterChip`）共用的 MD3Exp 默认值。
//
// 数值来源 = KhatKit 探索市场页原来那枚手绘筛选药丸（t13 之前的工作区版本 MarketFilterChip 的
// `Row(...).clip(shape).background(...).clickable(indication = null).height(44.dp)
//  .padding(start = chipTileInset(6.dp), end = 16.dp)` + `Arrangement.spacedBy(10.dp)` +
// 按下圆角 22→14 / 12→8，见 `git show HEAD:…/ExploreMarketPage.kt`）。t20 用户裁定把它升级成
// `:kedge` 三个 chip 组件的**默认行为**，一次生效全 App；Miuix 分支不读这些值，保持各自的设计语言。
// ---------------------------------------------------------------------------------------------

/**
 * 按下圆角统一公式：`pressedCorner = max(静止圆角 − 8dp, 8dp)`。
 *
 * 真实分段（不是全程 −8dp）：静止 ≥ 16dp 时按下 = 静止 − 8dp；静止 < 16dp 时按下**恒为 8dp**，
 * 所以静止 < 8dp 的调用点按下反而变大（如 6dp → 8dp）。
 */
internal fun pressedChipCorner(restingCorner: Dp): Dp = maxOf(restingCorner - 8.dp, 8.dp)

/**
 * 统一内容内边距：水平 = 原手绘药丸的 `padding(start = 6.dp, end = 16.dp)`；
 * **垂直原样沿用各 chip 组件自己的 MD3 默认值**（调用处把 `AssistChipDefaults.ContentPadding`
 * / `FilterChipDefaults.ContentPadding` / `InputChipDefaults.contentPadding(...)` 传进来）。
 *
 * 不写成 `PaddingValues(start = 6.dp, end = 16.dp)`：那等于把垂直内边距硬置 0，
 * 而硬置 0 会**抹掉**将来 MD3 的垂直内边距。
 * 实测 material3 1.5.0-alpha26 源码：`AssistChipDefaults.ContentPadding` 与
 * `FilterChipDefaults.ContentPadding` 都是 `PaddingValues(horizontal = 8.dp)`，
 * `InputChipDefaults.contentPadding(...)` 返回 `PaddingValues(start = …, end = …)`
 * —— 三者垂直值本来就是 0dp，所以读默认值 = 与手绘药丸逐值一致，同时不写死 0。
 */
internal fun kedgeChipContentPadding(mdDefault: PaddingValues): PaddingValues = PaddingValues(
    start = 6.dp,
    top = mdDefault.calculateTopPadding(),
    end = 16.dp,
    bottom = mdDefault.calculateBottomPadding(),
)

/**
 * 默认图标-文字间距 10dp（MD3 chip 自带的默认是 8dp）。
 *
 * 用 `FilterChipDefaults` 自己那支 `horizontalArrangement(spacing)` 工厂：拿到的是与默认值
 * 同一个 `ChipArrangement` 实现，只是间距换成 10dp —— 不是另写一套排布，也没有抄 chip 主体。
 */
internal val KedgeChipHorizontalArrangement: Arrangement.Horizontal =
    MdFilterChipDefaults.horizontalArrangement(10.dp)

/**
 * 默认不画描边：0dp 透明边框（= 「没画」）。
 *
 * ⚠️ 已知副作用（用户裁定按 low 记账，暂不修）：`enabled = false` 时 MD3 会把
 * `Md*ChipDefaults` 的边框换成禁用态（通常也是透明 + 禁用容器色），这里统一盖成 0dp 透明，
 * 所以**禁用 chip 同样没有描边**，只有容器/文字色能表达禁用。将来真出现 disabled chip 时，
 * 在 [KedgeAssistChip] / [KedgeInputChip] / `KedgeFilterChip` 的 MD3 分支按 `enabled` 分流
 * （disabled 时回落到 `Md*ChipDefaults` 的 border 实参）即可，当前 25 个调用点全部 `enabled = true`。
 */
internal val KedgeChipNoBorder = BorderStroke(0.dp, Color.Transparent)

/**
 * 按压圆角收缩：静止圆角由调用点的 `cornerRadius` 给，按下 = [pressedChipCorner]，松手用同一档
 * spring 弹回 —— 与探索市场页原来那套 `animateDpAsState(spring(StiffnessMediumLow))` 同源。
 *
 * 用户裁定 ②：这段必须落在组件内部，调用点不再各写一份 pressed 逻辑。
 * [interactionSource] 传 null 时这里会自建一枚，但**三个 chip 组件的 MD3 分支都先解析出一枚
 * 非 null 实例**再传进来，并且把同一实例也交给 MD3 chip 本体 —— 否则 MD3 会自建另一枚，
 * 按下事件永远进不了本函数读的这个 source。
 */
@Composable
internal fun rememberKedgeChipShape(
    restingCorner: Dp,
    interactionSource: MutableInteractionSource?,
): Shape {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val pressedCorner = pressedChipCorner(restingCorner)
    // 非按下时 progress = 0 ⇒ 圆角**直接等于**静止值，不让组件再套一层动画把调用点自己的
    // spring（如市场页 22↔12 那支）拖慢；只有「按下那一下」由这里插值。
    val pressProgress by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "kedge_chip_press_corner",
    )
    return AutoCornersShape(restingCorner + (pressedCorner - restingCorner) * pressProgress)
}

/**
 * 可点击的辅助标签（筛选/跳转用），**按风格分流**。
 *
 * MD3Exp 下用原生 [MdAssistChip]。Miuix 没有 Chip 组件，这里用胶囊形态：
 * `surfaceContainer` 底 + `onSurfaceVariantSummary` 字色，圆角 50（与
 * [KedgeSingleChoiceSegmentedRow] 的分段胶囊同源）。
 *
 * @param label 标签文案。收 String 而不是插槽，和 Miuix 的 Text 保持一致；
 *   调用点若需要自定义内容用 [content]。
 */
@Composable
fun KedgeAssistChip(
    onClick: () -> Unit,
    label: String? = null,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    /** MD3 配色，只在 MD3Exp 分支生效。 */
    containerColor: Color? = null,
    labelColor: Color? = null,
    /** 静止圆角（MD3 专用）：给了就启用「按下 = max(静止−8dp, 8dp)」的圆角收缩。 */
    cornerRadius: Dp? = null,
    /** MD3 专用槽，null = 本文件的统一默认（左 6dp/右 16dp / 无描边 / 10dp 间距）。 */
    contentPadding: PaddingValues? = null,
    border: BorderStroke? = null,
    /**
     * 按压状态源。null = 组件内部解析出**一枚**实例，并且**同一实例同时喂给圆角动画与 MD3 chip
     * 本体**（MD3 收到 null 会自建另一枚，圆角那支就收不动）。调用点若自己也在
     * `collectIsPressedAsState()`，把同一个 source 传进来即可。
     */
    interactionSource: MutableInteractionSource? = null,
    content: (@Composable () -> Unit)? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            // 按压源只解析一处：**同一实例**既喂圆角动画、也喂 MD3 AssistChip 本体。
            // 分开解析会各自 remember 一枚（各占一个 remember 槽）—— MD3 的私有 chip 实现在
            // `interactionSource == null` 时**自建一枚**：material3 1.5.0-alpha29 aar 的
            // ChipKt.class 里私有 Chip(`Chip-EosBjUw`) / SelectableChip(`SelectableChip-9rhh4-4`)
            // 三处 `invokestatic InteractionSourceKt.MutableInteractionSource:()`，行号表分别落在
            // Chip.kt:3200 / :3248 / :3303，与 alpha26 sources 同名行同源：
            // `val interactionSource = interactionSource ?: remember { MutableInteractionSource() }`。
            // 于是 press 事件只进 MD3 那枚，圆角那支永远收不动。
            val chipInteraction = interactionSource ?: remember { MutableInteractionSource() }
            MdAssistChip(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                label = content ?: { MdText(label.orEmpty()) },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                // t20 统一默认值：未选中不画描边 / 内容内边距左 6 右 16 / 图标-文字 10dp。
                // 垂直内边距由 kedgeChipContentPadding 从 AssistChipDefaults.ContentPadding
                // 原样读出（material3 = 0dp），高度仍是组件自己的 32dp 下限，
                // 与 t20 之前逐值一致；不再写死 0。
                shape = if (cornerRadius != null) {
                    rememberKedgeChipShape(cornerRadius, chipInteraction)
                } else {
                    MdAssistChipDefaults.shape
                },
                border = border ?: KedgeChipNoBorder,
                horizontalArrangement = KedgeChipHorizontalArrangement,
                contentPadding = contentPadding
                    ?: kedgeChipContentPadding(MdAssistChipDefaults.ContentPadding),
                interactionSource = chipInteraction,
                colors = MdAssistChipDefaults.assistChipColors(
                    containerColor = containerColor ?: MdAssistChipDefaults.assistChipColors().containerColor,
                    labelColor = labelColor ?: MdAssistChipDefaults.assistChipColors().labelColor,
                ),
            )
        }

        KedgeStyle.Miuix -> Row(
            modifier = modifier
                .clip(RoundedCornerShape(50))
                .background(MiuixTheme.colorScheme.surfaceContainer)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()
            if (content != null) {
                content()
            } else {
                MiuixText(
                    text = label.orEmpty(),
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = 1,
                )
            }
            trailingIcon?.invoke()
        }
    }
}

/**
 * 输入型标签（带删除槽的实心 chip），**按风格分流**，签名对齐 MD3 `InputChip`。
 *
 * MD3Exp 直接用原生 [MdInputChip]。Miuix 0.9.3 没有 chip 组件，按
 * [KedgeFilterChip] 的 Miuix 分支同源拼胶囊：圆角 50、`surfaceContainer` 底 +
 * `onSurfaceContainer` 字色、body2 文本、高度下限 18dp（= 勾的边长）；选中态整块换成
 * `primary` / `onPrimary`。
 *
 * [trailingIcon] 是纯插槽：删除动作由调用点在自己的图标上挂 `clickable`
 * （KhatKit 的 `TagsInput` 就是这么用的），这里只把它排在文字右侧。
 *
 * @param label 标签内容插槽（对齐 MD3 收 composable，而不是 String）。
 * @param leadingIcon / trailingIcon 前后图标插槽，两种风格都直接渲染。
 */
@Composable
fun KedgeInputChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    /** 静止圆角（MD3 专用）：给了就启用「按下 = max(静止−8dp, 8dp)」的圆角收缩。 */
    cornerRadius: Dp? = null,
    /** MD3 专用槽，null = 本文件的统一默认（左 6dp/右 16dp / 无描边 / 10dp 间距）。 */
    contentPadding: PaddingValues? = null,
    border: BorderStroke? = null,
    /**
     * 按压状态源。null = 组件内部解析出**一枚**实例，并且**同一实例同时喂给圆角动画与 MD3 chip
     * 本体**（MD3 收到 null 会自建另一枚，圆角那支就收不动）。调用点若自己也在
     * `collectIsPressedAsState()`，把同一个 source 传进来即可。
     */
    interactionSource: MutableInteractionSource? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            // 同 [KedgeAssistChip]：按压源只解析一处，同一实例既喂圆角动画也喂 MD3 InputChip
            // 本体（MD3 收到 null 会自建一枚，见 material3 1.5.0-alpha29 ChipKt.class
            // SelectableChip 的 Chip.kt:3248 / :3303）。
            val chipInteraction = interactionSource ?: remember { MutableInteractionSource() }
            MdInputChip(
                selected = selected,
                onClick = onClick,
                label = label,
                modifier = modifier,
                enabled = enabled,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                // t20 统一默认值：未选中不画描边 / 左 6 右 16 / 图标-文字 10dp。
                shape = if (cornerRadius != null) {
                    rememberKedgeChipShape(cornerRadius, chipInteraction)
                } else {
                    MdInputChipDefaults.shape
                },
                border = border ?: KedgeChipNoBorder,
                horizontalArrangement = KedgeChipHorizontalArrangement,
                contentPadding = contentPadding
                    ?: kedgeChipContentPadding(
                        MdInputChipDefaults.contentPadding(
                            hasAvatar = false,
                            hasLeadingIcon = leadingIcon != null,
                            hasTrailingIcon = trailingIcon != null,
                        )
                    ),
                interactionSource = chipInteraction,
            )
        }

        KedgeStyle.Miuix -> {
            val contentColor = if (selected) {
                MiuixTheme.colorScheme.onPrimary
            } else {
                MiuixTheme.colorScheme.onSurfaceContainer
            }
            CompositionLocalProvider(
                // 槽里的 material3 Text/Icon 读的是 **MD3 的** LocalContentColor
                // （桥接到 onBackground），这里显式对齐到胶囊自己的内容色 + body2 字号，
                // 免得一排标签在 Miuix 下字号/字色跟胶囊对不上。
                MdLocalContentColor provides contentColor,
                MdLocalTextStyle provides MiuixTheme.textStyles.body2,
            ) {
                Row(
                    modifier = modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (selected) {
                                MiuixTheme.colorScheme.primary
                            } else {
                                MiuixTheme.colorScheme.surfaceContainer
                            }
                        )
                        .clickable(enabled = enabled, onClick = onClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        // 高度下限 = 18dp，与 KedgeFilterChip 的 Miuix 胶囊一致：
                        // 不带图标的素胶囊不会比带图标的矮一两 dp，并排看才齐。
                        .heightIn(min = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    leadingIcon?.invoke()
                    label()
                    trailingIcon?.invoke()
                }
            }
        }
    }
}
