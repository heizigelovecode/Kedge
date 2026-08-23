package dev.heizige.kedge.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Stable
class KedgeDialogHostState internal constructor() {
    internal var currentDialog by mutableStateOf<KedgeHostedDialog?>(null)
        private set

    fun showLoading(title: String, message: String? = null) {
        currentDialog = KedgeHostedDialog.Loading(title = title, message = message)
    }

    fun showConfirm(
        title: String,
        message: String? = null,
        confirmText: String = "OK",
        dismissText: String? = "Cancel",
        onConfirm: () -> Unit,
    ) {
        currentDialog = KedgeHostedDialog.Confirm(
            title = title,
            message = message,
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = onConfirm,
        )
    }

    fun dismiss() {
        currentDialog = null
    }
}

sealed interface KedgeHostedDialog {
    data class Loading(val title: String, val message: String?) : KedgeHostedDialog
    data class Confirm(
        val title: String,
        val message: String?,
        val confirmText: String,
        val dismissText: String?,
        val onConfirm: () -> Unit,
    ) : KedgeHostedDialog
}

@Composable
fun rememberKedgeDialogHostState(): KedgeDialogHostState = remember { KedgeDialogHostState() }

@Composable
fun KedgeDialogHost(
    state: KedgeDialogHostState,
    modifier: Modifier = Modifier,
) {
    when (val dialog = state.currentDialog) {
        is KedgeHostedDialog.Loading -> KedgeLoadingDialog(
            show = true,
            title = dialog.title,
            message = dialog.message,
            modifier = modifier,
        )

        is KedgeHostedDialog.Confirm -> KedgeAlertDialog(
            show = true,
            onDismissRequest = state::dismiss,
            title = dialog.title,
            text = dialog.message,
            confirmText = dialog.confirmText,
            onConfirm = {
                dialog.onConfirm()
                state.dismiss()
            },
            dismissText = dialog.dismissText,
            onDismiss = state::dismiss,
        )

        null -> Unit
    }
}

@Composable
fun KedgeLoadingDialog(
    show: Boolean,
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    KedgeDialog(show = show, onDismissRequest = {}, modifier = modifier, title = title, summary = message) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KedgeProgressIndicator(type = KedgeProgressIndicatorType.Circular)
            if (message != null) Text(message)
        }
    }
}
