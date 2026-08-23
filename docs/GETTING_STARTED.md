# 快速接入

本文档说明如何在 Android Compose 项目中接入 Kedge，并完成 MD3Exp / Miuix 双轨风格切换。

## 1. 项目结构

推荐开发期使用 composite build：

```text
Android/Project/
  YourApp/
  Kedge/
  Khromia/        # 可选；存在时 Kedge 自动使用本地 Khromia
```

Kedge 的 `settings.gradle.kts` 会检测 `../Khromia`。如果目录存在，自动把 `heizige.kk:khromia` 替换成本地 `:khromia` 模块；如果不存在，则走常规依赖解析。

## 2. 在业务 App 中接入 Kedge

在业务 App 的 `settings.gradle.kts` 增加：

```kotlin
includeBuild("../Kedge") {
    dependencySubstitution {
        substitute(module("dev.heizige:kedge")).using(project(":kedge"))
    }
}
```

在业务 App 的 `build.gradle.kts` 增加：

```kotlin
dependencies {
    implementation("dev.heizige:kedge:0.1.0")
}
```

## 3. Khromia 依赖

Kedge 的 MD3Exp 分支会优先复用 Khromia。开发期最稳的方式是把 Khromia clone 到 Kedge 同级目录。

如果不使用本地 Khromia，需要保证 Gradle 能解析 `heizige.kk:khromia`。当前配置包含 GitHub Packages 仓库，可通过 `gradle.properties` 或环境变量配置凭据：

```properties
gpr.user=your-github-user
gpr.key=your-github-token
```

或：

```bash
export GITHUB_USER=your-github-user
export GITHUB_TOKEN=your-github-token
```

## 4. 初始化主题

```kotlin
@Composable
fun AppRoot() {
    KedgeTheme(style = KedgeStyle.MD3Exp) {
        KedgeToastHost()
        AppContent()
    }
}
```

切换 Miuix：

```kotlin
KedgeTheme(style = KedgeStyle.Miuix) {
    KedgeToastHost()
    AppContent()
}
```

## 5. 风格切换建议

业务层不要直接判断 Material 或 Miuix 组件。推荐把用户选择存成 `KedgeStyle`，然后只传入 `KedgeTheme`：

```kotlin
var style by rememberSaveable { mutableStateOf(KedgeStyle.MD3Exp) }

KedgeTheme(style = style) {
    KedgeAdaptiveScaffold(...)
}
```

组件内部会根据 `LocalKedgeStyle.current` 自动路由到对应实现。

## 6. 构建验证

在 Kedge 根目录执行：

```bash
./gradlew :kedge:assembleDebug
```

在业务 App 中执行你的常规 assemble 任务即可。若业务 App 需要本地 Khromia，请确认 `Kedge/../Khromia` 路径存在。
