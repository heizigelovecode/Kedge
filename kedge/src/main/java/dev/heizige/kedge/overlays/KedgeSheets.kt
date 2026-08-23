package dev.heizige.kedge.overlays

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.heizige.kedge.components.KedgeCard
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
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
        KedgeStyle.MD3Exp -> ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
        ) {
            KedgeCard { content() }
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
