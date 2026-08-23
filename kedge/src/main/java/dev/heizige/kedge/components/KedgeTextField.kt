package dev.heizige.kedge.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor as MdLocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TextField as MiuixTextField
import top.yukonga.miuix.kmp.basic.TextFieldDefaults as MiuixTextFieldDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme

object KedgeTextFieldDefaults {
    val Md3Shape: Shape = RoundedCornerShape(20.dp)
}

@Composable
fun KedgeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    supportingText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = KedgeTextFieldDefaults.Md3Shape,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
            supportingText = supportingText?.let { { Text(it) } },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            shape = shape,
        )

        KedgeStyle.Miuix -> KedgeMiuixTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            label = label,
            placeholder = placeholder,
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            supportingText = supportingText,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
        )
    }
}

@Composable
fun KedgeOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    supportingText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = KedgeTextFieldDefaults.Md3Shape,
) {
    KedgeTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        supportingText = supportingText,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        shape = shape,
    )
}

@Composable
private fun KedgeMiuixTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    label: String?,
    placeholder: String?,
    enabled: Boolean,
    readOnly: Boolean,
    isError: Boolean,
    supportingText: String?,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    singleLine: Boolean,
    minLines: Int,
    maxLines: Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation,
    interactionSource: MutableInteractionSource?,
) {
    val helperText = supportingText
    val labelText = label ?: placeholder.orEmpty()
    val helperColor = if (isError) MiuixTheme.colorScheme.error else MiuixTheme.colorScheme.onSurfaceVariantSummary
    val fieldContentColor = if (enabled) {
        MiuixTheme.colorScheme.onSecondaryContainer
    } else {
        MiuixTheme.colorScheme.disabledOnSurface
    }

    Column(modifier = modifier) {
        CompositionLocalProvider(MdLocalContentColor provides fieldContentColor) {
            MiuixTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = MiuixTextFieldDefaults.textFieldColors(
                    borderColor = if (isError) MiuixTheme.colorScheme.error else MiuixTheme.colorScheme.primary,
                ),
                label = labelText,
                useLabelAsPlaceholder = label == null && placeholder != null,
                enabled = enabled,
                readOnly = readOnly,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = singleLine,
                minLines = minLines,
                maxLines = maxLines,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
            )
        }

        if (helperText != null) {
            Spacer(modifier = Modifier.height(6.dp))
            MiuixText(
                text = helperText,
                color = helperColor,
                style = MiuixTheme.textStyles.footnote1,
            )
        }
    }
}
