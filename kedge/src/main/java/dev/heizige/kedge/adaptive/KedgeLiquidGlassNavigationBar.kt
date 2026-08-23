package dev.heizige.kedge.adaptive

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
import kotlin.math.floor
import top.yukonga.miuix.kmp.blur.Backdrop
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.blur
import top.yukonga.miuix.kmp.blur.drawBackdrop
import top.yukonga.miuix.kmp.blur.highlight.BloomStroke
import top.yukonga.miuix.kmp.blur.highlight.Highlight
import top.yukonga.miuix.kmp.blur.highlight.LightPosition
import top.yukonga.miuix.kmp.blur.highlight.LightSource
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Floating liquid-glass bottom navigation inspired by KernelSU's FloatingBottomBar and the
 * compose-miuix-ui IosLiquidGlassNavigationBar sample. The component uses miuix-blur when enabled
 * and falls back to layered glass drawing when blur is disabled.
 */
@Composable
fun <T> KedgeLiquidGlassNavigationBar(
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    navigationItems: List<KedgeNavigationItem<T>>,
    modifier: Modifier = Modifier,
    itemWidth: Dp = 76.dp,
    height: Dp = 64.dp,
    contentPadding: PaddingValues = PaddingValues(4.dp),
    dragSelectionEnabled: Boolean = true,
    backdrop: KedgeLiquidGlassBackdropState? = null,
    blurEnabled: Boolean = true,
    blurRadius: Dp = 18.dp,
    colors: KedgeLiquidGlassNavigationBarColors = KedgeLiquidGlassNavigationBarDefaults.colors(),
) {
    if (navigationItems.isEmpty()) return

    val density = LocalDensity.current
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val internalBackdrop = rememberKedgeLiquidGlassBackdrop()
    val resolvedBackdrop = backdrop ?: internalBackdrop
    val selectedIndex = navigationItems.indexOfFirst { it.key == selectedItem }.let { index ->
        if (index >= 0) index else 0
    }
    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "KedgeLiquidGlassSelectedIndex",
    )
    var barWidthPx by remember { mutableStateOf(0f) }
    var indicatorPressed by remember { mutableStateOf(false) }
    val indicatorScale by animateFloatAsState(
        targetValue = if (indicatorPressed) 1.09f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "KedgeLiquidGlassIndicatorScale",
    )

    val itemWidthPx = with(density) { itemWidth.toPx() }
    val startPaddingPx = with(density) { contentPadding.calculateLeftPadding(LayoutDirection.Ltr).toPx() }
    val endPaddingPx = with(density) { contentPadding.calculateRightPadding(LayoutDirection.Ltr).toPx() }

    fun selectFromX(x: Float) {
        if (itemWidthPx <= 0f || navigationItems.isEmpty()) return
        val contentWidth = (barWidthPx - startPaddingPx - endPaddingPx).coerceAtLeast(0f)
        val localX = if (isLtr) x - startPaddingPx else contentWidth - (x - startPaddingPx)
        val index = floor(localX / itemWidthPx).toInt().coerceIn(0, navigationItems.lastIndex)
        val item = navigationItems[index]
        if (item.enabled && item.key != selectedItem) onItemSelected(item.key)
    }

    Box(
        modifier = modifier
            .then(if (backdrop == null && blurEnabled) Modifier.layerBackdrop(resolvedBackdrop.layerBackdrop) else Modifier)
            .width(IntrinsicSize.Min)
            .height(height)
            .onGloballyPositioned { barWidthPx = it.size.width.toFloat() }
            .then(
                if (dragSelectionEnabled) {
                    Modifier.pointerInput(navigationItems, selectedItem, itemWidthPx, barWidthPx, isLtr) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                indicatorPressed = true
                                selectFromX(offset.x)
                            },
                            onDragEnd = { indicatorPressed = false },
                            onDragCancel = { indicatorPressed = false },
                        ) { change, _ -> selectFromX(change.position.x) }
                    }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .shadow(elevation = 14.dp, shape = CircleShape, clip = false)
                .clip(CircleShape)
                .then(
                    if (blurEnabled) {
                        Modifier.kedgeDrawGlassBackdrop(
                            backdrop = resolvedBackdrop.layerBackdrop,
                            shape = CircleShape,
                            blurRadius = blurRadius,
                            highlight = KedgeGlassContainerHighlight,
                            onDrawSurface = { drawRect(colors.containerColor) },
                        )
                    } else {
                        Modifier.background(colors.containerColor, CircleShape)
                    }
                )
                .liquidGlassSurface(colors)
                .border(BorderStroke(0.7.dp, colors.borderColor), CircleShape),
        )

        Box(
            modifier = Modifier
                .padding(contentPadding)
                .graphicsLayer {
                    val offset = animatedIndex * itemWidthPx
                    translationX = if (isLtr) offset else -offset
                    scaleX = indicatorScale
                    scaleY = indicatorScale
                }
                .width(itemWidth)
                .fillMaxHeight()
                .clip(CircleShape)
                .then(
                    if (blurEnabled) {
                        Modifier.kedgeDrawGlassBackdrop(
                            backdrop = resolvedBackdrop.layerBackdrop,
                            shape = CircleShape,
                            blurRadius = blurRadius * 0.55f,
                            highlight = KedgeGlassIndicatorHighlight,
                            onDrawSurface = {
                                drawRect(colors.indicatorColor)
                                drawRect(Color.White.copy(alpha = 0.08f))
                            },
                        )
                    } else {
                        Modifier.background(colors.indicatorColor, CircleShape)
                    }
                )
                .liquidGlassIndicator(colors)
                .border(BorderStroke(0.6.dp, colors.indicatorBorderColor), CircleShape),
        )

        Row(
            modifier = Modifier
                .height(height)
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navigationItems.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                KedgeLiquidGlassNavigationItem(
                    item = item,
                    selected = selected,
                    itemWidth = itemWidth,
                    colors = colors,
                    onClick = { onItemSelected(item.key) },
                )
            }
        }
    }
}

@Composable
fun <T> KedgeLiquidGlassBottomBar(
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    navigationItems: List<KedgeNavigationItem<T>>,
    modifier: Modifier = Modifier,
    itemWidth: Dp = 76.dp,
    height: Dp = 64.dp,
    contentPadding: PaddingValues = PaddingValues(4.dp),
    dragSelectionEnabled: Boolean = true,
    backdrop: KedgeLiquidGlassBackdropState? = null,
    blurEnabled: Boolean = true,
    blurRadius: Dp = 18.dp,
    colors: KedgeLiquidGlassNavigationBarColors = KedgeLiquidGlassNavigationBarDefaults.colors(),
) {
    KedgeLiquidGlassNavigationBar(
        selectedItem = selectedItem,
        onItemSelected = onItemSelected,
        navigationItems = navigationItems,
        modifier = modifier,
        itemWidth = itemWidth,
        height = height,
        contentPadding = contentPadding,
        dragSelectionEnabled = dragSelectionEnabled,
        backdrop = backdrop,
        blurEnabled = blurEnabled,
        blurRadius = blurRadius,
        colors = colors,
    )
}

@Composable
fun rememberKedgeLiquidGlassBackdrop(): KedgeLiquidGlassBackdropState {
    val layerBackdrop = rememberLayerBackdrop { drawContent() }
    return remember(layerBackdrop) { KedgeLiquidGlassBackdropState(layerBackdrop) }
}

@Stable
class KedgeLiquidGlassBackdropState internal constructor(
    internal val layerBackdrop: LayerBackdrop,
)

@Composable
fun KedgeLiquidGlassBackdrop(
    modifier: Modifier = Modifier,
    backdrop: KedgeLiquidGlassBackdropState = rememberKedgeLiquidGlassBackdrop(),
    content: @Composable BoxScope.(KedgeLiquidGlassBackdropState) -> Unit,
) {
    Box(modifier = modifier.layerBackdrop(backdrop.layerBackdrop)) {
        content(backdrop)
    }
}

@Composable
private fun <T> KedgeLiquidGlassNavigationItem(
    item: KedgeNavigationItem<T>,
    selected: Boolean,
    itemWidth: Dp,
    colors: KedgeLiquidGlassNavigationBarColors,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.12f else if (selected) 1.04f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "KedgeLiquidGlassItemScale",
    )
    val contentColor = if (selected) colors.selectedContentColor else colors.contentColor

    Column(
        modifier = Modifier
            .width(itemWidth)
            .fillMaxHeight()
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = item.enabled,
                role = Role.Tab,
                onClick = onClick,
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (item.enabled) 1f else 0.42f
            },
        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.icon,
                contentDescription = item.contentDescription,
                tint = contentColor,
                modifier = Modifier.size(22.dp),
            )
            if (item.badge != null) {
                Box(modifier = Modifier.graphicsLayer { translationX = 9.dp.toPx(); translationY = (-5).dp.toPx() }) {
                    item.badge.invoke()
                }
            }
        }
        Text(
            text = item.label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Immutable
data class KedgeLiquidGlassNavigationBarColors(
    val containerColor: Color,
    val contentColor: Color,
    val selectedContentColor: Color,
    val indicatorColor: Color,
    val borderColor: Color,
    val indicatorBorderColor: Color,
    val highlightColor: Color,
    val lowlightColor: Color,
)

object KedgeLiquidGlassNavigationBarDefaults {
    @Composable
    fun colors(
        containerColor: Color = defaultContainerColor(),
        contentColor: Color = defaultContentColor(),
        selectedContentColor: Color = defaultSelectedContentColor(),
        indicatorColor: Color = defaultIndicatorColor(),
        borderColor: Color = Color.White.copy(alpha = 0.28f),
        indicatorBorderColor: Color = Color.White.copy(alpha = 0.36f),
        highlightColor: Color = Color.White.copy(alpha = 0.44f),
        lowlightColor: Color = Color.Black.copy(alpha = 0.10f),
    ): KedgeLiquidGlassNavigationBarColors = KedgeLiquidGlassNavigationBarColors(
        containerColor = containerColor,
        contentColor = contentColor,
        selectedContentColor = selectedContentColor,
        indicatorColor = indicatorColor,
        borderColor = borderColor,
        indicatorBorderColor = indicatorBorderColor,
        highlightColor = highlightColor,
        lowlightColor = lowlightColor,
    )

    @Composable
    private fun defaultContainerColor(): Color = when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
        KedgeStyle.Miuix -> MiuixTheme.colorScheme.surfaceContainer.copy(alpha = 0.58f)
    }

    @Composable
    private fun defaultContentColor(): Color = when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.onSurfaceVariant
        KedgeStyle.Miuix -> MiuixTheme.colorScheme.onSurface
    }

    @Composable
    private fun defaultSelectedContentColor(): Color = when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.primary
        KedgeStyle.Miuix -> MiuixTheme.colorScheme.primary
    }

    @Composable
    private fun defaultIndicatorColor(): Color = when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        KedgeStyle.Miuix -> MiuixTheme.colorScheme.primary.copy(alpha = 0.18f)
    }
}

private fun Modifier.liquidGlassSurface(colors: KedgeLiquidGlassNavigationBarColors): Modifier =
    drawWithCache {
        val topHighlight = Brush.verticalGradient(
            colors = listOf(colors.highlightColor, Color.Transparent),
            startY = 0f,
            endY = size.height * 0.62f,
        )
        val bottomLowlight = Brush.verticalGradient(
            colors = listOf(Color.Transparent, colors.lowlightColor),
            startY = size.height * 0.45f,
            endY = size.height,
        )
        val sideGlow = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
            center = Offset(size.width * 0.22f, size.height * 0.08f),
            radius = size.maxDimension * 0.9f,
        )
        onDrawWithContent {
            drawContent()
            drawRect(topHighlight)
            drawRect(bottomLowlight)
            drawRect(sideGlow)
        }
    }

private fun Modifier.liquidGlassIndicator(colors: KedgeLiquidGlassNavigationBarColors): Modifier =
    drawWithCache {
        val highlight = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.30f), Color.Transparent),
            startY = 0f,
            endY = size.height * 0.75f,
        )
        val depth = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.08f)),
            center = Offset(size.width * 0.5f, size.height * 1.15f),
            radius = size.maxDimension,
        )
        onDrawWithContent {
            drawContent()
            drawRect(highlight)
            drawRect(depth)
        }
    }

private fun Modifier.kedgeDrawGlassBackdrop(
    backdrop: Backdrop,
    shape: Shape,
    blurRadius: Dp,
    highlight: Highlight,
    onDrawSurface: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit,
): Modifier = drawBackdrop(
    backdrop = backdrop,
    shape = { shape },
    effects = {
        blur(blurRadius.toPx(), blurRadius.toPx())
    },
    highlight = { highlight },
    onDrawSurface = onDrawSurface,
)

private val KedgeGlassContainerHighlight: Highlight = Highlight(
    width = 1.dp,
    alpha = 0.78f,
    style = BloomStroke(
        color = Color.White.copy(alpha = 0.12f),
        innerBlurRadius = 2.dp,
        primaryLight = LightSource(
            position = LightPosition(0.5f, -0.25f, -0.05f),
            color = Color.White,
            intensity = 1f,
        ),
        secondaryLight = LightSource(
            position = LightPosition(0.5f, 0.85f, -0.5f),
            color = Color.White,
            intensity = 0.45f,
        ),
        dualPeak = true,
    ),
)

private val KedgeGlassIndicatorHighlight: Highlight = KedgeGlassContainerHighlight.copy(alpha = 0.92f)
