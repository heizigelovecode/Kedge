package dev.heizige.kedge.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.heizige.kedge.adaptive.KedgeAdaptiveScaffold
import dev.heizige.kedge.adaptive.KedgeBottomBar
import dev.heizige.kedge.adaptive.KedgeBreadcrumbBar
import dev.heizige.kedge.adaptive.KedgeBreadcrumbItem
import dev.heizige.kedge.adaptive.KedgeLargeTopAppBar
import dev.heizige.kedge.adaptive.KedgeLiquidGlassBackdrop
import dev.heizige.kedge.adaptive.KedgeLiquidGlassNavigationBar
import dev.heizige.kedge.adaptive.KedgeNavigationBar
import dev.heizige.kedge.adaptive.KedgeNavigationItem
import dev.heizige.kedge.adaptive.KedgeNavigationRail
import dev.heizige.kedge.adaptive.KedgeTopAppBar
import dev.heizige.kedge.components.KedgeBadge
import dev.heizige.kedge.components.KedgeBadgedBox
import dev.heizige.kedge.components.KedgeButton
import dev.heizige.kedge.components.KedgeButtonDefaults
import dev.heizige.kedge.components.KedgeButtonVariant
import dev.heizige.kedge.components.KedgeCard
import dev.heizige.kedge.components.KedgeCheckbox
import dev.heizige.kedge.components.KedgeColumnScope
import dev.heizige.kedge.components.KedgeIconButton
import dev.heizige.kedge.components.KedgeIconButtonVariant
import dev.heizige.kedge.components.KedgeIconImage
import dev.heizige.kedge.components.KedgeOutlinedTextField
import dev.heizige.kedge.components.KedgeRadioButton
import dev.heizige.kedge.components.KedgeRangeSlider
import dev.heizige.kedge.components.KedgeRowScope
import dev.heizige.kedge.components.KedgeSearchBar
import dev.heizige.kedge.components.KedgeSegmentedCheckboxItem
import dev.heizige.kedge.components.KedgeSegmentedList
import dev.heizige.kedge.components.KedgeSegmentedListItem
import dev.heizige.kedge.components.KedgeSegmentedRadioItem
import dev.heizige.kedge.components.KedgeSegmentedSwitchItem
import dev.heizige.kedge.components.KedgeSegmentedTextField
import dev.heizige.kedge.components.KedgeSlider
import dev.heizige.kedge.components.KedgeStatusLevel
import dev.heizige.kedge.components.KedgeStatusTag
import dev.heizige.kedge.components.KedgeSurface
import dev.heizige.kedge.components.KedgeSwitch
import dev.heizige.kedge.components.KedgeTag
import dev.heizige.kedge.components.KedgeTabItem
import dev.heizige.kedge.components.KedgeTabRow
import dev.heizige.kedge.components.KedgeTextButton
import dev.heizige.kedge.components.KedgeTextField
import dev.heizige.kedge.components.KedgeToggleButton
import dev.heizige.kedge.components.KedgeWarningCard
import dev.heizige.kedge.overlays.KedgeAlertDialog
import dev.heizige.kedge.overlays.KedgeBottomSheet
import dev.heizige.kedge.overlays.KedgeDialog
import dev.heizige.kedge.overlays.KedgeDialogHost
import dev.heizige.kedge.overlays.KedgeDropdownMenu
import dev.heizige.kedge.overlays.KedgeListPopup
import dev.heizige.kedge.overlays.KedgeMenuItem
import dev.heizige.kedge.overlays.KedgeModalBottomSheet
import dev.heizige.kedge.overlays.KedgeProgressIndicator
import dev.heizige.kedge.overlays.KedgeProgressIndicatorType
import dev.heizige.kedge.overlays.KedgeToastHost
import dev.heizige.kedge.overlays.KedgeToastPill
import dev.heizige.kedge.overlays.rememberKedgeDialogHostState
import dev.heizige.kedge.overlays.rememberKedgeToastController
import dev.heizige.kedge.preferences.KedgeArrowPreference
import dev.heizige.kedge.preferences.KedgeListPreference
import dev.heizige.kedge.preferences.KedgePreferenceCategory
import dev.heizige.kedge.preferences.KedgePreferenceOption
import dev.heizige.kedge.preferences.KedgeSwitchPreference
import dev.heizige.kedge.preferences.KedgeTextPreference
import dev.heizige.kedge.theme.KedgeStyle
import dev.heizige.kedge.theme.KedgeTheme

private enum class DemoDestination {
    Home,
    Search,
    Settings,
}

@Composable
fun KedgeUsageExample(
    style: KedgeStyle = KedgeStyle.MD3Exp,
) {
    var selectedDestination by rememberSaveable { mutableStateOf(DemoDestination.Home) }
    val navigationItems = rememberDemoNavigationItems()

    KedgeTheme(style = style) {
        KedgeToastHost()
        KedgeAdaptiveScaffold(
            selectedItem = selectedDestination,
            onItemSelected = { selectedDestination = it },
            navigationItems = navigationItems,
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                KedgeShowcaseContent(
                    selectedDestination = selectedDestination,
                    onDestinationSelected = { selectedDestination = it },
                    navigationItems = navigationItems,
                )
            }
        }
    }
}

@Composable
private fun rememberDemoNavigationItems(): List<KedgeNavigationItem<DemoDestination>> = listOf(
    KedgeNavigationItem(
        key = DemoDestination.Home,
        label = "Home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Rounded.Home,
    ),
    KedgeNavigationItem(
        key = DemoDestination.Search,
        label = "Search",
        icon = Icons.Outlined.Search,
        selectedIcon = Icons.Rounded.Search,
        badge = { KedgeBadge { Text("3") } },
    ),
    KedgeNavigationItem(
        key = DemoDestination.Settings,
        label = "Settings",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Rounded.Settings,
    ),
)

@Composable
private fun KedgeShowcaseContent(
    selectedDestination: DemoDestination,
    onDestinationSelected: (DemoDestination) -> Unit,
    navigationItems: List<KedgeNavigationItem<DemoDestination>>,
) {
    var query by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("Multiline notes") }
    var enabled by remember { mutableStateOf(true) }
    var checked by remember { mutableStateOf(true) }
    var radio by remember { mutableStateOf("Miuix") }
    var slider by remember { mutableFloatStateOf(0.42f) }
    var rangeStart by remember { mutableFloatStateOf(0.2f) }
    var rangeEnd by remember { mutableFloatStateOf(0.8f) }
    var selectedMode by remember { mutableStateOf("Balanced") }
    var selectedTab by remember { mutableStateOf("Apps") }
    var toggleChecked by remember { mutableStateOf(true) }
    var segmentedText by remember { mutableStateOf("Kedge") }
    var showDialog by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var showModalSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showListPopup by remember { mutableStateOf(false) }
    val toast = rememberKedgeToastController()
    val dialogHostState = rememberKedgeDialogHostState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ShowcaseSection("Scaffold & Navigation") {
            KedgeTopAppBar(
                title = "Kedge",
                subtitle = "Unified Android UI bridge",
                navigationIcon = {
                    KedgeIconButton(onClick = {}, variant = KedgeIconButtonVariant.Standard) {
                        Icon(Icons.Rounded.Home, contentDescription = null)
                    }
                },
                actions = {
                    KedgeIconButton(onClick = {}, selected = true, variant = KedgeIconButtonVariant.Tonal) {
                        Icon(Icons.Rounded.Notifications, contentDescription = null)
                    }
                },
            )
            KedgeLargeTopAppBar(
                title = "Large Title",
                subtitle = "MD3Exp large app bar / Miuix immersive title",
                navigationIcon = {
                    KedgeIconButton(onClick = {}) {
                        Icon(Icons.Rounded.Folder, contentDescription = null)
                    }
                },
            )
            KedgeBreadcrumbBar(
                items = listOf(
                    KedgeBreadcrumbItem("Root", Icons.Outlined.Folder),
                    KedgeBreadcrumbItem("Kedge"),
                    KedgeBreadcrumbItem("Showcase"),
                ),
            )
            KedgeNavigationBar(
                selectedItem = selectedDestination,
                onItemSelected = onDestinationSelected,
                navigationItems = navigationItems,
            )
            KedgeBottomBar(
                selectedItem = selectedDestination,
                onItemSelected = onDestinationSelected,
                navigationItems = navigationItems,
            )
            KedgeLiquidGlassBackdrop(modifier = Modifier.fillMaxWidth()) { backdrop ->
                KedgeLiquidGlassNavigationBar(
                    selectedItem = selectedDestination,
                    onItemSelected = onDestinationSelected,
                    navigationItems = navigationItems,
                    backdrop = backdrop,
                )
            }
            KedgeSurface(modifier = Modifier.height(180.dp)) {
                KedgeNavigationRail(
                    selectedItem = selectedDestination,
                    onItemSelected = onDestinationSelected,
                    navigationItems = navigationItems,
                    expanded = true,
                )
            }
        }

        ShowcaseSection("Buttons & Inputs") {
            val md3MorphShapes = KedgeButtonDefaults.md3ButtonShapes(
                shape = RoundedCornerShape(22.dp),
                pressedShape = RoundedCornerShape(8.dp),
            )
            val md3IconMorphShapes = KedgeButtonDefaults.md3IconButtonShapes(
                shape = RoundedCornerShape(50),
                pressedShape = RoundedCornerShape(12.dp),
            )

            KedgeRowScope {
                KedgeButton(onClick = {}, variant = KedgeButtonVariant.Primary) { Text("Primary") }
                KedgeButton(onClick = {}, variant = KedgeButtonVariant.Secondary) { Text("Secondary") }
                KedgeTextButton(onClick = {}) { Text("Text") }
            }
            KedgeRowScope {
                KedgeButton(onClick = {}, shapes = md3MorphShapes) { Text("Morph") }
                KedgeButton(
                    onClick = {},
                    variant = KedgeButtonVariant.Secondary,
                    shapes = md3MorphShapes,
                ) {
                    Text("Tonal Morph")
                }
                KedgeTextButton(onClick = {}, shapes = md3MorphShapes) { Text("Text Morph") }
            }
            KedgeRowScope {
                KedgeIconButton(onClick = {}, variant = KedgeIconButtonVariant.Standard) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
                KedgeIconButton(onClick = {}, variant = KedgeIconButtonVariant.Filled) {
                    Icon(Icons.Rounded.Check, contentDescription = null)
                }
                KedgeIconButton(onClick = {}, variant = KedgeIconButtonVariant.Tonal, selected = true) {
                    Icon(Icons.Rounded.Favorite, contentDescription = null)
                }
            }
            KedgeRowScope {
                KedgeIconButton(
                    onClick = {},
                    variant = KedgeIconButtonVariant.Standard,
                    shapes = md3IconMorphShapes,
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
                KedgeIconButton(
                    onClick = {},
                    variant = KedgeIconButtonVariant.Filled,
                    shapes = md3IconMorphShapes,
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null)
                }
                KedgeIconButton(
                    onClick = {},
                    variant = KedgeIconButtonVariant.Tonal,
                    shapes = md3IconMorphShapes,
                ) {
                    Icon(Icons.Rounded.Favorite, contentDescription = null)
                }
            }
            KedgeTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = "Search",
                placeholder = "Type something",
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            )
            KedgeOutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                label = "Notes",
                supportingText = "Outlined alias uses the same Kedge contract",
                minLines = 2,
            )
            KedgeSearchBar(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Search components",
            )
        }

        ShowcaseSection("Tabs & Segmented Lists") {
            KedgeTabRow(
                selectedItem = selectedTab,
                onItemSelected = { selectedTab = it },
                items = listOf(
                    KedgeTabItem("Apps", "Apps", Icons.Rounded.Home),
                    KedgeTabItem("Logs", "Logs", Icons.Rounded.Search),
                    KedgeTabItem("Settings", "Settings", Icons.Rounded.Settings),
                ),
            )
            KedgeToggleButton(checked = toggleChecked, onCheckedChange = { toggleChecked = it }) {
                Text("Toggle")
            }
            KedgeSegmentedList(title = "Segmented settings") {
                item {
                    KedgeSegmentedListItem(
                        title = "Text row",
                        summary = "Animated segmented container",
                        icon = Icons.Rounded.Star,
                    )
                }
                item {
                    KedgeSegmentedSwitchItem(
                        title = "Switch row",
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                        icon = Icons.Rounded.Settings,
                    )
                }
                item {
                    KedgeSegmentedCheckboxItem(
                        title = "Checkbox row",
                        checked = checked,
                        onCheckedChange = { checked = it },
                        icon = Icons.Rounded.Check,
                    )
                }
                item {
                    KedgeSegmentedRadioItem(
                        title = "Radio row",
                        selected = radio == "Kedge",
                        onClick = { radio = "Kedge" },
                        icon = Icons.Rounded.Favorite,
                    )
                }
                item {
                    KedgeSegmentedTextField(
                        title = "Inline input",
                        value = segmentedText,
                        onValueChange = { segmentedText = it },
                        placeholder = "Name",
                        icon = Icons.Rounded.Search,
                    )
                }
            }
        }

        ShowcaseSection("Selection Controls") {
            KedgeRowScope {
                KedgeSwitch(checked = enabled, onCheckedChange = { enabled = it })
                KedgeCheckbox(checked = checked, onCheckedChange = { checked = it })
                KedgeRadioButton(selected = radio == "MD3Exp", onClick = { radio = "MD3Exp" })
                KedgeRadioButton(selected = radio == "Miuix", onClick = { radio = "Miuix" })
            }
            KedgeSlider(
                value = slider,
                onValueChange = { slider = it },
                modifier = Modifier.fillMaxWidth(),
            )
            KedgeRangeSlider(
                value = rangeStart..rangeEnd,
                onValueChange = {
                    rangeStart = it.start
                    rangeEnd = it.endInclusive
                },
                modifier = Modifier.fillMaxWidth(),
            )
            KedgeRowScope {
                KedgeBadge()
                KedgeBadge { Text("9") }
                KedgeBadgedBox(badge = { KedgeBadge { Text("2") } }) {
                    Icon(Icons.Rounded.Notifications, contentDescription = null)
                }
                KedgeTag(text = "Stable")
                KedgeTag(
                    text = "Clickable",
                    leadingIcon = { Icon(Icons.Rounded.Star, contentDescription = null) },
                    onClick = {},
                )
            }
        }

        ShowcaseSection("Containers") {
            KedgeSurface(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text("KedgeSurface")
                }
            }
            KedgeCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
                Text("KedgeCard")
                Text("MD3 card vs Miuix block container")
            }
            KedgeColumnScope(modifier = Modifier.fillMaxWidth()) {
                Text("KedgeColumnScope item A")
                Text("KedgeColumnScope item B")
            }
            KedgeRowScope(modifier = Modifier.fillMaxWidth()) {
                Text("KedgeRowScope")
                KedgeTag("Inline")
                KedgeIconImage(Icons.Rounded.Star, contentDescription = null)
            }
        }

        ShowcaseSection("Status") {
            KedgeRowScope {
                KedgeStatusTag("Info", level = KedgeStatusLevel.Info)
                KedgeStatusTag("Notice", level = KedgeStatusLevel.Notice)
                KedgeStatusTag("Error", level = KedgeStatusLevel.Error)
            }
            KedgeWarningCard(
                message = "WarningCard maps semantic colors by style.",
                level = KedgeStatusLevel.Warning,
            )
        }

        ShowcaseSection("Dialogs & Feedback") {
            KedgeRowScope {
                KedgeButton(onClick = { showDialog = true }) { Text("Dialog") }
                KedgeButton(onClick = { showAlert = true }) { Text("Alert") }
                KedgeButton(onClick = { showSheet = true }) { Text("Sheet") }
                KedgeButton(onClick = { showModalSheet = true }) { Text("Modal") }
                KedgeButton(onClick = { toast.show("Toast event", Icons.Rounded.Check) }) { Text("Toast") }
            }
            KedgeRowScope {
                Box {
                    KedgeButton(onClick = { showMenu = true }) { Text("Menu") }
                    KedgeDropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        items = listOf(
                            KedgeMenuItem("Refresh", Icons.Rounded.Search) { toast.show("Refresh") },
                            KedgeMenuItem("Delete", Icons.Rounded.Close, destructive = true) { toast.show("Delete", isError = true) },
                        ),
                    )
                }
                KedgeButton(onClick = { showListPopup = true }) { Text("List Popup") }
                KedgeButton(
                    onClick = {
                        dialogHostState.showConfirm(
                            title = "Hosted dialog",
                            message = "DialogHost controls confirm/loading dialogs.",
                            onConfirm = { toast.show("Confirmed") },
                        )
                    },
                ) { Text("Host") }
            }
            KedgeToastPill(message = "Toast pill preview", icon = Icons.Rounded.Check)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KedgeProgressIndicator(type = KedgeProgressIndicatorType.Circular)
                KedgeProgressIndicator(type = KedgeProgressIndicatorType.Infinite)
            }
            KedgeProgressIndicator(
                type = KedgeProgressIndicatorType.Linear,
                progress = slider,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        ShowcaseSection("Preferences") {
            KedgePreferenceCategory("General")
            KedgeTextPreference(
                title = "Text preference",
                summary = "Opens a detail screen",
                icon = Icons.Outlined.Info,
                onClick = {},
            )
            KedgeSwitchPreference(
                title = "Switch preference",
                summary = "Whole row toggles the switch",
                icon = Icons.Outlined.Notifications,
                checked = enabled,
                onCheckedChange = { enabled = it },
            )
            KedgeListPreference(
                title = "List preference",
                summary = selectedMode,
                icon = Icons.Outlined.Settings,
                options = listOf(
                    KedgePreferenceOption("Compact", "Compact"),
                    KedgePreferenceOption("Balanced", "Balanced"),
                    KedgePreferenceOption("Expanded", "Expanded"),
                ),
                selectedValue = selectedMode,
                onValueSelected = { selectedMode = it },
            )
            KedgeArrowPreference(
                title = "Arrow preference",
                summary = "Navigate deeper",
                icon = Icons.Outlined.Folder,
                onClick = {},
            )
        }
    }

    KedgeDialog(
        show = showDialog,
        onDismissRequest = { showDialog = false },
        title = "KedgeDialog",
        summary = "A shared dialog shell",
    ) {
        Text("Dialog content can use any Kedge component.")
        Spacer(modifier = Modifier.height(12.dp))
        KedgeButton(onClick = { showDialog = false }) { Text("Close") }
    }

    KedgeAlertDialog(
        show = showAlert,
        onDismissRequest = { showAlert = false },
        title = "KedgeAlertDialog",
        text = "Confirm and dismiss actions are mapped by style.",
        confirmText = "Confirm",
        onConfirm = { showAlert = false },
        dismissText = "Cancel",
        onDismiss = { showAlert = false },
    )

    KedgeBottomSheet(
        show = showSheet,
        onDismissRequest = { showSheet = false },
        title = "KedgeBottomSheet",
    ) {
        Text("Bottom sheet content")
        KedgeButton(onClick = { showSheet = false }) { Text("Done") }
    }

    KedgeModalBottomSheet(
        show = showModalSheet,
        onDismissRequest = { showModalSheet = false },
        title = "KedgeModalBottomSheet",
    ) {
        Text("Modal bottom sheet content")
        KedgeButton(onClick = { showModalSheet = false }) { Text("Done") }
    }

    KedgeListPopup(
        show = showListPopup,
        onDismissRequest = { showListPopup = false },
        title = "KedgeListPopup",
        items = listOf(
            KedgeMenuItem("Compact", Icons.Rounded.Home) { selectedMode = "Compact" },
            KedgeMenuItem("Balanced", Icons.Rounded.Star) { selectedMode = "Balanced" },
            KedgeMenuItem("Expanded", Icons.Rounded.Settings) { selectedMode = "Expanded" },
        ),
    )

    KedgeDialogHost(state = dialogHostState)
}

@Composable
private fun ShowcaseSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 1200)
@Composable
fun KedgeMd3Preview() {
    KedgeUsageExample(style = KedgeStyle.MD3Exp)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 1200)
@Composable
fun KedgeMiuixPreview() {
    KedgeUsageExample(style = KedgeStyle.Miuix)
}

@Preview(showBackground = true, widthDp = 900, heightDp = 900)
@Composable
fun KedgeTabletPreview() {
    KedgeUsageExample(style = KedgeStyle.Miuix)
}
