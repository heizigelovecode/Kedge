# Kedge

Kedge 是一个专属于 Android 原生 Jetpack Compose 的统一 UI 桥接库。它用一套 `KedgeXxx` 契约组件同时覆盖 MD3Exp 与 Miuix / HyperOS 两套视觉风格，让业务层不再直接依赖 Material 3 或 Miuix 的具体组件实现。

当前版本是第一阶段框架版，重点完成主题分发、基础组件、导航骨架、设置项、弹窗、Toast 反馈和接入 `miuix-blur-android` 的 KernelSU 风格液态玻璃导航。

## 设计目标

- Android-only：只面向 Android 原生 Compose，不做 KMP 抽象。
- 双轨风格：通过 `KedgeStyle.MD3Exp` 与 `KedgeStyle.Miuix` 切换渲染分支。
- 业务解耦：业务层只调用 `KedgeTheme`、`LocalKedgeStyle` 与 `KedgeXxx` 组件。
- MD3Exp 优先复用 Khromia：Khromia 已覆盖的 MD3Exp 交互直接桥接，缺失部分回退官方 Material 3。
- Miuix 保持 HyperOS 气质：默认使用 Miuix 色板、圆角、块状设置项和高对比容器，不默认套 Material You。
- 反馈只用 Toast：瞬时提示统一走 Khromia Toast host，Miuix 分支仅做 custom toast pill 配色适配。

## 当前能力

- `KedgeTheme`：全局风格与主题分发。
- `KedgeAdaptiveScaffold`：MD3 `NavigationSuiteScaffold` 与 Miuix 响应式底栏/侧栏桥接。
- `KedgeButton` / `KedgeTextButton` / `KedgeIconButton`：支持 MD3 Expressive shape morph 与 Miuix 按钮分支。
- `KedgeTextField` / `KedgeOutlinedTextField`：MD3 默认 20dp 圆角，Miuix 使用 Miuix 输入框语义。
- `KedgeNavigationBar` / `KedgeNavigationRail` / `KedgeLiquidGlassNavigationBar`：覆盖常规导航和基于 `miuix-blur` backdrop 的液态玻璃浮动导航。
- `KedgeDialog` / `KedgeAlertDialog` / `KedgeBottomSheet` / `KedgeDialogHost`：统一浮层 API。
- `KedgeToastHost` / `KedgeToast` / `rememberKedgeToastController` / `KedgeToastPill`：统一 Toast 反馈。
- `KedgePreferenceCategory` / `KedgeTextPreference` / `KedgeSwitchPreference` / `KedgeListPreference` / `KedgeArrowPreference`：设置中心组件。
- `KedgeSearchBar`：MD3Exp 桥接 Material3 原生 `SearchBar`，Miuix 桥接 Miuix 原生 `SearchBar` / `InputField`。
- 分段列表、状态标签、警告卡片、菜单、图片容器、Slider、Badge、Switch、Checkbox、RadioButton 等基础组件。

完整组件矩阵见 [docs/COMPONENT_COVERAGE.md](docs/COMPONENT_COVERAGE.md)。

## 环境要求

- Android Gradle Plugin 9.3.1
- Kotlin 2.4.10
- Java 21
- compileSdk 37
- minSdk 26
- Jetpack Compose BOM 2026.08.00
- Material 3 1.5.0-alpha26
- Miuix 0.9.3
- Miuix Blur 0.9.3
- Khromia 1.6.2 或本地 Khromia composite build

## 本地构建

```bash
./gradlew :kedge:assembleDebug
```

如果你正在同时开发 Khromia，把 Khromia 放在 Kedge 的同级目录：

```text
Android/Project/
  Kedge/
  Khromia/
```

Kedge 会自动检测 `../Khromia`，存在时使用 composite build 替换 `heizige.kk:khromia` 依赖；不存在时走 Gradle 依赖解析。

## 接入方式

当前 Kedge 尚未发布到公共 Maven。推荐先用 composite build 接入业务 App。

在业务 App 的 `settings.gradle.kts` 中：

```kotlin
includeBuild("../Kedge") {
    dependencySubstitution {
        substitute(module("heizige.kk:kedge")).using(project(":kedge"))
    }
}
```

在业务 App 模块中：

```kotlin
dependencies {
    implementation("heizige.kk:kedge:0.1.0")
}
```

如果没有本地 Khromia，需要配置 Khromia 的 GitHub Packages 凭据或先把 Khromia 发布到你可访问的 Maven 仓库：

```properties
gpr.user=你的 GitHub 用户名
gpr.key=你的 GitHub token
```

## 快速示例

```kotlin
@Composable
fun App() {
    KedgeTheme(style = KedgeStyle.Miuix) {
        val toast = rememberKedgeToastController()

        KedgeToastHost()
        KedgeAdaptiveScaffold(
            selectedItem = Destination.Home,
            onItemSelected = { /* update route */ },
            navigationItems = listOf(
                KedgeNavigationItem(
                    key = Destination.Home,
                    label = "Home",
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Rounded.Home,
                ),
            ),
        ) {
            KedgeColumnScope(Modifier.padding(20.dp)) {
                KedgeTextField(
                    value = "",
                    onValueChange = {},
                    label = "Name",
                    singleLine = true,
                )

                KedgeButton(onClick = { toast.show("Saved", Icons.Rounded.Check) }) {
                    Text("Save")
                }
            }
        }
    }
}
```

完整示例见 [kedge/src/main/java/heizige/kk/kedge/sample/KedgeUsageExample.kt](kedge/src/main/java/heizige/kk/kedge/sample/KedgeUsageExample.kt)。

## 文档索引

- [快速接入](docs/GETTING_STARTED.md)
- [API 总览](docs/API_OVERVIEW.md)
- [主题与样式](docs/THEME_AND_STYLING.md)
- [示例用法](docs/EXAMPLES.md)
- [组件覆盖矩阵](docs/COMPONENT_COVERAGE.md)
- [KernelSU 组件差异审计](docs/KERNELSU_COMPONENT_GAP_AUDIT.md)
- [路线图](docs/ROADMAP.md)
- [贡献指南](CONTRIBUTING.md)
- [更新日志](CHANGELOG.md)

## 开源协议

Kedge 使用 MIT License，见 [LICENSE](LICENSE)。
