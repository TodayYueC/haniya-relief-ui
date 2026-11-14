package com.example.relief.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.relief.R

private fun inter(weight: Int) = Font(
    R.font.inter_variable,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

/** Inter：数字与英文。中文交给系统字体（思源黑体 / Noto Sans CJK）。 */
val InterFamily = FontFamily(inter(400), inter(500), inter(600), inter(650), inter(700))

private val trimNone = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None)

/**
 * 克制的排版层级：标题 medium / semibold，正文保持正常字重。
 * 中文不使用负字距；说明文字至少 12sp，靠字号与留白区分层级。
 */
@Immutable
data class ReliefType(
    val display: TextStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold, lineHeight = 38.sp, letterSpacing = 0.em),
    val greeting: TextStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium, lineHeight = 34.sp, letterSpacing = 0.01.em),
    val pageTitle: TextStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp, letterSpacing = 0.01.em),
    val title: TextStyle = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium, lineHeight = 25.sp, letterSpacing = 0.01.em),
    val h3: TextStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, lineHeight = 23.sp, letterSpacing = 0.01.em),
    val body: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 23.sp, letterSpacing = 0.01.em),
    val bodyMedium: TextStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, lineHeight = 23.sp, letterSpacing = 0.01.em),
    val caption: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 18.sp, letterSpacing = 0.01.em),
    val label: TextStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp, letterSpacing = 0.01.em),
    val badge: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp, letterSpacing = 0.01.em),
    val button: TextStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, lineHeight = 22.sp, letterSpacing = 0.01.em),
    val buttonSmall: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp, letterSpacing = 0.01.em),
    val tab: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp, letterSpacing = 0.01.em),
)

/** 数字：Inter + 等宽数字 + 负字距。 */
fun num(size: TextUnit, weight: FontWeight = FontWeight.Medium, tracking: Float = -0.02f): TextStyle = TextStyle(
    fontFamily = InterFamily,
    fontSize = size,
    fontWeight = weight,
    letterSpacing = tracking.em,
    fontFeatureSettings = "tnum",
    lineHeight = size * 1.15f,
    lineHeightStyle = trimNone,
)

object NumStyles {
    val xxl = num(52.sp)
    val xl = num(44.sp)
    val lg = num(34.sp)
    val md = num(28.sp)
    val sm = num(22.sp)
    val xs = num(19.sp, FontWeight.Medium)
    val tiny = num(14.sp, FontWeight.Medium, 0f)
    val caption = num(12.sp, FontWeight.Medium, 0f)
}
