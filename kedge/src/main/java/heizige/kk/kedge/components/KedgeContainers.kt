package heizige.kk.kedge.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card as MdCard
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults as MdCardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface as MdSurface
import androidx.compose.material3.LocalContentColor as MdLocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeColors
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CardDefaults as MiuixCardDefaults
import top.yukonga.miuix.kmp.basic.Surface as MiuixSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Miuix 下「卡片类」表面的默认底色：卡片、设置项、分组。
 *
 * Miuix 的设置项（`SwitchPreference` / `ArrowPreference`）本身是**透明**的
 * （`BasicComponent` 只画内容不画背景），所以设置项看到的底色完全来自外面那层卡片。
 * 浅色模式下页面底色是 `surface`，而 `surfaceContainer` 与它只差一档明度，
 * 分组卡片/设置项看着像没画底色（用户反馈：浅色模式「背景色太浅」）。
 *
 * 浅色模式曾改成深一档的 `surfaceContainerHigh`，但那就不是 KernelSU 的观感了
 * （KSU 全程只用 `surfaceContainer`）：TonalSpot 调色板下 surfaceContainer
 * 与 surface 本来就有足够明度差，按 KSU 原文照搬，两种模式统一。
 */
object KedgeMiuixSurface {
    /** 卡片 / 设置项的底色。 */
    val cardContainer: Color
        @Composable get() = MiuixTheme.colorScheme.surfaceContainer
}

@Composable
fun KedgeSurface(
    modifier: Modifier = Modifier,
    /**
     * 可点击。为 `null`（默认）时是纯容器。MD3 有 `Surface(onClick = …)` 重载，
     * 这里一并支持，好让 MD3 调用点能整体换成 Kedge 组件。
     */
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    shape: Shape? = null,
    shadowElevation: Dp = 0.dp,
    /**
     * MD3 的色调抬升（tonal overlay），**只在 MD3Exp 分支生效**。Miuix 没有色调
     * 叠加这回事，收下它是为了让 MD3 调用点（`Surface(tonalElevation = …)`）能整体
     * 换成 Kedge 组件而不丢参数。
     */
    tonalElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        // MD3 的可点击 Surface 是独立重载且 onClick 非空，所以要分两次调用，
        // 不能靠把 onClick 传成 null 落到无 onClick 的重载上。
        KedgeStyle.MD3Exp -> if (onClick != null) {
            MdSurface(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                color = if (color == Color.Unspecified) MaterialTheme.colorScheme.surface else color,
                contentColor = if (contentColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else contentColor,
                shape = shape ?: MaterialTheme.shapes.medium,
                shadowElevation = shadowElevation,
                tonalElevation = tonalElevation,
                border = border,
                content = content,
            )
        } else {
            MdSurface(
                modifier = modifier,
                color = if (color == Color.Unspecified) MaterialTheme.colorScheme.surface else color,
                contentColor = if (contentColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else contentColor,
                shape = shape ?: MaterialTheme.shapes.medium,
                shadowElevation = shadowElevation,
                tonalElevation = tonalElevation,
                border = border,
                content = content,
            )
        }

        KedgeStyle.Miuix -> {
            val surfaceContentColor =
                if (contentColor == Color.Unspecified) MiuixTheme.colorScheme.onSurface else contentColor
            MiuixSurface(
                modifier = modifier.then(
                    if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick) else Modifier
                ),
                color = if (color == Color.Unspecified) MiuixTheme.colorScheme.surface else color,
                contentColor = surfaceContentColor,
                shape = shape ?: RoundedCornerShape(16.dp),
                shadowElevation = shadowElevation,
                border = border,
            ) {
                // Miuix 的 Surface 只 provide Miuix 的 LocalContentColor，而业务代码里
                // 全是 material3 的 Text（读 MD3 那个）。这里同步一份，否则容器里的
                // 文字在深色模式下会是默认黑字。
                CompositionLocalProvider(MdLocalContentColor provides surfaceContentColor) {
                    content()
                }
            }
        }
    }
}

/**
 * [androidx.compose.foundation.shape.CornerSize] 没有 `toDp()`，只能用 Density 反算。
 * DpCornerSize 的 `toPx` 忽略 shapeSize，所以传 [Size.Zero] 即可。
 */
@Composable
private fun CornerSize.toDpCompat(): Dp {
    val density = LocalDensity.current
    return with(density) { toPx(Size.Zero, density).toDp() }
}

@Composable
fun KedgeCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    color: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    shape: Shape? = null,
    onClick: (() -> Unit)? = null,
    /**
     * MD3 配色，**只在 MD3Exp 分支生效**。收下它是为了让 MD3 调用点
     * （`Card(colors = CardDefaults.cardColors(...))`）能整体换成 Kedge 组件而不丢
     * 自定义配色；Miuix 分支用自己那套 `surface` / `surfaceContainer`。
     */
    colors: CardColors? = null,
    /** MD3 边框，只在 MD3Exp 分支生效（见 [colors]）。 */
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val cardColors = when {
                colors != null -> colors
                color == Color.Unspecified && contentColor == Color.Unspecified -> MdCardDefaults.cardColors()
                contentColor == Color.Unspecified -> MdCardDefaults.cardColors(containerColor = color)
                else -> MdCardDefaults.cardColors(containerColor = color, contentColor = contentColor)
            }
            val cardContent: @Composable ColumnScope.() -> Unit = {
                Column(modifier = Modifier.padding(contentPadding), content = content)
            }
            if (onClick != null) {
                MdCard(
                    onClick = onClick,
                    modifier = modifier,
                    colors = cardColors,
                    shape = shape ?: MdCardDefaults.shape,
                    border = border,
                    content = cardContent,
                )
            } else {
                MdCard(
                    modifier = modifier,
                    colors = cardColors,
                    shape = shape ?: MdCardDefaults.shape,
                    border = border,
                    content = cardContent,
                )
            }
        }

        KedgeStyle.Miuix -> {
        // Miuix Card 只会画一个单一 cornerRadius 的 squircle 背景，自己没有 clip，
        // 所以只在**外层**加 Modifier.clip 是没用的：clip 只能削掉像素，削不出直角，
        // 卡片自身的圆角背景会原样透出来，结果每个选项都还是一张全圆角独立卡片，
        // CardGroup 用 indexedShape 算的「首项只圆上边 / 中间直角 / 末项只圆下边」全部失效。
        //
        // 正确做法是两边分工：
        // - 四角统一（独立卡片，或整组就一项）→ 把那个圆角直接交给 Miuix 画，保留 squircle 手感；
        // - 四角不统一（分组里的首/中/末项）→ 背景画成直角，再由外层 clip 裁出目标形状。
        val uniformRadius: Dp? = (shape as? RoundedCornerShape)
            ?.takeIf { r ->
                r.topStart == r.topEnd && r.topStart == r.bottomStart && r.topStart == r.bottomEnd
            }
            ?.topStart
            ?.toDpCompat()
        val miuixCorner: Dp = when {
            shape == null -> MiuixCardDefaults.CornerRadius
            uniformRadius != null -> uniformRadius
            else -> 0.dp
        }

        val cardContentColor = when {
            contentColor != Color.Unspecified -> contentColor
            colors != null && colors.contentColor != Color.Unspecified -> colors.contentColor
            else -> MiuixTheme.colorScheme.onSurfaceContainer
        }
        val cardColors = MiuixCardDefaults.defaultColors(
            color = when {
                color != Color.Unspecified -> color
                colors != null && colors.containerColor != Color.Unspecified -> colors.containerColor
                else -> KedgeMiuixSurface.cardContainer
            },
            contentColor = cardContentColor,
        )

        MiuixCard(
            modifier = modifier.then(
                if (shape != null && uniformRadius == null) Modifier.clip(shape) else Modifier
            ),
            cornerRadius = miuixCorner,
            insideMargin = contentPadding,
            onClick = onClick,
            // 调用方没指定颜色时**不要**覆盖 Miuix Card 的默认色：它的默认是
            // surfaceContainer / onSurfaceContainer（KernelSU 的卡片就是这个色）。
            // 之前这里强制成 surface，和页面背景同色 -> 卡片在 Miuix 下完全隐形，
            // 表现为「设置项没有背景颜色和圆角」。
            //
            // colors 也要读：调用方常写
            // `KedgeCard(colors = CardDefaults.cardColors(containerColor = ...))`
            // 来表达"错误态 / 选中态"这类语义（原先只喂 MD3 分支，Miuix 下被静默
            // 丢弃，于是禁用项和正常项长得一样）。CardColors 的默认值是
            // Color.Unspecified，只有调用方真的指定了才覆盖。
            //
            // contentColor 缺省**不能**传 Unspecified：Miuix 的 Card 会
            // `LocalContentColor provides colors.contentColor`，Unspecified 一路传到
            // 文字着色（textStyle.color ?: LocalContentColor），最后当黑色画 —— 于是
            // 浅色模式看着正常，深色模式下卡片里的标题/摘要全变成黑字（用户报「写死的
            // 颜色、看不清」）。这里补回 Miuix 自己的默认值 onSurfaceContainer。
            colors = cardColors,
            content = {
                // 同 KedgeSurface：Miuix Card 只 provide Miuix 的 LocalContentColor，
                // 业务代码的 material3 Text 读不到，必须同步一份给 MD3 那个。
                CompositionLocalProvider(MdLocalContentColor provides cardContentColor) {
                    content()
                }
            },
        )
        }
    }
}

@Composable
fun KedgeColumnScope(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

@Composable
fun KedgeRowScope(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(12.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content,
    )
}

/**
 * 把任意 [Shape] 近似成 Miuix Card 的单一 [Dp] 圆角。
 *
 * Miuix 的 `Card` 只接受 `cornerRadius: Dp`，没有任意 Shape 的重载。
 * 这里取圆角矩形的最大角半径；非圆角矩形（如 CutCornerShape）也按其
 * 固定圆角量取近似值，保证 CardGroup 的分组圆角观感不丢。
 */

