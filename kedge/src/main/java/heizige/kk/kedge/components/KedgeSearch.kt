package heizige.kk.kedge.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar as MdSearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.adaptive.KedgeTopAppBar
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.InputField as MiuixSearchInputField
import top.yukonga.miuix.kmp.basic.SearchBar as MiuixSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KedgeSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    active: Boolean = false,
    onActiveChange: ((Boolean) -> Unit)? = null,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val actualTrailingIcon = trailingIcon ?: if (value.isNotEmpty()) {
        {
            IconButton(onClick = { onValueChange("") }) {
                Icon(Icons.Rounded.Close, contentDescription = null)
            }
        }
    } else {
        null
    }

    val horizontalPadding = when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> 4.dp
        else -> 16.dp
    }

    // The MD3 collapsed field sits flush against whatever is above it, so give it the
    // standard 16dp of breathing room on top; the Miuix bar carries its own spacing.
    val topPadding = when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> 0.dp
        else -> 16.dp
    }

    // The caller modifier is consumed exactly once, by the outer box; the inner bars fill it.
    Box(
        modifier = modifier
            .padding(top = topPadding)
            .padding(horizontal = horizontalPadding),
    ) {
        when (LocalKedgeStyle.current) {
            KedgeStyle.MD3Exp -> Md3SearchBarBridge(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                active = active,
                onActiveChange = onActiveChange,
                enabled = enabled,
                trailingIcon = actualTrailingIcon,
            )

            KedgeStyle.Miuix -> MiuixSearchBar(
                inputField = {
                    MiuixSearchInputField(
                        query = value,
                        onQueryChange = onValueChange,
                        onSearch = { onActiveChange?.invoke(false) },
                        expanded = active,
                        onExpandedChange = { onActiveChange?.invoke(it) },
                        label = placeholder,
                        enabled = enabled,
                        trailingIcon = actualTrailingIcon,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                expanded = active,
                onExpandedChange = { onActiveChange?.invoke(it) },
                modifier = Modifier.fillMaxWidth(),
                content = {},
            )
        }
    }
}

/**
 * MD3 bridge for [KedgeSearchBar] built on the current search APIs
 * (`rememberSearchBarState` + `SearchBarDefaults.InputField`). Unlike the deprecated
 * query-based `SearchBar`, the collapsed field carries no default status-bar window
 * insets, so no phantom gap appears above it. The expanded full-screen view provides
 * the collapse path (back handling) once the user focuses the field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Md3SearchBarBridge(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    active: Boolean,
    onActiveChange: ((Boolean) -> Unit)?,
    enabled: Boolean,
    trailingIcon: @Composable (() -> Unit)?,
) {
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState(value)
    val latestValue by rememberUpdatedState(value)
    val latestOnActiveChange by rememberUpdatedState(onActiveChange)

    // Text field -> external value.
    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .collect { text ->
                if (text != latestValue) onValueChange(text)
            }
    }
    // External value -> text field.
    LaunchedEffect(value) {
        if (textFieldState.text.toString() != value) {
            textFieldState.setTextAndPlaceCursorAtEnd(value)
        }
    }

    // External active flag drives the expansion state.
    LaunchedEffect(active) {
        when {
            active && searchBarState.targetValue != SearchBarValue.Expanded ->
                searchBarState.animateToExpanded()

            !active && searchBarState.targetValue != SearchBarValue.Collapsed ->
                searchBarState.animateToCollapsed()
        }
    }
    // Internal expansion (user tapping the field) reports back through onActiveChange.
    LaunchedEffect(searchBarState) {
        snapshotFlow { searchBarState.targetValue }
            .drop(1)
            .collect { target ->
                latestOnActiveChange?.invoke(target == SearchBarValue.Expanded)
            }
    }

    val inputField: @Composable () -> Unit = {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
            enabled = enabled,
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = trailingIcon,
        )
    }

    MdSearchBar(
        state = searchBarState,
        inputField = inputField,
        modifier = Modifier.fillMaxWidth(),
    )
    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField,
        content = {},
    )
}

@Composable
fun KedgeSearchTopAppBar(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    searchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    placeholder: String = "Search",
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    defaultContent: @Composable BoxScope.() -> Unit = {},
    searchContent: @Composable BoxScope.() -> Unit = {},
) {
    Column(modifier = modifier) {
        KedgeTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = {
                IconButton(onClick = { onSearchActiveChange(true) }) {
                    Icon(Icons.Rounded.Search, contentDescription = null)
                }
                actions()
            },
        )
        AnimatedVisibility(visible = searchActive) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                KedgeSearchBar(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = placeholder,
                    active = true,
                    onActiveChange = onSearchActiveChange,
                )
                Box(modifier = Modifier.fillMaxWidth()) { searchContent() }
            }
        }
        AnimatedVisibility(visible = !searchActive) {
            Box(modifier = Modifier.fillMaxWidth()) { defaultContent() }
        }
    }
}
