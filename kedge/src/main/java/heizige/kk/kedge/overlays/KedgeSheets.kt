package heizige.kk.kedge.overlays

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import heizige.kk.kedge.components.KedgeCard
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.window.WindowBottomSheet as MiuixWindowBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KedgeModalBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!show) return

    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = onDismissRequest,
                modifier = modifier,
                sheetState = sheetState,
            ) {
                KedgeCard {
                    Column(modifier = Modifier.imePadding()) {
                        content()
                    }
                }
            }
        }

        KedgeStyle.Miuix -> MiuixWindowBottomSheet(
            show = show,
            modifier = modifier,
            title = title,
            onDismissRequest = onDismissRequest,
        ) {
            Column { content() }
        }
    }
}

@Composable
fun KedgeBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    KedgeModalBottomSheet(
        show = show,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = title,
        content = content,
    )
}
