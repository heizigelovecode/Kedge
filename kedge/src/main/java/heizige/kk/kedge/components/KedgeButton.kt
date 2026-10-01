package heizige.kk.kedge.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonDefaults as MdButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton as MdFilledIconButton
import androidx.compose.material3.FilledTonalIconButton as MdFilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults as MdIconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.IconButton as MdIconButton
import androidx.compose.material3.LocalContentColor as MdLocalContentColor
import androidx.compose.material3.Button as MdButton
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.FilledTonalButton as MdFilledTonalButton
import androidx.compose.material3.TextButton as MdTextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import heizige.kk.khromia.components.pressBounce
import top.yukonga.miuix.kmp.basic.Button as MiuixButton
import top.yukonga.miuix.kmp.basic.ButtonDefaults as MiuixButtonDefaults
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.ui.unit.Dp

enum class KedgeButtonVariant {
    Primary,
    Secondary,
    Text,
}

enum class KedgeIconButtonVariant {
    Standard,
    Filled,
    Tonal,
}

object KedgeButtonDefaults {
    val ContentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)

    /**
     * Miuix 按钮的圆角。Miuix 自带的 16.dp 在按钮上看着是圆角矩形；
     * squircle 会把半径夹到 min(w,h)/2，所以给一个大值就是完全的胶囊形。
     */
    val MiuixPillCornerRadius = 999.dp

    @Composable
    fun md3ButtonShapes(shape: Shape? = null, pressedShape: Shape? = null): ButtonShapes =
        MdButtonDefaults.shapes(shape = shape, pressedShape = pressedShape)

    @Composable
    fun md3IconButtonShapes(shape: Shape? = null, pressedShape: Shape? = null): IconButtonShapes =
        MdIconButtonDefaults.shapes(shape = shape, pressedShape = pressedShape)
}

@Composable
fun KedgeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: KedgeButtonVariant = KedgeButtonVariant.Primary,
    contentPadding: PaddingValues = KedgeButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    shapes: ButtonShapes? = null,
    /**
     * MD3 配色，**只在 MD3Exp 分支生效**。收下它是为了让 MD3 调用点
     * （`Button(colors = ButtonDefaults.buttonColors(...))`）能整体换成 Kedge 组件
     * 而不丢自定义配色；Miuix 分支用自己那套 primary / surface 配色。
     */
    colors: ButtonColors? = null,
    /**
     * Miuix 分支的圆角半径，**只在 Miuix 分支生效**。默认胶囊（KSU 的按钮都是胶囊），
     * 但有些位置要的是大圆角矩形（如侧边栏的「新建文件夹」是 16dp 圆角块）。
     * MD3Exp 分支请用 [shapes]。
     */
    miuixCornerRadius: Dp = KedgeButtonDefaults.MiuixPillCornerRadius,
    content: @Composable RowScope.() -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> KedgeMaterialButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            variant = variant,
            contentPadding = contentPadding,
            interactionSource = interactionSource,
            shapes = shapes,
            colors = colors,
            content = content,
        )

        KedgeStyle.Miuix -> KedgeMiuixButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            variant = variant,
            contentPadding = contentPadding,
            interactionSource = interactionSource,
            miuixCornerRadius = miuixCornerRadius,
            content = content,
        )
    }
}

@Composable
fun KedgeTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = KedgeButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    shapes: ButtonShapes? = null,
    colors: ButtonColors? = null,
    content: @Composable RowScope.() -> Unit,
) {
    KedgeButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = KedgeButtonVariant.Text,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        shapes = shapes,
        colors = colors,
        content = content,
    )
}

@Composable
fun KedgeIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    variant: KedgeIconButtonVariant = KedgeIconButtonVariant.Standard,
    shapes: IconButtonShapes? = null,
    content: @Composable () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val actualShapes = shapes ?: MdIconButtonDefaults.shapes()
            when (variant) {
                KedgeIconButtonVariant.Standard -> MdIconButton(
                    onClick = onClick,
                    shapes = actualShapes,
                    modifier = modifier,
                    enabled = enabled,
                    content = content,
                )

                KedgeIconButtonVariant.Filled -> MdFilledIconButton(
                    onClick = onClick,
                    shapes = actualShapes,
                    modifier = modifier,
                    enabled = enabled,
                    content = content,
                )

                KedgeIconButtonVariant.Tonal -> MdFilledTonalIconButton(
                    onClick = onClick,
                    shapes = actualShapes,
                    modifier = modifier,
                    enabled = enabled,
                    content = content,
                )
            }
        }

        KedgeStyle.Miuix -> {
            val backgroundColor = when (variant) {
                KedgeIconButtonVariant.Standard -> Color.Transparent
                KedgeIconButtonVariant.Filled -> MiuixTheme.colorScheme.primary
                KedgeIconButtonVariant.Tonal -> MiuixTheme.colorScheme.secondaryVariant
            }
            val contentColor = when (variant) {
                KedgeIconButtonVariant.Filled -> MiuixTheme.colorScheme.onPrimary
                KedgeIconButtonVariant.Standard,
                KedgeIconButtonVariant.Tonal,
                -> if (selected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface
            }

            MiuixIconButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                holdDownState = selected,
                backgroundColor = backgroundColor,
                cornerRadius = KedgeButtonDefaults.MiuixPillCornerRadius,
            ) {
                CompositionLocalProvider(MdLocalContentColor provides contentColor) {
                    content()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun KedgeMaterialButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    variant: KedgeButtonVariant,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource?,
    shapes: ButtonShapes?,
    colors: ButtonColors?,
    content: @Composable RowScope.() -> Unit,
) {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val actualShapes = shapes ?: MdButtonDefaults.shapes()
    // MD3 各 variant 的 colors 形参都是非空，这里给个与默认等价的兜底
    val md3Colors = colors ?: MdButtonDefaults.buttonColors()
    val animatedModifier = modifier.pressBounce(
        interactionSource = actualInteractionSource,
        pressedScale = 0.97f,
        enabled = enabled,
    )

    when (variant) {
        KedgeButtonVariant.Primary -> MdButton(
            onClick = onClick,
            shapes = actualShapes,
            modifier = animatedModifier,
            enabled = enabled,
            contentPadding = contentPadding,
            interactionSource = actualInteractionSource,
            colors = md3Colors,
            content = content,
        )

        KedgeButtonVariant.Secondary -> MdFilledTonalButton(
            onClick = onClick,
            shapes = actualShapes,
            modifier = animatedModifier,
            enabled = enabled,
            contentPadding = contentPadding,
            interactionSource = actualInteractionSource,
            colors = md3Colors,
            content = content,
        )

        KedgeButtonVariant.Text -> MdTextButton(
            onClick = onClick,
            shapes = actualShapes,
            modifier = animatedModifier,
            enabled = enabled,
            contentPadding = contentPadding,
            interactionSource = actualInteractionSource,
            colors = md3Colors,
            content = content,
        )
    }
}

@Composable
private fun KedgeMiuixButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    variant: KedgeButtonVariant,
    miuixCornerRadius: Dp,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource?,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = when (variant) {
        KedgeButtonVariant.Primary -> MiuixButtonDefaults.buttonColorsPrimary()
        KedgeButtonVariant.Secondary -> MiuixButtonDefaults.buttonColors()
        KedgeButtonVariant.Text -> MiuixButtonDefaults.buttonColors(
            color = Color.Transparent,
            disabledColor = Color.Transparent,
            contentColor = MiuixTheme.colorScheme.primary,
            disabledContentColor = MiuixTheme.colorScheme.disabledOnSurface,
        )
    }
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor

    MiuixButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        cornerRadius = miuixCornerRadius,
        colors = colors,
        insideMargin = contentPadding,
        interactionSource = interactionSource,
    ) {
        CompositionLocalProvider(MdLocalContentColor provides contentColor) {
            content()
        }
    }
}
