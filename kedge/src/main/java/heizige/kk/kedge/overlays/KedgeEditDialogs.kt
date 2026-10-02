package heizige.kk.kedge.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.components.KedgeButton
import heizige.kk.kedge.components.KedgeButtonVariant
import heizige.kk.kedge.components.KedgeTextButton
import heizige.kk.kedge.components.KedgeTextField
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import heizige.kk.kedge.theme.MiuixTextStyleScope
import heizige.kk.khromia.R as KhromiaR
import heizige.kk.khromia.components.EditDialog as KhromiaEditDialog
import heizige.kk.khromia.components.EditFieldConfig
import heizige.kk.khromia.components.validateEditFields
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog as MiuixWindowDialog

/**
 * 多字段编辑弹窗的双风格分发：MD3Exp 走 Khromia `EditDialog`，Miuix 走 Miuix 实现。
 *
 * 与 [KedgeAlertDialog] / [KedgeAlertDialogSlots] 同一层分发，调用点无需关心风格。
 *
 * [EditFieldConfig] 与校验规则（[validateEditFields]）都直接复用 Khromia 的，**不要
 * 在这里重新定义一份**：两边类型不通的话，调用点准备好的 `fields` 就没法传进来，
 * 校验 bug 也会要修两处。
 *
 * [visible] 转 `false` 会先播完退场动画再退出组合，所以调用点要**始终调用本组件**
 * 并用 [visible] 控制显隐，不要写 `if (show) KedgeEditDialog(visible = true, ...)`。
 *
 * @param onDismiss 退场动画播完后回调，两种关闭路径（内部取消 / 调用点置
 *   [visible] 为 `false`）都会走它。
 */
@Composable
fun KedgeEditDialog(
    visible: Boolean,
    title: String,
    fields: List<EditFieldConfig>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String? = null,
    dismissText: String? = null,
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> KhromiaEditDialog(
            visible = visible,
            title = title,
            fields = fields,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            modifier = modifier,
            confirmText = confirmText,
            dismissText = dismissText,
        )

        KedgeStyle.Miuix -> MiuixEditDialog(
            visible = visible,
            title = title,
            fields = fields,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            modifier = modifier,
            confirmText = confirmText,
            dismissText = dismissText,
        )
    }
}

/**
 * [KedgeEditDialog] 的 Miuix 分支，私有。
 *
 * 与 Khromia `EditDialog` 的差异只在控件与形状：
 * - 宿主走 Miuix [MiuixWindowDialog]（自带 scrim 与 dismiss），而不是 Khromia 自有的
 *   `FullscreenPopup`；
 * - 输入框用 [KedgeTextField]，确认/取消用 [KedgeButton] / [KedgeTextButton]；
 * - 圆角与内边距交给 Miuix 的 DialogDefaults，**不沿用** MD3 的 28dp / 16dp。
 *
 * 校验规则不复制，直接调 Khromia 的 [validateEditFields]。
 */
@Composable
private fun MiuixEditDialog(
    visible: Boolean,
    title: String,
    fields: List<EditFieldConfig>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit,
    modifier: Modifier,
    confirmText: String?,
    dismissText: String?,
) {
    if (!visible) return

    val actualConfirmText = confirmText ?: stringResource(KhromiaR.string.edit_dialog_confirm)
    val actualDismissText = dismissText ?: stringResource(KhromiaR.string.edit_dialog_cancel)
    val errInvalidNumber = stringResource(KhromiaR.string.edit_dialog_error_invalid_number)
    val errRangeTemplate = stringResource(KhromiaR.string.edit_dialog_error_range)
    val errMaxLengthTemplate = stringResource(KhromiaR.string.edit_dialog_error_max_length)

    // 与 Khromia EditDialog 同一个坑：不 key 在 fields 上。fields 每次重组都是新的
    // list（onValidate 是捕获 lambda、引用每次都变），key 在它上面会让父层任意一次
    // 重组都清空用户输入。
    val initialValues = remember(fields) { fields.map { it.initialValue } }
    val values = remember(visible, initialValues) {
        mutableStateListOf<String>().apply { addAll(initialValues) }
    }

    val errorMessages = validateEditFields(
        fields = fields,
        values = values,
        invalidNumberError = errInvalidNumber,
        rangeErrorTemplate = errRangeTemplate,
        maxLengthErrorTemplate = errMaxLengthTemplate,
    )
    val isAllValid = errorMessages.all { it == null }

    // MiuixWindowDialog 自己管显隐动画（show=false 时播 tween(260) 的退场，
    // 播完才把内部 visible 置 false），所以这里**不能**像 Khromia 那样自己判断
    // 卸载，否则 visible 一变 false 组件就 return，260ms 退场一帧都播不出来。
    // 只在退场确实结束后才把本组件移出组合。
    var exitDone by remember { mutableStateOf(false) }
    LaunchedEffect(visible) { exitDone = false }
    if (!visible && exitDone) return

    MiuixWindowDialog(
        show = visible,
        title = title,
        modifier = modifier,
        // 这里是「请求关闭」，与 MD3 分支的 onDismiss（退场后回调）时机不同，
        // 但对调用点效果一致：都是把 visible 置 false。KedgeEditDialog 的 KDoc 已写明。
        onDismissRequest = onDismiss,
        onDismissFinished = { exitDone = !visible },
    ) {
        Column {
            fields.forEachIndexed { index, config ->
                val error = errorMessages.getOrNull(index)
                KedgeTextField(
                    value = values[index],
                    onValueChange = { values[index] = it },
                    label = config.label,
                    placeholder = config.placeholder.ifEmpty { null },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = config.keyboardType,
                    ),
                    isError = error != null,
                    // Miuix 的 TextField 没有 supportingText 槽，错误文案跟在输入框
                    // 下面单独一行。放在 MiuixTextStyleScope 里，裸 MiuixText 也能拿到
                    // Miuix 字阶而不是漏回 MD3 字号。
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                if (error != null) {
                    MiuixTextStyleScope(MiuixTheme.textStyles.body2) {
                        MiuixText(
                            text = error,
                            color = MiuixTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            MiuixTextStyleScope(MiuixTheme.textStyles.body2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    KedgeTextButton(onClick = onDismiss) {
                        MiuixText(actualDismissText)
                    }
                    KedgeButton(
                        onClick = { onConfirm(values.toList()) },
                        variant = KedgeButtonVariant.Primary,
                        enabled = isAllValid,
                    ) {
                        MiuixText(actualConfirmText)
                    }
                }
            }
        }
    }
}