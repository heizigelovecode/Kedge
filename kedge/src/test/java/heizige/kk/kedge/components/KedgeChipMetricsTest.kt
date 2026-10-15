package heizige.kk.kedge.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 三个 chip 组件 MD3Exp 默认值的纯函数护栏（JVM 单测，无 Android / 无 Compose 运行时）。
 *
 * 断言一律写**具体数值**，不复写被测公式 —— 公式一旦被人改动，这里必须炸而不是跟着一起变。
 * 数值口径取 `Dp.value`（Float），避开 value class 装箱带来的 `equals` 差异。
 */
class KedgeChipMetricsTest {

    private fun assertDp(what: String, expected: Float, actual: Dp) =
        assertEquals(what, expected, actual.value, 0f)

    // ---- pressedChipCorner：静止 ≥ 16dp 时按下 = 静止 − 8dp ----

    @Test
    fun pressedChipCorner_aboveThreshold_subtractsExactly8dp() {
        assertDp("22dp → 14dp", 14f, pressedChipCorner(22.dp))
        assertDp("32dp → 24dp", 24f, pressedChipCorner(32.dp))
    }

    @Test
    fun pressedChipCorner_atThreshold_sitsOnTheFloor() {
        assertDp("16dp → 8dp", 8f, pressedChipCorner(16.dp))
    }

    // ---- pressedChipCorner：静止 < 16dp 时按下恒为 8dp（< 8dp 的调用点按下反而变大）----

    @Test
    fun pressedChipCorner_belowThreshold_isPinnedAt8dp() {
        assertDp("12dp → 8dp", 8f, pressedChipCorner(12.dp))
        assertDp("8dp → 8dp", 8f, pressedChipCorner(8.dp))
        assertDp("6dp → 8dp（按下变大）", 8f, pressedChipCorner(6.dp))
        assertDp("0dp → 8dp（按下变大）", 8f, pressedChipCorner(0.dp))
    }

    // ---- kedgeChipContentPadding：垂直原样透传，水平写死 6 / 16 ----

    @Test
    fun contentPadding_passesMd3VerticalThroughVerbatim() {
        // 特意给一组非 0 且不对称的垂直值：写死 0dp 或写死别的数都会在这里露馅。
        val mdDefault = PaddingValues(start = 4.dp, top = 11.dp, end = 20.dp, bottom = 13.dp)

        val out = kedgeChipContentPadding(mdDefault)

        assertDp("top 原样透传", 11f, out.calculateTopPadding())
        assertDp("bottom 原样透传", 13f, out.calculateBottomPadding())
    }

    @Test
    fun contentPadding_horizontalIs6And16_andMd3DefaultVerticalStays0() {
        // material3 1.5.0-alpha29 三个 chip 的默认内边距水平都是 8dp、垂直都是 0dp。
        val out = kedgeChipContentPadding(PaddingValues(horizontal = 8.dp))

        assertDp("start = 6dp（手绘药丸的 chipTileInset）", 6f, out.calculateLeftPadding(LayoutDirection.Ltr))
        assertDp("end = 16dp（手绘药丸的 padding end）", 16f, out.calculateRightPadding(LayoutDirection.Ltr))
        assertDp("top = MD3 默认 0dp", 0f, out.calculateTopPadding())
        assertDp("bottom = MD3 默认 0dp", 0f, out.calculateBottomPadding())
    }

    @Test
    fun contentPadding_againstRealMd3Default_isComponentWiseIdentity() {
        // 拿真身默认值再证一次「不是硬编码 0」：MD3 哪天给了垂直内边距，这里照样透传。
        val mdDefault = AssistChipDefaults.ContentPadding

        val out = kedgeChipContentPadding(mdDefault)

        assertDp("top", mdDefault.calculateTopPadding().value, out.calculateTopPadding())
        assertDp("bottom", mdDefault.calculateBottomPadding().value, out.calculateBottomPadding())
        assertDp("水平仍写死 6dp", 6f, out.calculateLeftPadding(LayoutDirection.Ltr))
        assertDp("水平仍写死 16dp", 16f, out.calculateRightPadding(LayoutDirection.Ltr))
    }

    // ---- KedgeChipHorizontalArrangement：图标-文字间距 10dp（MD3 自带默认是 8dp）----

    @Test
    fun horizontalArrangement_spacingIs10dp() {
        assertDp("spacing", 10f, KedgeChipHorizontalArrangement.spacing)
    }
}
