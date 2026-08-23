# 主题与样式

Kedge 的主题层负责把业务层的一套组件调用分发到 MD3Exp 或 Miuix 渲染分支。

## KedgeStyle

```kotlin
enum class KedgeStyle {
    MD3Exp,
    Miuix,
}
```

- `MD3Exp`：Material 3 / Material 3 Expressive 风格。优先复用 Khromia 的 MD3Exp 组件和动效，缺失组件回退官方 Material 3。
- `Miuix`：MIUI / HyperOS 风格。优先使用 `top.yukonga.miuix.kmp`，缺失组件由 Kedge 用 Miuix token 组合实现。

## KedgeTheme

```kotlin
KedgeTheme(
    style = KedgeStyle.MD3Exp,
    darkTheme = isSystemInDarkTheme(),
    dynamicColor = style == KedgeStyle.MD3Exp,
) {
    AppContent()
}
```

参数说明：

| 参数 | 说明 |
|---|---|
| `style` | 当前 Kedge 风格。 |
| `darkTheme` | 是否使用深色模式。 |
| `dynamicColor` | MD3Exp 默认开启动态色；Miuix 默认关闭，显式开启后使用 Monet mode。 |
| `md3ColorScheme` | 可选 Material3 `ColorScheme`，用于完全接管 MD3Exp 配色。 |
| `miuixSeedColor` | 可选 Miuix key color。 |

## CompositionLocal

组件内部通过 `LocalKedgeStyle.current` 判断当前风格。业务层一般不需要读取它，除非要做非 UI 框架层面的行为切换。

```kotlin
val style = LocalKedgeStyle.current
```

## MD3Exp 样式策略

- `KedgeButton` / `KedgeTextButton` / `KedgeIconButton` 保留 Khromia press motion。
- 按钮支持 Material3 Expressive `shapes` 参数，用于按压时 shape morph。
- `KedgeTextField` 与 `KedgeOutlinedTextField` 的 MD3 默认圆角是 20dp。
- `KedgeAdaptiveScaffold` 使用官方 Material3 `NavigationSuiteScaffold`。

按钮形变示例：

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

图标按钮形变示例：

```kotlin
val iconShapes = KedgeButtonDefaults.md3IconButtonShapes(
    shape = RoundedCornerShape(50),
    pressedShape = RoundedCornerShape(12.dp),
)

KedgeIconButton(
    onClick = {},
    shapes = iconShapes,
) {
    Icon(Icons.Rounded.Check, contentDescription = null)
}
```

## Miuix 样式策略

- 默认关闭动态色，使用 Miuix 原生蓝/灰色板。
- Preference、SegmentedList、Card、Dialog、Toast pill 等组件使用 Miuix color tokens。
- 输入、按钮和导航优先调用 Miuix 官方组件。
- 不要求业务层写任何 `if (style == KedgeStyle.Miuix)` 分支。

Miuix 主题示例：

```kotlin
KedgeTheme(
    style = KedgeStyle.Miuix,
    dynamicColor = false,
) {
    AppContent()
}
```

如需跟随系统 Monet：

```kotlin
KedgeTheme(
    style = KedgeStyle.Miuix,
    dynamicColor = true,
) {
    AppContent()
}
```

## Toast 反馈策略

Kedge 的瞬时反馈统一使用 Khromia Toast。推荐入口是 `rememberKedgeToastController()`：

```kotlin
@Composable
fun SaveButton() {
    val toast = rememberKedgeToastController()

    KedgeButton(onClick = { toast.show("Saved", Icons.Rounded.Check) }) {
        Text("Save")
    }
}
```

- MD3Exp：走 Khromia 原生 `Toast.show(...)`。
- Miuix：走 Khromia `Toast.showCustom { KedgeToastPill(...) }`，只替换成 Miuix 配色。
- App 根部必须放置一次 `KedgeToastHost()`。

```kotlin
KedgeTheme(style = style) {
    KedgeToastHost()
    AppContent()
}
```

## 液态玻璃导航

`KedgeLiquidGlassNavigationBar` 已接入 `miuix-blur-android`：

- floating pill 容器
- 选中项 capsule 动画
- 拖动切换
- `KedgeLiquidGlassBackdropState` 背景采样包装
- `drawBackdrop` + `blur(...)` 毛玻璃绘制
- 高光、半透明和边缘层次
- MD3Exp / Miuix 配色适配

推荐在页面内容外层建立共享 backdrop，再传给导航条：

```kotlin
KedgeLiquidGlassBackdrop(modifier = Modifier.fillMaxSize()) { backdrop ->
    Box(Modifier.fillMaxSize()) {
        ScreenContent()

        KedgeLiquidGlassNavigationBar(
            selectedItem = current,
            onItemSelected = { current = it },
            navigationItems = items,
            backdrop = backdrop,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
```

如果不传 `backdrop`，`KedgeLiquidGlassNavigationBar` 会创建内部 backdrop 作为兼容路径；真实页面级模糊建议显式传入共享 backdrop。KernelSU 的 lens refraction、vibrancy 和 inner shadow 后续可继续作为独立 liquid API 补齐。相关审计见 [KERNELSU_COMPONENT_GAP_AUDIT.md](KERNELSU_COMPONENT_GAP_AUDIT.md)。
