package heizige.kk.kedge.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator as MdCircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator as MdLinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import heizige.kk.khromia.components.GlobalToastHost
import heizige.kk.khromia.components.shape.AutoCornersShape
import heizige.kk.khromia.helper.Toast
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator as MiuixCircularProgressIndicator
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator as MiuixInfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator as MiuixLinearProgressIndicator
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.material3.ProgressIndicatorDefaults as MdProgressIndicatorDefaults
import androidx.compose.ui.unit.Dp
import top.yukonga.miuix.kmp.basic.ProgressIndicatorColors
import top.yukonga.miuix.kmp.basic.ProgressIndicatorDefaults

enum class KedgeProgressIndicatorType {
    Linear,
    Circular,
    Infinite,
}

/** Host for Toast.show(...) events. Place once near the app root. */
@Composable
fun KedgeToastHost(animationDurationMillis: Long = 150L) {
    GlobalToastHost(durations = animationDurationMillis)
}

fun KedgeToast(
    message: String,
    isError: Boolean = false,
) {
    Toast.show(message = message, isError = isError)
}

fun KedgeToast(
    message: String,
    icon: ImageVector,
    isError: Boolean = false,
) {
    Toast.show(message = message, icon = icon, isError = isError)
}

fun KedgeToast(
    message: String,
    painter: Painter,
    isError: Boolean = false,
) {
    Toast.show(message = message, painter = painter, isError = isError)
}

fun KedgeDismissToast() {
    Toast.dismiss()
}

@Stable
class KedgeToastController internal constructor(
    private val style: KedgeStyle,
) {
    fun show(
        message: String,
        isError: Boolean = false,
    ) {
        show(message = message, icon = null, painter = null, isError = isError)
    }

    fun show(
        message: String,
        icon: ImageVector,
        isError: Boolean = false,
    ) {
        show(message = message, icon = icon, painter = null, isError = isError)
    }

    fun show(
        message: String,
        painter: Painter,
        isError: Boolean = false,
    ) {
        show(message = message, icon = null, painter = painter, isError = isError)
    }

    fun dismiss() {
        Toast.dismiss()
    }

    private fun show(
        message: String,
        icon: ImageVector?,
        painter: Painter?,
        isError: Boolean,
    ) {
        if (style == KedgeStyle.Miuix) {
            Toast.showCustom {
                KedgeToastPill(
                    message = message,
                    isError = isError,
                    icon = icon,
                    painter = painter,
                    style = style,
                    modifier = Modifier
                        .padding(bottom = 48.dp)
                        .systemBarsPadding(),
                )
            }
            return
        }

        when {
            icon != null -> Toast.show(message = message, icon = icon, isError = isError)
            painter != null -> Toast.show(message = message, painter = painter, isError = isError)
            else -> Toast.show(message = message, isError = isError)
        }
    }
}

@Composable
fun rememberKedgeToastController(): KedgeToastController {
    val style = LocalKedgeStyle.current
    return remember(style) { KedgeToastController(style) }
}

@Composable
fun KedgeToastPill(
    message: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    icon: ImageVector? = null,
    painter: Painter? = null,
    style: KedgeStyle? = null,
) {
    val isMiuix = (style ?: LocalKedgeStyle.current) == KedgeStyle.Miuix
    val shape = AutoCornersShape(32.dp)
    val containerColor = when {
        isMiuix && isError -> MiuixTheme.colorScheme.errorContainer
        isMiuix -> MiuixTheme.colorScheme.secondaryContainer
        isError -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.inverseSurface
    }
    val contentColor = when {
        isMiuix && isError -> MiuixTheme.colorScheme.onErrorContainer
        isMiuix -> MiuixTheme.colorScheme.onSecondaryContainer
        isError -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.inverseOnSurface
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = shape,
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = Color.Black,
                spotColor = Color.Black,
            )
            .heightIn(min = 48.dp)
            .widthIn(max = 320.dp)
            .alpha(0.95f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
            } else if (painter != null) {
                Icon(painter = painter, contentDescription = null, modifier = Modifier.size(20.dp))
            }
            Text(
                text = message,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp,
            )
        }
    }
}

/**
 * MD3 新版把进度改成了 `progress = { fraction }` 形式的 lambda，旧的
 * [KedgeProgressIndicator] 用的是 `progress: Float?`。两个都提供，调用点（无论
 * 原来写的是 `CircularProgressIndicator(progress = { … })` 还是老式 Float）都能
 * 直接换成 Kedge 组件，不用先把 lambda 拆开。
 */
@Composable
fun KedgeProgressIndicator(
    modifier: Modifier = Modifier,
    progress: () -> Float,
    type: KedgeProgressIndicatorType = KedgeProgressIndicatorType.Circular,
    color: Color? = null,
    trackColor: Color? = null,
    strokeWidth: Dp? = null,
) {
    KedgeProgressIndicator(
        modifier = modifier,
        progress = progress(),
        type = type,
        color = color,
        trackColor = trackColor,
        strokeWidth = strokeWidth,
    )
}

/**
 * 双风格进度指示器。
 *
 * `color` / `trackColor` / `strokeWidth` 是为了让 MD3 调用点能原样搬过来而不丢
 * 视觉参数：业务里到处写着 `CircularProgressIndicator(color = …, strokeWidth = 3.dp)`，
 * 如果这里不收下就只能把那些调用点退回 MD3，Miuix 下就又露出 MD3 控件了。
 * Miuix 侧能映射的映射（前景/背景色、线宽），映射不了的（如 gapSize）忽略。
 *
 * @param progress null 表示不确定进度（无限循环）。
 */
@Composable
fun KedgeProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    type: KedgeProgressIndicatorType = KedgeProgressIndicatorType.Circular,
    color: Color? = null,
    trackColor: Color? = null,
    strokeWidth: Dp? = null,
) {
    // 注意：MiuixTheme.colorScheme 只能在 @Composable 上下文读，所以先把颜色取到
    // 局部变量，再交给 remember 缓存，不能在 remember  lambda 里直接读。
    val miuixFg = color ?: MiuixTheme.colorScheme.primary
    val miuixDisabledFg = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val miuixBg = trackColor ?: MiuixTheme.colorScheme.surfaceVariant
    val miuixColors = remember(miuixFg, miuixDisabledFg, miuixBg) {
        ProgressIndicatorColors(
            foregroundColor = miuixFg,
            disabledForegroundColor = miuixDisabledFg,
            backgroundColor = miuixBg,
        )
    }
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> when (type) {
            KedgeProgressIndicatorType.Linear -> if (progress == null) {
                MdLinearProgressIndicator(
                    modifier = modifier,
                    color = color ?: MdProgressIndicatorDefaults.linearColor,
                    trackColor = trackColor ?: MdProgressIndicatorDefaults.linearTrackColor,
                )
            } else {
                MdLinearProgressIndicator(
                    progress = { progress },
                    modifier = modifier,
                    color = color ?: MdProgressIndicatorDefaults.linearColor,
                    trackColor = trackColor ?: MdProgressIndicatorDefaults.linearTrackColor,
                )
            }

            KedgeProgressIndicatorType.Circular,
            KedgeProgressIndicatorType.Infinite,
            -> if (progress == null) {
                MdCircularProgressIndicator(
                    modifier = modifier,
                    color = color ?: MdProgressIndicatorDefaults.circularColor,
                    trackColor = trackColor ?: MdProgressIndicatorDefaults.circularIndeterminateTrackColor,
                    strokeWidth = strokeWidth ?: MdProgressIndicatorDefaults.CircularStrokeWidth,
                )
            } else {
                MdCircularProgressIndicator(
                    progress = { progress },
                    modifier = modifier,
                    color = color ?: MdProgressIndicatorDefaults.circularColor,
                    trackColor = trackColor ?: MdProgressIndicatorDefaults.circularTrackColor,
                    strokeWidth = strokeWidth ?: MdProgressIndicatorDefaults.CircularStrokeWidth,
                )
            }
        }

        KedgeStyle.Miuix -> when (type) {
            KedgeProgressIndicatorType.Linear -> MiuixLinearProgressIndicator(
                modifier = modifier,
                progress = progress,
                colors = miuixColors,
            )

            KedgeProgressIndicatorType.Circular -> MiuixCircularProgressIndicator(
                modifier = modifier,
                progress = progress,
                colors = miuixColors,
                strokeWidth = strokeWidth ?: ProgressIndicatorDefaults.DefaultCircularProgressIndicatorStrokeWidth,
            )

            KedgeProgressIndicatorType.Infinite -> MiuixInfiniteProgressIndicator(modifier = modifier)
        }
    }
}
