package dev.heizige.kedge.components

import androidx.compose.material3.Checkbox as MdCheckbox
import androidx.compose.material3.RadioButton as MdRadioButton
import androidx.compose.material3.Switch as MdSwitch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
import heizige.kk.khromia.components.OptionSwitch
import top.yukonga.miuix.kmp.basic.Checkbox as MiuixCheckbox
import top.yukonga.miuix.kmp.basic.RadioButton as MiuixRadioButton
import top.yukonga.miuix.kmp.basic.Switch as MiuixSwitch

@Composable
fun KedgeSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> OptionSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled,
        )

        KedgeStyle.Miuix -> MiuixSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled,
        )
    }
}

@Composable
fun KedgeCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled,
        )

        KedgeStyle.Miuix -> MiuixCheckbox(
            state = if (checked) ToggleableState.On else ToggleableState.Off,
            onClick = onCheckedChange?.let { { it(!checked) } },
            modifier = modifier,
            enabled = enabled,
        )
    }
}

@Composable
fun KedgeRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdRadioButton(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
        )

        KedgeStyle.Miuix -> MiuixRadioButton(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
        )
    }
}
