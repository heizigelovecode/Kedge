package heizige.kk.kedge.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor as MdLocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TextField as MiuixTextField
import top.yukonga.miuix.kmp.basic.TextFieldDefaults as MiuixTextFieldDefaults
import top.yukonga.miuix.kmp.theme.LocalContentColor as MiuixLocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme

object KedgeTextFieldDefaults {
    val Md3Shape: Shape = RoundedCornerShape(16.dp)
}

/**
 * 把外部的 `String` 受控值接成 [TextFieldValue]，**保住 IME 合成区与光标位置**。
 *
 * 为什么必须持有 [TextFieldValue] 而不是直接传字符串：
 * Compose 的 `BasicTextField(value: String, …)` 重载每次重组都会用
 * `TextFieldValue(value)` 重建一个新值，而这个构造函数的默认值是
 * `selection = TextRange(value.length)`、**合成区为 null**。中文输入法打字期间 IME 侧
 * 一直维护着拼音合成区，每敲一个字母就丢一次：拼音和已经上屏的字会一起进框，光标还被
 * 复位到开头（用户实测反馈）。
 *
 * 所以打字过程中原样收下 IME 给的 [TextFieldValue]（合成区、光标都保住），只有外部
 * 真的换成了另一个字符串（清空、换模型、重置表单）时才重建。
 */
@Composable
internal fun rememberKedgeTextFieldValue(value: String): MutableState<TextFieldValue> {
    val state = remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    if (state.value.text != value) {
        state.value = TextFieldValue(value, TextRange(value.length))
    }
    return state
}

/**
 * 当前「表单行」的标题，由外层行容器（KhatKit 的 `KedgeFormRow`）下发。
 *
 * MD3 的表单习惯是「标题在上、输入框在下且输入框自己不带标题」，这套结构搬到
 * Miuix 下会变成一个没有标题的空输入框。这里把行标题透给输入框，让它在**框内**
 * 显示（KSU 的 `SuperTextField` 就是这么做的）。
 */
val LocalKedgeRowLabel = staticCompositionLocalOf<(@Composable () -> Unit)?> { null }

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
    /** 文字样式：MD3 原样应用；Miuix 只取字号/字重，其余用 Miuix 的样式。 */
    textStyle: androidx.compose.ui.text.TextStyle? = null,
    /**
     * MD3 配色。**只在 MD3Exp 分支生效**；Miuix 走自己的配色，这个参数传进来只是为了
     * 让 MD3 调用点（`OutlinedTextFieldDefaults.colors(...)`）能原样换成 Kedge
     * 组件而不丢自定义配色。
     */
    colors: TextFieldColors? = null,
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
            textStyle = textStyle ?: androidx.compose.material3.LocalTextStyle.current,
            colors = colors ?: OutlinedTextFieldDefaults.colors(),
        )

        KedgeStyle.Miuix -> {
            // 没有自己的 label 时回退到「行标题」（由 KhatKit 的 KedgeFormRow 下发），
            // 让它显示在输入框**框内**，而不是变成一个没有标题的空框。
            val rowLabel = LocalKedgeRowLabel.current
            val showLabelInside = label == null && rowLabel != null && value.isEmpty()
            KedgeMiuixTextField(
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
            textStyle = textStyle,
            insideLabel = if (showLabelInside) rowLabel else null,
        )
        }
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
    textStyle: androidx.compose.ui.text.TextStyle? = null,
    colors: TextFieldColors? = null,
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
        textStyle = textStyle,
        colors = colors,
    )
}

/**
 * slot 版 [KedgeOutlinedTextField]：MD3 的 `label` / `placeholder` /
 * `supportingText` 是 `@Composable () -> Unit`，而 Miuix 的 `TextField` 只收
 * `String`。这里给 slot 版本，让 MD3 调用点可以整体换成 Kedge 组件：
 *
 * 名字带 `WithSlots` 而不是重载：两个重载的参数只在 label 类型上不同，调用方
 * 一旦不传 label 就会歧义。
 *
 * - MD3Exp：原样透传给 MD3 `OutlinedTextField`，行为完全不变；
 * - Miuix：slot 内容**渲染到输入框外面**（label 在上、helper 在下）。
 *   Miuix 的浮动 label 需要 String，抽不出 slot 里的文本，所以退化成上下两行，
 *   语义和内容都在，只是位置跟 MD3 不同。
 *
 * slot 里通常是 `Text(stringResource(...))`，Miuix 下依旧走 MD3 `Text`，
 * 但颜色由 `MiuixMaterialBridge` 桥接过来，观感不会退回 MD3 紫/蓝。
 */
@Composable
fun KedgeOutlinedTextFieldWithSlots(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = KedgeTextFieldDefaults.Md3Shape,
    textStyle: androidx.compose.ui.text.TextStyle? = null,
    colors: TextFieldColors? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            label = label,
            placeholder = placeholder,
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
            colors = colors ?: OutlinedTextFieldDefaults.colors(),
        )

        KedgeStyle.Miuix -> KedgeMiuixTextFieldWithSlots(
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
            textStyle = textStyle,
        )
    }
}

/**
 * slot 版 [KedgeTextField]，语义同 [KedgeOutlinedTextFieldWithSlots]
 * （MD3 分支走 `TextField`，Miuix 分支把 label/supportingText 渲染到输入框外）。
 */
@Composable
fun KedgeTextFieldWithSlots(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = KedgeTextFieldDefaults.Md3Shape,
    textStyle: androidx.compose.ui.text.TextStyle? = null,
    colors: TextFieldColors? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            label = label,
            placeholder = placeholder,
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
            colors = colors ?: OutlinedTextFieldDefaults.colors(),
        )

        KedgeStyle.Miuix -> KedgeMiuixTextFieldWithSlots(
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
            textStyle = textStyle,
        )
    }
}

/**
 * Miuix 分支的 slot 版输入框：label 渲染在输入框上方、supportingText 在下方。
 * placeholder 在 Miuix 下没有对应位置（label 已占上方），忽略。
 */
@Composable
private fun KedgeMiuixTextFieldWithSlots(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    label: (@Composable () -> Unit)?,
    placeholder: (@Composable () -> Unit)?,
    enabled: Boolean,
    readOnly: Boolean,
    isError: Boolean,
    supportingText: (@Composable () -> Unit)?,
    leadingIcon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?,
    singleLine: Boolean,
    minLines: Int,
    maxLines: Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation,
    interactionSource: MutableInteractionSource?,
    textStyle: androidx.compose.ui.text.TextStyle? = null,
) {
    val effectiveTextStyle = if (textStyle == null) {
        MiuixTheme.textStyles.main
    } else {
        MiuixTheme.textStyles.main.copy(
            fontSize = textStyle.fontSize,
            fontWeight = textStyle.fontWeight,
            letterSpacing = textStyle.letterSpacing,
        )
    }
    @Suppress("UNUSED_EXPRESSION")
    placeholder
    // 照搬 KernelSU（ui/component/miuix/EditText.kt + SuperSearchBar.kt）：
    // 输入框是 surfaceContainerHigh 的块面，文字用 onSurface。之前这里用
    // onSecondaryContainer，在本仓 TonalSpot 配色下与 secondaryContainer 底色
    // 明度太近，深色模式下看着是「一块灰底上的灰字」（用户截图反馈）。
    val fieldContentColor = if (enabled) {
        MiuixTheme.colorScheme.onSurface
    } else {
        MiuixTheme.colorScheme.disabledOnSurface
    }
    val showLabelInside = label != null && value.isEmpty()
    // 持有 TextFieldValue 而不是直接传字符串，否则中文输入法的拼音合成区每次按键都被
    // 重组冲掉（详见 [rememberKedgeTextFieldValue]）。
    val textFieldValue = rememberKedgeTextFieldValue(value)
    Column(modifier = modifier) {
        // label 有值时**不**再渲染到框外面：Miuix 原生 TextField 的 label 是画在
        // 输入框边框内的，悬在外面会变成「标题 + 一个空灰框」，和 KernelSU 差很远。
        // 这里把 label slot 叠到框内左上角，空值时当占位标题用。
        if (label != null && !showLabelInside) {
            label()
            Spacer(modifier = Modifier.height(4.dp))
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            CompositionLocalProvider(
                // Miuix 的 TextField 自己 provide 的是 **Miuix 的** LocalContentColor；
                // 只给 MD3 那个等于没给（之前的 onSurface 改动因此看不到效果）。
                // 两个都 provide：内部文字走 Miuix 的，MD3 插槽（如 trailing icon
                // 里的 Text）走 MD3 的。
                MdLocalContentColor provides fieldContentColor,
                MiuixLocalContentColor provides fieldContentColor,
            ) {
                MiuixTextField(
                    value = textFieldValue.value,
                    onValueChange = { textFieldValue.value = it; onValueChange(it.text) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = MiuixTextFieldDefaults.textFieldColors(
                        // 底色照搬 KernelSU（ui/component/miuix/EditText.kt）：输入框是
                        // surfaceContainerHigh 的块面，不描边。Miuix 默认的
                        // secondaryContainer 在本仓 TonalSpot 配色下偏亮，字和底色
                        // 明度太近（用户截图里是「灰底上的灰字」）。
                        backgroundColor = if (enabled) {
                            MiuixTheme.colorScheme.surfaceContainerHigh
                        } else {
                            MiuixTheme.colorScheme.surfaceContainer
                        },
                        labelColor = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        borderColor = if (isError) {
                            MiuixTheme.colorScheme.error
                        } else {
                            MiuixTheme.colorScheme.primary
                        },
                    ),
                    label = "",
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
                    textStyle = effectiveTextStyle,
                    cornerRadius = 16.dp,
                )
            }
            if (showLabelInside) {
                // label 是 slot，直接在框内左上角渲染调用方给的内容
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 10.dp),
                ) {
                    CompositionLocalProvider(
                        androidx.compose.material3.LocalTextStyle provides
                            MiuixTheme.textStyles.body2,
                    ) {
                        label()
                    }
                }
            }
        }
        if (supportingText != null) {
            Spacer(modifier = Modifier.height(6.dp))
            supportingText()
        }
    }
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
    textStyle: androidx.compose.ui.text.TextStyle? = null,
    insideLabel: (@Composable () -> Unit)? = null,
) {
    val effectiveTextStyle = if (textStyle == null) {
        MiuixTheme.textStyles.main
    } else {
        MiuixTheme.textStyles.main.copy(
            fontSize = textStyle.fontSize,
            fontWeight = textStyle.fontWeight,
            letterSpacing = textStyle.letterSpacing,
        )
    }
    val helperText = supportingText
    // insideLabel 由调用方算好（label 为空且有行标题、且当前值为空时传入），
    // 这里只负责把它叠在输入框内；没有它就按原来的 label/placeholder 逻辑。
    val labelText = when {
        label != null -> label
        insideLabel != null -> ""
        else -> placeholder.orEmpty()
    }
    val helperColor = if (isError) MiuixTheme.colorScheme.error else MiuixTheme.colorScheme.onSurfaceVariantSummary
    // 照搬 KernelSU（ui/component/miuix/EditText.kt + SuperSearchBar.kt）：
    // 输入框是 surfaceContainerHigh 的块面，文字用 onSurface。之前这里用
    // onSecondaryContainer，在本仓 TonalSpot 配色下与 secondaryContainer 底色
    // 明度太近，深色模式下看着是「一块灰底上的灰字」（用户截图反馈）。
    val fieldContentColor = if (enabled) {
        MiuixTheme.colorScheme.onSurface
    } else {
        MiuixTheme.colorScheme.disabledOnSurface
    }
    // 同 [KedgeMiuixTextFieldWithSlots]：传 String 会丢 IME 合成区。
    val textFieldValue = rememberKedgeTextFieldValue(value)

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            CompositionLocalProvider(
                // Miuix 的 TextField 自己 provide 的是 **Miuix 的** LocalContentColor；
                // 只给 MD3 那个等于没给（之前的 onSurface 改动因此看不到效果）。
                // 两个都 provide：内部文字走 Miuix 的，MD3 插槽（如 trailing icon
                // 里的 Text）走 MD3 的。
                MdLocalContentColor provides fieldContentColor,
                MiuixLocalContentColor provides fieldContentColor,
            ) {
                MiuixTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    colors = MiuixTextFieldDefaults.textFieldColors(
                        // 底色照搬 KernelSU（ui/component/miuix/EditText.kt）：输入框是
                        // surfaceContainerHigh 的块面，不描边。Miuix 默认的
                        // secondaryContainer 在本仓 TonalSpot 配色下偏亮，字和底色
                        // 明度太近（用户截图里是「灰底上的灰字」）。
                        backgroundColor = if (enabled) {
                            MiuixTheme.colorScheme.surfaceContainerHigh
                        } else {
                            MiuixTheme.colorScheme.surfaceContainer
                        },
                        labelColor = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        borderColor = if (isError) {
                            MiuixTheme.colorScheme.error
                        } else {
                            MiuixTheme.colorScheme.primary
                        },
                    ),
                    label = labelText,
                    useLabelAsPlaceholder = label == null && insideLabel == null && placeholder != null,
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
                    textStyle = effectiveTextStyle,
                    cornerRadius = 16.dp,
                )
            }
            if (insideLabel != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 10.dp),
                ) {
                    CompositionLocalProvider(
                        androidx.compose.material3.LocalTextStyle provides
                            MiuixTheme.textStyles.body2,
                    ) {
                        insideLabel()
                    }
                }
            }
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
/**
 * 基于 `TextFieldState` 的文本框，**按风格分流**。
 *
 * 与 [KedgeTextField] 的区别：状态由调用方用 `rememberTextFieldState()` 持有
 * （而不是 value/onValueChange 一对），适合代码编辑器这类需要靠 `TextFieldState`
 * 读值、并且会在重组外读当前文本的场景。
 *
 * Miuix 的 `TextField` 同样有接收 `TextFieldState` + `TextFieldLineLimits` 的重载，
 * 所以两个风格的行数限制语义能保持一致。
 */
@Composable
fun KedgeTextFieldWithState(
    state: androidx.compose.foundation.text.input.TextFieldState,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    lineLimits: androidx.compose.foundation.text.input.TextFieldLineLimits? = null,
    /** 占位文案。Miuix 的 TextField 只收字符串占位，所以这里也用 String。 */
    placeholder: String? = null,
    /** 文字样式：MD3 原样应用；Miuix 只取字号/字重，其余用 Miuix 的样式。 */
    textStyle: androidx.compose.ui.text.TextStyle? = null,
    shape: androidx.compose.ui.graphics.Shape = KedgeTextFieldDefaults.Md3Shape,
    /** MD3 配色，只在 MD3Exp 分支生效。 */
    colors: androidx.compose.material3.TextFieldColors? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> androidx.compose.material3.TextField(
            state = state,
            modifier = modifier,
            readOnly = readOnly,
            enabled = enabled,
            lineLimits = lineLimits ?: androidx.compose.foundation.text.input.TextFieldLineLimits.SingleLine,
            placeholder = placeholder?.let { p -> { Text(p) } },
            textStyle = textStyle ?: androidx.compose.material3.LocalTextStyle.current,
            shape = shape,
            colors = colors ?: androidx.compose.material3.TextFieldDefaults.colors(),
        )

        KedgeStyle.Miuix -> top.yukonga.miuix.kmp.basic.TextField(
            state = state,
            modifier = modifier,
            readOnly = readOnly,
            enabled = enabled,
            // Miuix 的 lineLimits 是非空参数，没传时按多行处理（编辑器场景）。
            lineLimits = lineLimits ?: androidx.compose.foundation.text.input.TextFieldLineLimits.MultiLine(),
            // Miuix 的 label 是非空 String，用空串代表"无标签"，再靠
            // useLabelAsPlaceholder 让它只在空值时显示成占位。
            label = placeholder.orEmpty(),
            useLabelAsPlaceholder = placeholder != null,
            textStyle = textStyle ?: MiuixTheme.textStyles.main,
        )
    }
}
