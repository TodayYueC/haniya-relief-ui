package com.example.relief.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * 柔光浮雕配色：同色系浅底 + 左上白光 / 右下冷影两组柔影塑造体积，
 * 强调色以双色渐变（accent → acc2）出现在少量关键处，保证安静而有光泽。
 */
@Immutable
data class ReliefColors(
    val bg: Color,
    val o1: Color,
    val o2: Color,
    val o3: Color,
    val ink: Color,
    val ink2: Color,
    val glass: Color,
    val glass2: Color,
    val stroke: Color,
    val line: Color,
    /** 左上方的受光柔影。 */
    val hi: Color,
    val sheen: Color,
    /** 右下方的背光柔影。 */
    val shadow: Color,
    val accent: Color,
    val on: Color,
    val soft: Color,
    val acc2: Color,
    val soft2: Color,
    val track: Color,
    val scrim: Color,
    val isDark: Boolean,
) {
    /** 固定色：提示 / 待确认徽标。 */
    val amber: Color get() = Color(0xFFD2A26C)
    val amberInk: Color get() = if (isDark) Color(0xFFE6C9A2) else Color(0xFF8C6235)
    val amberSoft: Color get() = Color(0x1FD2A26C)
    /** 解除配对等不可逆操作的文字色（不用于数据评价）。 */
    val danger: Color get() = if (isDark) Color(0xFFE6A3AE) else Color(0xFFB4566A)

    /** 主渐变：图标底块、主按钮、进度填充。 */
    val brush: Brush get() = Brush.linearGradient(listOf(accent, acc2))
    /** 横向渐变（进度条、开关轨道）。 */
    val hBrush: Brush get() = Brush.horizontalGradient(listOf(accent, acc2))
    /** 渐变中段色，用于单色场景（图标线稿、选中文字）。 */
    val mid: Color get() = lerp(accent, acc2, 0.35f)
    /** 凸面受光面与背光面（极轻的面内渐变，模拟陶瓷质感）。 */
    val faceHi: Color get() = if (isDark) lerp(glass, Color.White, 0.035f) else lerp(glass, Color.White, 0.42f)
    val faceLo: Color get() = if (isDark) lerp(glass, Color.Black, 0.10f) else lerp(glass, shadow.copy(alpha = 1f), 0.05f)
}

private val White = Color.White

/**
 * 浅色主题：bg 与卡片同色（浮雕的前提），文字用带色相的深灰，
 * 阴影取 bg 的同色相加深，避免发脏。
 */
private fun lightPalette(
    bg: Color,
    shadow: Color,
    ink: Color,
    ink2: Color,
    accent: Color,
    acc2: Color,
    swatch: Color,
) = ReliefColors(
    bg = bg, o1 = swatch, o2 = lerp(bg, acc2, .14f), o3 = lerp(bg, accent, .10f),
    ink = ink, ink2 = ink2,
    glass = bg, glass2 = lerp(bg, White, .10f),
    stroke = White.copy(alpha = .75f), line = lerp(bg, shadow, .32f),
    hi = White, sheen = White.copy(alpha = .55f), shadow = shadow,
    accent = accent, on = White,
    soft = lerp(bg, accent, .12f), acc2 = acc2, soft2 = lerp(bg, acc2, .14f),
    track = lerp(bg, shadow, .10f),
    scrim = lerp(ink, Color.Black, .3f).copy(alpha = .32f), isDark = false,
)

/** 五套调色板：四套浅色 + 一套深色。 */
enum class ReliefPalette(val label: String, val isDark: Boolean) {
    PLATINUM("铂雾", false), MOONSTONE("月岩", false), CHAMPAGNE("香槟", false), ROSE("烟玫", false), OBSIDIAN("曜石", true),
}

object ReliefPalettes {
    /** 铂雾（默认）：冷调珍珠灰 × 长春花蓝 → 薰衣草紫。 */
    val Platinum = lightPalette(
        bg = Color(0xFFE6EAF1), shadow = Color(0xFFA9B5C9),
        ink = Color(0xFF2F3648), ink2 = Color(0xFF6F7890),
        accent = Color(0xFF6A86F2), acc2 = Color(0xFFAA8CF0), swatch = Color(0xFFC7CFF5),
    )

    /** 月岩：灰紫石面 × 堇紫 → 兰花粉。 */
    val Moonstone = lightPalette(
        bg = Color(0xFFE9E7EF), shadow = Color(0xFFB2ADC6),
        ink = Color(0xFF332F45), ink2 = Color(0xFF746E8C),
        accent = Color(0xFF8072E0), acc2 = Color(0xFFD291C8), swatch = Color(0xFFD6CDF0),
    )

    /** 香槟：暖象牙 × 古铜金 → 杏粉。 */
    val Champagne = lightPalette(
        bg = Color(0xFFEFEBE5), shadow = Color(0xFFC4B8A8),
        ink = Color(0xFF3B342D), ink2 = Color(0xFF7D7366),
        accent = Color(0xFFBA8B5C), acc2 = Color(0xFFE2A88C), swatch = Color(0xFFE8D3BC),
    )

    /** 烟玫：灰粉瓷 × 烟玫瑰 → 雾桃。 */
    val Rose = lightPalette(
        bg = Color(0xFFEFE8EB), shadow = Color(0xFFC6B3BC),
        ink = Color(0xFF3C2F37), ink2 = Color(0xFF80707A),
        accent = Color(0xFFC77391), acc2 = Color(0xFFE6A59A), swatch = Color(0xFFEDCBD6),
    )

    /** 曜石：深石墨暗面浮雕 × 冰蓝 → 星紫。 */
    val Obsidian = ReliefColors(
        bg = Color(0xFF262A33), o1 = Color(0xFF7E95FF), o2 = Color(0xFF3A3550), o3 = Color(0xFF2E3A52),
        ink = Color(0xFFE8EBF3), ink2 = Color(0xFF9AA2B6),
        glass = Color(0xFF262A33), glass2 = Color(0xFF2B303A),
        stroke = Color(0x14FFFFFF), line = Color(0xFF363C48),
        hi = Color(0xFF353B47), sheen = Color(0x0FFFFFFF), shadow = Color(0xFF14171D),
        accent = Color(0xFF7E95FF), on = Color.White, soft = Color(0xFF323B57),
        acc2 = Color(0xFFB79CFF), soft2 = Color(0xFF3A3452), track = Color(0xFF22262E),
        scrim = Color(0x990C0E12), isDark = true,
    )

    fun of(theme: ReliefPalette): ReliefColors = when (theme) {
        ReliefPalette.PLATINUM -> Platinum
        ReliefPalette.MOONSTONE -> Moonstone
        ReliefPalette.CHAMPAGNE -> Champagne
        ReliefPalette.ROSE -> Rose
        ReliefPalette.OBSIDIAN -> Obsidian
    }

    fun swatch(theme: ReliefPalette): Pair<Color, Color> = of(theme).let { it.accent to it.acc2 }
}
