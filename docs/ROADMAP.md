# Roadmap

Kedge 当前处于 `0.1.0` 第一阶段框架版。路线图按优先级组织，具体版本号可以在发布前调整。

## 0.1.x Stabilization

- 稳定 `KedgeTheme` / `LocalKedgeStyle` / `KedgeStyle` 契约。
- 稳定基础组件参数命名，避免后续大面积 breaking change。
- 为核心组件补 Compose UI screenshot 或 semantic tests。
- 用 Material3 新 `SearchBarState` / `TextFieldState` API 替换当前兼容型 SearchBar overload。
- 为 `KedgeLiquidGlassNavigationBar` 增加设备 API fallback 策略和 runtime capability 检测。

## 0.2.x Component Completion

- 继续补齐高级导航状态：scroll behavior、折叠 rail、large top app bar 联动。
- 补齐表单组件：密码框、下拉选择、日期/时间选择入口。
- 补齐列表组件：lazy preference section、sticky category、empty state、load state。
- 增加 `KedgeToolbar` / `KedgeActionBar` 轻量工具栏。
- 增加富文本和 Markdown 可选模块。

## 0.3.x Liquid & Motion

- 将 KernelSU / Miuix liquid glass 的 lens refraction、vibrancy、inner shadow 抽成小型 Kedge liquid API。
- 将 damped drag 和 interactive highlight 通用化，供 navigation、segmented control、tab row 共用。
- 增加风格级 motion tokens，减少每个组件各自定义 spring 参数。

## 0.4.x Publishing

- 接入 Maven publishing metadata：POM name、description、license、scm、developer。
- 增加 release variant CI 构建。
- 增加 GitHub Actions：assemble、lint、API check。
- 发布到 GitHub Packages 或 Maven Central。

## Non-goals

- 不做 KMP。
- 不把 Material 3 / Miuix 所有底层参数原样外漏给业务层。
- 不复制 KernelSU 的 app-domain flow，例如下载弹窗、卸载弹窗、profile 配置页。
- 不新增 Snackbar API；瞬时反馈统一走 Toast。
