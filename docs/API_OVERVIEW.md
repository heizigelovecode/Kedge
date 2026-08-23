# API 总览

Kedge 的公开 API 按包分成五类：`theme`、`adaptive`、`components`、`overlays`、`preferences`。业务层应优先使用这些 `KedgeXxx` 组件，不直接依赖底层 Material 3 或 Miuix 组件。

## Theme

| API | 说明 |
|---|---|
| `KedgeStyle` | 风格枚举：`MD3Exp`、`Miuix`。 |
| `LocalKedgeStyle` | 当前风格的 `CompositionLocal`。组件内部根据它分发实现。 |
| `KedgeTheme` | 全局主题入口，自动应用 MaterialTheme 或 MiuixTheme。 |
| `currentKedgeStyle` | 只读便捷访问器。 |

## Scaffold & Navigation

| API | 说明 |
|---|---|
| `KedgeNavigationItem<T>` | 泛型导航项，承载 key、label、icon、selectedIcon、badge。 |
| `KedgeAdaptiveScaffold<T>` | 自适应页面骨架。MD3Exp 使用 `NavigationSuiteScaffold`，Miuix 使用底栏/侧栏响应式布局。 |
| `KedgeTopAppBar` | 小标题顶部栏，支持 subtitle、navigationIcon、actions、centered。 |
| `KedgeLargeTopAppBar` | 大标题顶部栏。 |
| `KedgeTopBarBackButton` | 顶部栏返回按钮。 |
| `KedgeBottomBar<T>` | 底部导航容器。 |
| `KedgeNavigationBar<T>` | 底部导航条。 |
| `KedgeNavigationRail<T>` | 侧边导航栏，支持 `expanded`。 |
| `KedgeBreadcrumbItem` | 面包屑项。 |
| `KedgeBreadcrumbBar` | 面包屑导航。 |
| `KedgeLiquidGlassNavigationBar<T>` | 液态玻璃浮动导航条。 |
| `KedgeLiquidGlassBottomBar<T>` | 底部栏包装版液态玻璃导航。 |
| `rememberKedgeLiquidGlassBackdrop` | 创建 Kedge 包装后的液态玻璃 backdrop 状态。 |
| `KedgeLiquidGlassBackdropState` | Kedge 自有 backdrop 状态类型，内部持有 Miuix blur layer，业务层无需导入 `LayerBackdrop`。 |
| `KedgeLiquidGlassBackdrop` | 对页面内容应用 `Modifier.layerBackdrop(...)`，供液态玻璃导航采样背景。 |
| `KedgeLiquidGlassNavigationBarColors` | 液态玻璃导航颜色配置。 |
| `KedgeLiquidGlassNavigationBarDefaults` | 液态玻璃导航默认值。 |

## Atoms & Inputs

| API | 说明 |
|---|---|
| `KedgeButtonVariant` | `Primary`、`Secondary`、`Text`。 |
| `KedgeIconButtonVariant` | `Standard`、`Filled`、`Tonal`。 |
| `KedgeButtonDefaults` | 默认 padding 与 MD3 shape morph helpers。 |
| `KedgeButton` | 统一按钮。MD3Exp 支持 `ButtonShapes` 点击变形。 |
| `KedgeTextButton` | 文本按钮。 |
| `KedgeIconButton` | 图标按钮。MD3Exp 支持 `IconButtonShapes` 点击变形。 |
| `KedgeTextFieldDefaults` | 输入框默认值；MD3 默认 shape 为 20dp 圆角。 |
| `KedgeTextField` | 统一输入框。 |
| `KedgeOutlinedTextField` | 统一描边输入框。 |
| `KedgeSearchBar` | 搜索组件契约；MD3Exp 桥接 Material3 `SearchBar`，Miuix 桥接 Miuix `SearchBar` / `InputField`。 |
| `KedgeSearchTopAppBar` | 带搜索框的顶部栏，内部复用原生桥接 `KedgeSearchBar`。 |
| `KedgeSwitch` | 开关。MD3Exp 优先复用 Khromia `OptionSwitch`。 |
| `KedgeCheckbox` | 复选框。 |
| `KedgeRadioButton` | 单选按钮。 |
| `KedgeSlider` | 单值滑杆。 |
| `KedgeRangeSlider` | 范围滑杆。 |
| `KedgeBadge` | 徽章。 |
| `KedgeBadgedBox` | 带徽章容器。 |
| `KedgeTag` | 轻量标签。 |
| `KedgeStatusLevel` | `Info`、`Success`、`Notice`、`Warning`、`Error`。 |
| `KedgeStatusTag` | 语义状态标签。 |
| `KedgeWarningCard` | 语义警告卡片。 |
| `KedgeTabItem<T>` | Tab 项。 |
| `KedgeTabRow<T>` | 分段式 Tab 行。 |
| `KedgeToggleButton` | 单个 toggle chip。 |
| `KedgeSegmentedButton` | 多选项分段按钮。 |
| `KedgeAppIconImage` | App 图标容器。 |
| `KedgeIconImage` | 通用图标容器。 |
| `KedgeAvatarImage` | 头像容器。 |

## Containers & Lists

| API | 说明 |
|---|---|
| `KedgeSurface` | 统一基础容器。 |
| `KedgeCard` | 统一卡片，支持 optional click。 |
| `KedgeColumnScope` | 带默认间距的 Column。 |
| `KedgeRowScope` | 带默认间距与换行策略的 Row。 |
| `KedgeSegmentedList` | 设置页分段列表容器。 |
| `KedgeSegmentedListItem` | 标准分段列表项。 |
| `KedgeSegmentedSwitchItem` | 带开关的分段列表项。 |
| `KedgeSegmentedCheckboxItem` | 带复选框的分段列表项。 |
| `KedgeSegmentedRadioItem` | 带单选按钮的分段列表项。 |
| `KedgeSegmentedTextField` | 分段列表内输入项。 |

## Dialogs, Sheets & Feedback

| API | 说明 |
|---|---|
| `KedgeDialog` | 通用弹窗容器。 |
| `KedgeAlertDialog` | 标准确认弹窗。 |
| `KedgeDialogHostState` | Dialog host 状态。 |
| `rememberKedgeDialogHostState` | 创建 Dialog host 状态。 |
| `KedgeDialogHost` | 渲染 confirm/loading 等 host-state 弹窗。 |
| `KedgeLoadingDialog` | 加载弹窗。 |
| `KedgeModalBottomSheet` | Modal bottom sheet。 |
| `KedgeBottomSheet` | Bottom sheet 别名入口。 |
| `KedgeMenuItem` | 菜单项模型。 |
| `KedgeDropdownMenu` | 下拉菜单。 |
| `KedgeDropdownItem` | 单个下拉项。 |
| `KedgeListPopup` | 列表弹出层。 |
| `KedgeToastHost` | Khromia Toast host，应用根部放一次。 |
| `KedgeToast` | 非组合式 Toast helper。 |
| `rememberKedgeToastController` | 推荐入口；能让 Miuix 分支使用 Miuix 配色 custom toast。 |
| `KedgeToastPill` | Toast 视觉 pill，可用于 preview 或 custom toast content。 |
| `KedgeDismissToast` | 主动关闭 persistent Toast。 |
| `KedgeProgressIndicatorType` | `Linear`、`Circular`、`Infinite`。 |
| `KedgeProgressIndicator` | 统一进度条。 |

## Preferences

| API | 说明 |
|---|---|
| `KedgePreferenceCategory` | 设置分组标题。 |
| `KedgeTextPreference` | 标准文本设置项。 |
| `KedgeSwitchPreference` | 带开关的设置项。 |
| `KedgePreferenceOption<T>` | 列表设置项 option 模型。 |
| `KedgeListPreference<T>` | 单选列表设置项。 |
| `KedgeArrowPreference` | 带跳转箭头的设置项。 |

## API 约束

- 新增组件必须以 `KedgeXxx` 命名。
- Public contract 不应要求业务层传入 Material/Miuix 专属 state 类型。
- 风格判断集中在组件内部，业务层不写样式分支。
- 反馈场景使用 Toast API。
- 优先复用 Khromia 和 Miuix 已有组件；只有缺口才在 Kedge 内补实现。
