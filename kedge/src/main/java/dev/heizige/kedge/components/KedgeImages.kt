package dev.heizige.kedge.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun KedgeAppIconImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: Shape = RoundedCornerShape(12.dp),
    backgroundColor: Color = kedgeImageContainerColor(),
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun KedgeIconImage(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconPadding: PaddingValues = PaddingValues(12.dp),
    shape: Shape = RoundedCornerShape(12.dp),
    backgroundColor: Color = kedgeImageContainerColor(),
    tint: Color = kedgeImageContentColor(),
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.padding(iconPadding),
            tint = tint,
        )
    }
}

@Composable
fun KedgeAvatarImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    KedgeAppIconImage(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        size = size,
        shape = RoundedCornerShape(size / 2),
    )
}

@Composable
private fun kedgeImageContainerColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.surfaceVariant
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.secondaryContainer
}

@Composable
private fun kedgeImageContentColor(): Color = when (LocalKedgeStyle.current) {
    KedgeStyle.MD3Exp -> MaterialTheme.colorScheme.primary
    KedgeStyle.Miuix -> MiuixTheme.colorScheme.primary
}
