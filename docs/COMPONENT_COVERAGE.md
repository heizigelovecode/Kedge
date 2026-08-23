# Kedge Component Coverage

Kedge 的目标是用一套业务层 API 覆盖 Android 原生 Compose 的两套视觉体系：

- `KedgeStyle.MD3Exp`: 优先接入 Khromia 已有 MD3Exp 能力；Khromia 没有的组件回退到官方 Material 3 / Material 3 Adaptive。设置项优先复用 Khromia 的 `OptionItem`、`ButtonOption`、`ExpandableOptionItem`、`OptionSwitch`。
- `KedgeStyle.Miuix`: 接入 `top.yukonga.miuix.kmp` 的 MIUI / HyperOS 组件体系；必要时在 Kedge 内补响应式布局与设置项语义。

## Bridge Pattern

每个组件按三层组织：

1. Public Contract: `dev.heizige.kedge.*.KedgeXxx`
2. MD3Exp Implementation: `when (LocalKedgeStyle.current == KedgeStyle.MD3Exp)` 分支，优先 Khromia，其次 Material 3 官方组件
3. Miuix Implementation: `when (LocalKedgeStyle.current == KedgeStyle.Miuix)` 分支，优先 Miuix 官方组件，其次 Kedge 自建 HyperOS 语义组件

公共 API 原则：

- 不向业务层暴露 Material3 / Miuix 专属类型，除非该类型是 Compose 基础类型，例如 `Modifier`、`ImageVector`、`PaddingValues`、`WindowInsets`。
- 业务层只读取 `KedgeStyle`、`KedgeTheme` 与 `KedgeXxx` 组件。
- 样式差异由组件内部消化，不让业务层写 `if (style == ...)`。
- Miuix 分支默认使用 Miuix 原生蓝/灰色板；需要系统 Monet 时由 `KedgeTheme(dynamicColor = true)` 显式开启。

## Status Legend

- Done: 已有可编译第一版
- Partial: 有基础版本，仍需补齐变体或高级行为
- Planned: 尚未实现

## 1. Scaffold & Adaptive Navigation

| Component | Status | MD3Exp Mapping | Miuix / HyperOS Mapping | Notes |
|---|---:|---|---|---|
| `KedgeAdaptiveScaffold` | Done | `NavigationSuiteScaffold` | `Scaffold` + `NavigationBar` / `NavigationRail` | 已按窗口宽度切底栏/侧栏 |
| `KedgeTopAppBar` | Done | `TopAppBar` / `CenterAlignedTopAppBar` | Miuix `SmallTopAppBar` | 统一 title / subtitle / navIcon / actions |
| `KedgeLargeTopAppBar` | Done | `LargeTopAppBar` | Miuix `TopAppBar` large title | 第一版不暴露 scroll behavior |
| `KedgeTopBarBackButton` | Done | Kedge `IconButton` + auto-mirrored back icon | Kedge `IconButton` + auto-mirrored back icon | KernelSU `TopBarBackButton` 通用化 |
| `KedgeBottomBar` | Done | `NavigationBar` | Miuix `NavigationBar` | 非 adaptive 场景底栏 |
| `KedgeNavigationBar` | Done | `NavigationBarItem` | Miuix `NavigationBarItem` | 纯底部导航包装 |
| `KedgeLiquidGlassNavigationBar` / `KedgeLiquidGlassBottomBar` | Done | Kedge glass pill nav + `miuix-blur` backdrop | Kedge HyperOS glass pill nav + `miuix-blur` backdrop | 参考 KernelSU `FloatingBottomBar` / miuix `IosLiquidGlassNavigationBar`，已接 `miuix-blur-android` |
| `KedgeNavigationRail` | Done | `NavigationRail` | Miuix `NavigationRail` + optional state | 第一版支持 header / items |
| `KedgeBreadcrumbBar` | Done | Kedge custom row + MD3 tokens | Kedge custom row + Miuix tokens | Miuix 特色深层路径导航 |
| `KedgeTabRow` | Done | Kedge segmented tabs | Kedge HyperOS segmented tabs | KernelSU `ExpressiveTabRow` 通用化 |

## 2. Atoms & Inputs

| Component | Status | MD3Exp Mapping | Miuix / HyperOS Mapping | Notes |
|---|---:|---|---|---|
| `KedgeButton` | Done | Material3 `Button` / Khromia press motion | Miuix `Button` | 支持 `Primary` / `Secondary` / `Text` 变体 |
| `KedgeTextButton` | Done | `TextButton` | Miuix transparent `Button` | `KedgeButtonVariant.Text` 便捷封装 |
| `KedgeIconButton` | Done | `IconButton` / `FilledIconButton` / `FilledTonalIconButton` | Miuix `IconButton` | 支持 standard / filled / tonal |
| `KedgeTextField` | Done | `OutlinedTextField` | Miuix `TextField` | 支持 String API、label、placeholder、helper text |
| `KedgeOutlinedTextField` | Done | `OutlinedTextField` | Miuix `TextField` + border token | 第一版复用 `KedgeTextField` |
| `KedgeSearchBar` / `KedgeSearchTopAppBar` | Done | Material3 `SearchBar` + top app bar | Miuix `SearchBar` / `InputField` | Kedge 只保留统一契约，不自建搜索框视觉 |
| `KedgeSwitch` | Done | Khromia `OptionSwitch` | Miuix `Switch` | Preference 组件复用 |
| `KedgeCheckbox` | Done | `Checkbox` | Miuix `Checkbox` | Boolean API |
| `KedgeRadioButton` | Done | `RadioButton` | Miuix `RadioButton` | 配合 list preference |
| `KedgeSlider` | Done | `Slider` | Miuix `Slider` | 支持 steps / valueRange |
| `KedgeRangeSlider` | Done | `RangeSlider` | Miuix `RangeSlider` | 支持 closed range API |
| `KedgeToggleButton` / `KedgeSegmentedButton` | Done | Kedge segmented control | Kedge HyperOS segmented control | KernelSU `ExpressiveToggleButton` 通用化 |
| `KedgeBadge` | Done | `Badge` / `BadgedBox` | Miuix `Badge` / `BadgedBox` | `KedgeBadgedBox` 同步实现 |
| `KedgeTag` | Done | `AssistChip` | Kedge HyperOS pill | 轻量标签，不等同 Badge |
| `KedgeStatusTag` | Done | Kedge semantic pill | Kedge semantic pill | Error / Notice / Success 等语义状态 |

## 3. Containers & Cards

| Component | Status | MD3Exp Mapping | Miuix / HyperOS Mapping | Notes |
|---|---:|---|---|---|
| `KedgeSurface` | Done | `Surface` | Miuix `Surface` | 统一容器色、shape、elevation |
| `KedgeCard` | Done | `Card` | Miuix `Card` | 支持 optional click |
| `KedgeWarningCard` | Done | Kedge semantic surface | Kedge semantic surface | KernelSU `WarningCard` 通用化 |
| `KedgeAppIconImage` / `KedgeIconImage` / `KedgeAvatarImage` | Done | Kedge image container | Kedge image container + Miuix colors | KernelSU `AppIconImage` 通用化 |
| `KedgeColumnScope` | Done | `Column` + default spacing | `Column` + default spacing | 用于设置页/表单页默认间距 |
| `KedgeRowScope` | Done | `Row` + default spacing | `Row` + default spacing | 用于列表项、工具条和卡片内容 |

## 4. Dialogs & Overlays

| Component | Status | MD3Exp Mapping | Miuix / HyperOS Mapping | Notes |
|---|---:|---|---|---|
| `KedgeDialog` | Done | `Dialog` + `KedgeCard` | Miuix `WindowDialog` | 统一弹出层容器 |
| `KedgeAlertDialog` | Done | `AlertDialog` | Miuix `WindowDialog` + Kedge buttons | 统一 confirm / dismiss / title / text |
| `KedgeDialogHost` / `KedgeLoadingDialog` | Done | Host-state dialog renderer | Host-state dialog renderer | KernelSU handle-style dialog 控制通用化 |
| `KedgeBottomSheet` | Done | `ModalBottomSheet` | Miuix `WindowBottomSheet` | 第一版复用 modal 入口 |
| `KedgeModalBottomSheet` | Done | `ModalBottomSheet` | Miuix `WindowBottomSheet` | 统一 show / dismiss contract |
| `KedgeDropdownMenu` / `KedgeDropdownItem` / `KedgeListPopup` | Done | Material popup menu | Popup/list menu + Miuix color adaptation | KernelSU menu/list popup 通用化 |
| `KedgeToastHost` / `KedgeToast` / `rememberKedgeToastController` / `KedgeToastPill` | Done | Khromia Toast host + helper | Khromia Toast host + Miuix-colored custom toast | 事件提示统一用 Toast；Miuix 只做配色适配 |
| `KedgeProgressIndicator` | Done | `LinearProgressIndicator` / `CircularProgressIndicator` | Miuix `LinearProgressIndicator` / `CircularProgressIndicator` / `InfiniteProgressIndicator` | 统一 linear/circular/infinite mode |

## 5. Preferences & Lists

| Component | Status | MD3Exp Mapping | Miuix / HyperOS Mapping | Notes |
|---|---:|---|---|---|
| `KedgePreferenceCategory` | Done | Khromia `OptionsText` | Miuix `SmallTitle` | 设置分组标题 |
| `KedgeTextPreference` | Done | Khromia `ButtonOption` / custom fallback | Miuix preference row custom | 标准文本设置项 |
| `KedgeSwitchPreference` | Done | Khromia `OptionItem(checked, onCheckedChange)` + fallback | Miuix preference row + `Switch` | 点击整行切换 |
| `KedgeListPreference` | Done | Dialog + `RadioButton` | Miuix dialog + `RadioButton` | 单选设置项 |
| `KedgeArrowPreference` | Done | custom Khromia-token row | Miuix row + arrow icon | 跳转条目 |
| `KedgeSegmentedList` family | Done | Kedge segmented settings group | Kedge segmented settings group + Miuix colors | `KedgeSegmentedListItem` / Switch / Checkbox / Radio / TextField |

### Khromia Preference Assets

Khromia 已提供可直接复用的 MD3Exp 设置项能力：

- `OptionItem(imageVector/painter, title, subtitle, onClick, content)`：标准带尾部 slot 的设置行。
- `OptionItem(imageVector/painter, title, subtitle, checked, onCheckedChange)`：带开关的设置行，内部使用 `OptionSwitch`。
- `ButtonOption(imageVector/painter, title, subtitle, onClick)`：点击型设置行。
- `ExpandableOptionItem(..., checked, onCheckedChange, content)`：带开关且可展开内容的设置行。
- `OptionSwitch`：Khromia 的 MD3Exp 开关实现，支持 expressive thumb shape 与 press motion。

因此 Preference 模块实现时，MD3Exp 分支应优先桥接这些 Khromia 组件；只有 Khromia 不覆盖的行为再用官方 Material3 组合实现。

## Implementation Order

推荐按依赖顺序推进：

1. Foundation tokens: `KedgeStyle`、`KedgeTheme`、spacing、shape、motion、content color bridge
2. Atoms: Button、IconButton、TextField、Switch、Checkbox、RadioButton、Slider、Badge
3. Containers: Surface、Card、RowScope、ColumnScope
4. Navigation: TopAppBar、BottomBar、NavigationBar、NavigationRail、BreadcrumbBar、AdaptiveScaffold refinement
5. Overlays: Dialog、AlertDialog、BottomSheet、Toast、ProgressIndicator
6. Preferences: Category、TextPreference、SwitchPreference、ListPreference、ArrowPreference

## Current First-Phase Files

- `theme/KedgeStyle.kt`
- `theme/KedgeTheme.kt`
- `components/KedgeButton.kt`
- `components/KedgeTextField.kt`
- `components/KedgeSelectionControls.kt`
- `components/KedgeSliders.kt`
- `components/KedgeBadges.kt`
- `components/KedgeContainers.kt`
- `adaptive/KedgeAdaptiveScaffold.kt`
- `adaptive/KedgeNavigation.kt`
- `overlays/KedgeDialogs.kt`
- `overlays/KedgeSheets.kt`
- `overlays/KedgeFeedback.kt`
- `preferences/KedgePreferences.kt`
- `sample/KedgeUsageExample.kt`

## Definition Of Done For Each Component

- Public `KedgeXxx` API exists and does not leak style-specific implementation types.
- MD3Exp branch compiles and uses Khromia where available, otherwise official Material3.
- Miuix branch compiles and uses Miuix official components where available.
- `@Preview` or sample usage covers both `KedgeStyle.MD3Exp` and `KedgeStyle.Miuix`.
- Component respects enabled/disabled/error/selected states.
- Miuix branch inherits Miuix `LocalContentColor` and also bridges Material3 `LocalContentColor` for mixed `Text` / `Icon` call sites.
