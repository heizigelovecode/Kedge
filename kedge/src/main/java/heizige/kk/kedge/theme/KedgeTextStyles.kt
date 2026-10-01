package heizige.kk.kedge.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 双风格文字样式：**替代直接在业务代码里读 `MaterialTheme.typography.*`**。
 *
 * 为什么需要它：`KedgeTheme` 的 Miuix 分支只桥接了配色（见 `MiuixMaterialThemeBridge`），
 * `MaterialTheme.typography` 仍是 MD3 默认字阶。于是 Miuix 界面里只要写了
 * `style = MaterialTheme.typography.bodySmall`，拿到的就是 MD3 的字号字重 ——
 * 与同页 Miuix 组件（Miuix 的 `SwitchPreference` 等自带 Miuix 字阶）放在一起，
 * 字明显偏大偏粗。
 *
 * 用法：`style = MaterialTheme.typography.bodySmall` 换成 `style = KedgeTextStyles.body()`。
 *
 * 映射基准取自 KernelSU（`SettingsMiuix.kt` 里 preference 的标题/摘要层级）与
 * Kedge 自身实现（`KedgeOptionItem` 用 headline1/Medium + body2）：
 *
 * | 语义     | Miuix 字阶         | MD3 对应                  |
 * |----------|--------------------|---------------------------|
 * | 标题     | headline1 + Medium | titleLarge/Medium/Small   |
 * | 大标题   | title3             | headlineMedium/Large      |
 * | 正文     | body2              | bodyLarge/Medium/Small    |
 * | 辅助文字 | footnote1          | labelLarge/Medium/Small   |
 */
object KedgeTextStyles {

    /** 标题类：分组标题、卡片标题、列表项主文案。 */
    @Composable
    fun title(): TextStyle = when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> MiuixTheme.textStyles.headline1.copy(fontWeight = FontWeight.Medium)
        KedgeStyle.MD3Exp -> androidx.compose.material3.MaterialTheme.typography.titleMedium
    }

    /** 页面级大标题（比 [title] 更大一级）。 */
    @Composable
    fun displayTitle(): TextStyle = when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> MiuixTheme.textStyles.title3
        KedgeStyle.MD3Exp -> androidx.compose.material3.MaterialTheme.typography.headlineMedium
    }

    /** 正文类：摘要、说明文字、列表项副文案。 */
    @Composable
    fun body(): TextStyle = when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> MiuixTheme.textStyles.body2
        KedgeStyle.MD3Exp -> androidx.compose.material3.MaterialTheme.typography.bodyMedium
    }

    /** 辅助类：时间戳、计数、状态标签等次要信息。 */
    @Composable
    fun footnote(): TextStyle = when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> MiuixTheme.textStyles.footnote1
        KedgeStyle.MD3Exp -> androidx.compose.material3.MaterialTheme.typography.labelMedium
    }

    /**
     * 更小一级的辅助文字（对应 MD3 `labelSmall`，12sp）。
     *
     * 与 [footnote] 分开是有必要的：MD3Exp 下若把 `labelSmall` 统一映射到
     * `labelMedium`，字号会从 12sp 涨到 14sp，属于视觉回归。Miuix 侧
     * footnote1/footnote2 的差异没有 MD3 明显，这里沿用 footnote1。
     */
    @Composable
    fun footnoteSmall(): TextStyle = when (LocalKedgeStyle.current) {
        KedgeStyle.Miuix -> MiuixTheme.textStyles.footnote1
        KedgeStyle.MD3Exp -> androidx.compose.material3.MaterialTheme.typography.labelSmall
    }
}
