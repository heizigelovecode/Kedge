package heizige.kk.kedge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar as MdSearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import heizige.kk.kedge.adaptive.KedgeScrollBehavior
import heizige.kk.kedge.theme.KedgeStyle
import heizige.kk.kedge.theme.LocalKedgeStyle
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.InputField as MiuixSearchInputField
import top.yukonga.miuix.kmp.basic.SearchBar as MiuixSearchBar
import top.yukonga.miuix.kmp.basic.SmallTopAppBar as MiuixSmallTopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBar as MiuixTopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * App bar with an integrated search field in a two-row layout: the title row on top and a
 * collapsed search bar pinned directly beneath it, with no vertical gap between them.
 *
 * On [KedgeStyle.MD3Exp] this follows the Material search pattern built on
 * [androidx.compose.material3.SearchBarState]: a top app bar plus the new collapsed
 * `SearchBar`, expanding into a full-screen search view via
 * [ExpandedFullScreenContainedSearchBar] while the input field is focused. On
 * [KedgeStyle.Miuix] it renders the stock Miuix top app bar above the stock Miuix
 * `SearchBar`.
 *
 * The query text stays hoisted: [onQueryChange] fires as the user types and external
 * updates to [query] are pushed into the field. Expansion is managed internally;
 * [defaultContent] shows inside the expanded view while the query is empty and
 * [searchContent] once the user typed something. Both receive a [closeSearch] callback
 * that collapses the search view and hides the keyboard.
 *
 * [scrollBehavior] applies to the title row only; attach its nestedScrollConnection to
 * the scrolled page content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KedgeSearchAppBar(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    placeholder: String = "Search",
    enabled: Boolean = true,
    largeTitle: Boolean = false,
    scrollBehavior: KedgeScrollBehavior? = null,
    defaultContent: @Composable (closeSearch: () -> Unit) -> Unit = {},
    searchContent: @Composable (closeSearch: () -> Unit) -> Unit = {},
) {
    when (LocalKedgeStyle.current) {
        KedgeStyle.MD3Exp -> Md3SearchAppBar(
            title = title,
            query = query,
            onQueryChange = onQueryChange,
            modifier = modifier,
            subtitle = subtitle,
            navigationIcon = navigationIcon,
            actions = actions,
            placeholder = placeholder,
            enabled = enabled,
            largeTitle = largeTitle,
            scrollBehavior = scrollBehavior,
            defaultContent = defaultContent,
            searchContent = searchContent,
        )

        KedgeStyle.Miuix -> MiuixSearchAppBar(
            title = title,
            query = query,
            onQueryChange = onQueryChange,
            modifier = modifier,
            subtitle = subtitle,
            navigationIcon = navigationIcon,
            actions = actions,
            placeholder = placeholder,
            enabled = enabled,
            largeTitle = largeTitle,
            defaultContent = defaultContent,
            searchContent = searchContent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Md3SearchAppBar(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier,
    subtitle: String?,
    navigationIcon: (@Composable () -> Unit)?,
    actions: (@Composable RowScope.() -> Unit)?,
    placeholder: String,
    enabled: Boolean,
    largeTitle: Boolean,
    scrollBehavior: KedgeScrollBehavior?,
    defaultContent: @Composable (closeSearch: () -> Unit) -> Unit,
    searchContent: @Composable (closeSearch: () -> Unit) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val searchBarState = rememberContainedSearchBarState()
    val textFieldState = rememberTextFieldState(query)
    val latestQuery by rememberUpdatedState(query)

    // External query -> text field.
    LaunchedEffect(query) {
        if (textFieldState.text.toString() != query) {
            textFieldState.setTextAndPlaceCursorAtEnd(query)
        }
    }
    // Text field -> external query.
    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .collect { text ->
                if (text != latestQuery) onQueryChange(text)
            }
    }

    val closeSearch: () -> Unit = {
        focusManager.clearFocus()
        keyboardController?.hide()
        scope.launch { searchBarState.animateToCollapsed() }
    }

    val inputField: @Composable () -> Unit = {
        val expandedNow = searchBarState.targetValue == SearchBarValue.Expanded
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            onSearch = {
                focusManager.clearFocus()
                keyboardController?.hide()
            },
            enabled = enabled,
            placeholder = { Text(placeholder) },
            leadingIcon = {
                if (expandedNow) {
                    IconButton(onClick = closeSearch) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                } else {
                    Icon(Icons.Rounded.Search, contentDescription = null)
                }
            },
            trailingIcon = if (query.isNotEmpty()) {
                {
                    IconButton(onClick = { textFieldState.setTextAndPlaceCursorAtEnd("") }) {
                        Icon(Icons.Rounded.Close, contentDescription = null)
                    }
                }
            } else {
                null
            },
        )
    }

    Surface(color = MaterialTheme.colorScheme.surfaceContainer, modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val barColors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            )
            val titleInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
            )
            val titleContent: @Composable () -> Unit = { Text(title) }
            if (largeTitle) {
                LargeFlexibleTopAppBar(
                    title = titleContent,
                    subtitle = if (subtitle.isNullOrBlank()) null else {
                        { Text(subtitle) }
                    },
                    navigationIcon = { navigationIcon?.invoke() },
                    actions = { actions?.invoke(this) },
                    colors = barColors,
                    windowInsets = titleInsets,
                    scrollBehavior = scrollBehavior?.material,
                )
            } else {
                TopAppBar(
                    title = titleContent,
                    navigationIcon = { navigationIcon?.invoke() },
                    actions = { actions?.invoke(this) },
                    colors = barColors,
                    windowInsets = titleInsets,
                    scrollBehavior = scrollBehavior?.material,
                )
            }

            // Collapsed search field directly below the title row; horizontal-only insets
            // so no status-bar padding sneaks back in above or below it.
            MdSearchBar(
                state = searchBarState,
                inputField = inputField,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 13.dp),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                ),
            )
        }
    }

    ExpandedFullScreenContainedSearchBar(
        state = searchBarState,
        inputField = inputField,
        windowInsets = {
            SearchBarDefaults.fullScreenWindowInsets.only(
                WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
            )
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (query.isNotEmpty()) searchContent(closeSearch) else defaultContent(closeSearch)
        }
    }
}

@Composable
private fun MiuixSearchAppBar(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier,
    subtitle: String?,
    navigationIcon: (@Composable () -> Unit)?,
    actions: (@Composable RowScope.() -> Unit)?,
    placeholder: String,
    enabled: Boolean,
    largeTitle: Boolean,
    defaultContent: @Composable (closeSearch: () -> Unit) -> Unit,
    searchContent: @Composable (closeSearch: () -> Unit) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var expanded by rememberSaveable { mutableStateOf(false) }
    val closeSearch: () -> Unit = {
        expanded = false
        keyboardController?.hide()
    }
    val clearIcon: (@Composable () -> Unit)? = if (query.isNotEmpty()) {
        {
            IconButton(onClick = { onQueryChange("") }) {
                Icon(Icons.Rounded.Close, contentDescription = null)
            }
        }
    } else {
        null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MiuixTheme.colorScheme.surface),
    ) {
        val barNavigation: @Composable () -> Unit = { navigationIcon?.invoke() }
        if (largeTitle) {
            MiuixTopAppBar(
                title = title,
                largeTitle = title,
                subtitle = subtitle.orEmpty(),
                navigationIcon = barNavigation,
                actions = { actions?.invoke(this) },
            )
        } else {
            MiuixSmallTopAppBar(
                title = title,
                subtitle = subtitle.orEmpty(),
                navigationIcon = barNavigation,
                actions = { actions?.invoke(this) },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 13.dp),
        ) {
            MiuixSearchBar(
                inputField = {
                    MiuixSearchInputField(
                        query = query,
                        onQueryChange = onQueryChange,
                        onSearch = { closeSearch() },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        label = placeholder,
                        enabled = enabled,
                        trailingIcon = clearIcon,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (query.isNotEmpty()) searchContent(closeSearch) else defaultContent(closeSearch)
            }
        }
    }
}
