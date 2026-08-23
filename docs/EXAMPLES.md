# 示例用法

完整 showcase 位于 `kedge/src/main/java/dev/heizige/kedge/sample/KedgeUsageExample.kt`。本文档只列出最常用的接入片段。

## 1. 主题与 Toast Host

```kotlin
@Composable
fun AppRoot(style: KedgeStyle) {
    KedgeTheme(style = style) {
        KedgeToastHost()
        AppContent()
    }
}
```

`KedgeToastHost()` 应放在 app 根部一次。业务层触发 Toast 推荐使用 `rememberKedgeToastController()`。

## 2. 自适应 Scaffold

```kotlin
enum class Destination { Home, Search, Settings }

@Composable
fun HomeShell() {
    var selected by rememberSaveable { mutableStateOf(Destination.Home) }
    val items = listOf(
        KedgeNavigationItem(
            key = Destination.Home,
            label = "Home",
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Rounded.Home,
        ),
        KedgeNavigationItem(
            key = Destination.Search,
            label = "Search",
            icon = Icons.Outlined.Search,
            selectedIcon = Icons.Rounded.Search,
        ),
    )

    KedgeAdaptiveScaffold(
        selectedItem = selected,
        onItemSelected = { selected = it },
        navigationItems = items,
    ) {
        ScreenContent(selected)
    }
}
```

MD3Exp 分支使用 Material3 `NavigationSuiteScaffold`。Miuix 分支按窗口宽度切换底栏和侧栏。

## 3. 原生搜索框桥接

```kotlin
@Composable
fun SearchExample() {
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    KedgeSearchBar(
        value = query,
        onValueChange = { query = it },
        active = active,
        onActiveChange = { active = it },
        placeholder = "Search components",
        modifier = Modifier.fillMaxWidth(),
    )
}
```

- `KedgeStyle.MD3Exp`：内部桥接 Material3 原生 `SearchBar`。
- `KedgeStyle.Miuix`：内部桥接 Miuix 原生 `SearchBar` / `InputField`。

## 4. MD3 Expressive 按钮形变

```kotlin
val shapes = KedgeButtonDefaults.md3ButtonShapes(
    shape = RoundedCornerShape(22.dp),
    pressedShape = RoundedCornerShape(8.dp),
)

KedgeButton(
    onClick = {},
    shapes = shapes,
) {
    Text("Morph")
}
```

Miuix 分支会忽略 Material3 专属 `ButtonShapes`，继续使用 Miuix 原生按钮。

## 5. 20dp MD3 输入框

```kotlin
KedgeTextField(
    value = name,
    onValueChange = { name = it },
    label = "Name",
    singleLine = true,
)
```

MD3Exp 默认 shape 是 `RoundedCornerShape(20.dp)`；Miuix 分支使用 Miuix 输入框样式。

## 6. Toast-only 反馈

```kotlin
@Composable
fun SaveButton() {
    val toast = rememberKedgeToastController()

    KedgeButton(onClick = { toast.show("Saved", Icons.Rounded.Check) }) {
        Text("Save")
    }
}
```

- MD3Exp：调用 Khromia 原生 Toast。
- Miuix：调用 Khromia custom Toast content，并套 Miuix 色板。

## 7. 液态玻璃导航

```kotlin
@Composable
fun GlassNavigationExample() {
    var selected by rememberSaveable { mutableStateOf(Destination.Home) }
    val items = rememberNavigationItems()

    KedgeLiquidGlassBackdrop(modifier = Modifier.fillMaxSize()) { backdrop ->
        Box(Modifier.fillMaxSize()) {
            ScreenContent(selected)

            KedgeLiquidGlassNavigationBar(
                selectedItem = selected,
                onItemSelected = { selected = it },
                navigationItems = items,
                backdrop = backdrop,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
```

`KedgeLiquidGlassBackdrop` 负责捕获背景层，`KedgeLiquidGlassNavigationBar` 使用 `drawBackdrop` 和 `blur` 进行毛玻璃绘制。

## 8. 设置项

```kotlin
KedgePreferenceCategory("General")

KedgeSwitchPreference(
    title = "Enable feature",
    summary = "Whole row toggles the switch",
    checked = enabled,
    onCheckedChange = { enabled = it },
)

KedgeListPreference(
    title = "Mode",
    selectedValue = mode,
    options = listOf(
        KedgePreferenceOption("balanced", "Balanced"),
        KedgePreferenceOption("performance", "Performance"),
    ),
    onValueSelected = { mode = it },
)
```

MD3Exp 分支优先复用 Khromia 的 `OptionItem` / `ButtonOption` / `OptionSwitch`，Miuix 分支使用 HyperOS 风格行布局。
