package dev.heizige.kedge.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.components.KedgeRadioButton
import dev.heizige.kedge.components.KedgeSwitch
import dev.heizige.kedge.overlays.KedgeDialog
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
import heizige.kk.khromia.components.ButtonOption
import heizige.kk.khromia.components.OptionItem
import heizige.kk.khromia.text.OptionsText
import top.yukonga.miuix.kmp.basic.SmallTitle as MiuixSmallTitle
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class KedgePreferenceOption<T>(
    val value: T,
    val label: String,
    val summary: String? = null,
)

@Composable
fun KedgePreferenceCategory(
    title: String,
    modifier: Modifier = Modifier,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> androidx.compose.foundation.layout.Box(
            modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            OptionsText(title)
        }

        KedgeStyle.Miuix -> MiuixSmallTitle(
            text = title,
            modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )
    }
}

@Composable
fun KedgeTextPreference(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> if (icon != null) {
            ButtonOption(
                modifier = modifier,
                imageVector = icon,
                title = title,
                subtitle = summary,
                onClick = onClick,
            )
        } else {
            KedgePreferenceRow(
                modifier = modifier,
                title = title,
                summary = summary,
                icon = null,
                onClick = onClick,
            )
        }

        KedgeStyle.Miuix -> KedgePreferenceRow(
            modifier = modifier,
            title = title,
            summary = summary,
            icon = icon,
            onClick = onClick,
        )
    }
}

@Composable
fun KedgeSwitchPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> if (icon != null) {
            OptionItem(
                modifier = modifier,
                imageVector = icon,
                title = title,
                subtitle = summary,
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        } else {
            KedgePreferenceRow(
                modifier = modifier,
                title = title,
                summary = summary,
                icon = null,
                enabled = enabled,
                onClick = { if (enabled) onCheckedChange(!checked) },
                trailing = {
                    KedgeSwitch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        enabled = enabled,
                    )
                },
            )
        }

        KedgeStyle.Miuix -> KedgePreferenceRow(
            modifier = modifier,
            title = title,
            summary = summary,
            icon = icon,
            enabled = enabled,
            onClick = { if (enabled) onCheckedChange(!checked) },
            trailing = {
                KedgeSwitch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    enabled = enabled,
                )
            },
        )
    }
}

@Composable
fun <T> KedgeListPreference(
    title: String,
    options: List<KedgePreferenceOption<T>>,
    selectedValue: T,
    onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = options.firstOrNull { it.value == selectedValue }?.label,
    icon: ImageVector? = null,
) {
    var showDialog by remember { mutableStateOf(false) }

    KedgeArrowPreference(
        title = title,
        modifier = modifier,
        summary = summary,
        icon = icon,
        onClick = { showDialog = true },
    )

    KedgeDialog(
        show = showDialog,
        onDismissRequest = { showDialog = false },
        title = title,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            options.forEach { option ->
                KedgePreferenceRow(
                    title = option.label,
                    summary = option.summary,
                    onClick = {
                        onValueSelected(option.value)
                        showDialog = false
                    },
                    trailing = {
                        KedgeRadioButton(
                            selected = option.value == selectedValue,
                            onClick = {
                                onValueSelected(option.value)
                                showDialog = false
                            },
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun KedgeArrowPreference(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    if (LocalKedgeStyle.current == KedgeStyle.MD3Exp && icon != null) {
        KedgePreferenceRow(
            modifier = modifier,
            title = title,
            summary = summary,
            icon = icon,
            onClick = onClick,
            trailing = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
        )
        return
    }

    KedgePreferenceRow(
        modifier = modifier,
        title = title,
        summary = summary,
        icon = icon,
        onClick = onClick,
        trailing = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = if (LocalKedgeStyle.current == KedgeStyle.Miuix) {
                    MiuixTheme.colorScheme.onSurfaceVariantSummary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        },
    )
}

@Composable
private fun KedgePreferenceRow(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val isMiuix = LocalKedgeStyle.current == KedgeStyle.Miuix
    val background = if (isMiuix) {
        MiuixTheme.colorScheme.surfaceContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f)
    }
    val titleColor = if (isMiuix) MiuixTheme.colorScheme.onSurfaceContainer else MaterialTheme.colorScheme.onSurface
    val summaryColor = if (isMiuix) MiuixTheme.colorScheme.onSurfaceVariantSummary else MaterialTheme.colorScheme.onSurfaceVariant
    val iconTint = if (isMiuix) MiuixTheme.colorScheme.onSurfaceContainerVariant else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(enabled = enabled && onClick != null) { onClick?.invoke() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (isMiuix) MiuixTheme.colorScheme.secondaryVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f),
                        shape = CircleShape,
                    )
                    .padding(8.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            if (isMiuix) {
                MiuixText(text = title, color = titleColor)
                if (summary != null) MiuixText(text = summary, color = summaryColor)
            } else {
                Text(text = title, color = titleColor, style = MaterialTheme.typography.bodyLarge)
                if (summary != null) Text(text = summary, color = summaryColor, style = MaterialTheme.typography.bodySmall)
            }
        }

        if (trailing != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailing()
        }
    }
}
