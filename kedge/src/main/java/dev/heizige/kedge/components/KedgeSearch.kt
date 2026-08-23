package dev.heizige.kedge.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar as MdSearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.adaptive.KedgeTopAppBar
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.LocalKedgeStyle
import top.yukonga.miuix.kmp.basic.InputField as MiuixSearchInputField
import top.yukonga.miuix.kmp.basic.SearchBar as MiuixSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
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

    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> MdSearchBar(
            query = value,
            onQueryChange = onValueChange,
            onSearch = { onActiveChange?.invoke(false) },
            active = active,
            onActiveChange = { onActiveChange?.invoke(it) },
            modifier = modifier,
            enabled = enabled,
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = actualTrailingIcon,
            content = {},
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
                    trailingIcon = trailingIcon,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            expanded = active,
            onExpandedChange = { onActiveChange?.invoke(it) },
            modifier = modifier,
            content = {},
        )
    }
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
