# Contributing

Kedge 是 Android-only Jetpack Compose UI bridge library。贡献代码时请优先保持现有桥接架构，而不是把业务层暴露给底层 Material3 / Miuix 差异。

## Development Setup

1. 安装 JDK 21。
2. 使用 Android Studio 打开项目根目录。
3. 如需本地联调 Khromia，把 Khromia 放在 Kedge 同级目录：

```text
Android/Project/
  Kedge/
  Khromia/
```

4. 执行构建：

```bash
./gradlew :kedge:assembleDebug
```

## Component Rules

- Public API 使用 `KedgeXxx` 命名。
- 组件内部通过 `LocalKedgeStyle.current` 分发 MD3Exp / Miuix 实现。
- MD3Exp 分支优先复用 Khromia；Khromia 没有时使用官方 Material3。
- Miuix 分支优先复用 Miuix 官方组件；没有时用 Kedge 内部 HyperOS token 组合实现。
- 搜索框使用 Material3 / Miuix 原生 SearchBar，不自建搜索视觉。
- 瞬时反馈使用 Toast，不新增 Snackbar API。
- 不把 Material3 / Miuix 专属 state 类型作为唯一 public contract；如必须暴露，需保留 Kedge 级兼容 API。

## Style Rules

- Kotlin 源码使用项目现有格式。
- 保持 API 简洁，避免把所有底层参数透传成超长参数列表。
- 新组件需要在 `docs/API_OVERVIEW.md` 和 `docs/COMPONENT_COVERAGE.md` 中登记。
- 有示例价值的组件需要补到 `KedgeUsageExample.kt` 或 `docs/EXAMPLES.md`。

## Verification

提交前至少执行：

```bash
./gradlew :kedge:assembleDebug
```

如果修改了 Khromia bridge，也建议在本地 Khromia 存在时执行一次，确认 composite build 正常。

## License

提交到本仓库的代码默认按 MIT License 授权。若代码参考或移植 Apache-2.0 来源，请在文件注释或文档中保留来源说明。
