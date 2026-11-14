package com.example.relief.component

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.relief.theme.ReliefColors
import com.example.relief.theme.LocalReliefColors

/**
 * 浮雕层级：Thin 用于按钮与小块，Regular 用于卡片，Thick 用于弹层与悬浮导航。
 * 名称沿用旧 API，数值表示柔影的扩散与位移。
 */
enum class GlassLevel(val blur: Dp, val saturation: Float, internal val radius: Dp, internal val distance: Dp) {
    Thin(16.dp, 1.6f, 9.dp, 3.5.dp),
    Regular(30.dp, 1.8f, 18.dp, 7.dp),
    Thick(40.dp, 1.9f, 26.dp, 10.dp),
}

object Radii {
    val xs = 12.dp
    val sm = 16.dp
    val md = 22.dp
    val card = 26.dp
    val lg = 28.dp
    val sheet = 32.dp
    val pill = 999.dp
}

val BlurSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * 写实浮雕：同色面 + 左上受光柔影 + 右下背光柔影，面内极轻的陶瓷渐变与一圈受光倒角。
 * [tint] 为非中性色时作为彩色实体（例如主按钮），柔影改用其自身色相。
 */
@Suppress("UNUSED_PARAMETER")
fun Modifier.glass(
    shape: Shape = RoundedCornerShape(Radii.md),
    level: GlassLevel = GlassLevel.Regular,
    shadow: Boolean = true,
    tint: Color? = null,
): Modifier = composed {
    val c = LocalReliefColors.current
    raisedSurface(shape, c, level, shadow, tint ?: c.glass)
}

private fun Modifier.raisedSurface(
    shape: Shape,
    c: ReliefColors,
    level: GlassLevel,
    shadow: Boolean,
    fill: Color,
): Modifier {
    val neutral = fill == c.glass || fill == c.glass2 || fill == c.bg
    val face = if (neutral) {
        Brush.linearGradient(listOf(c.faceHi, c.glass, c.faceLo))
    } else {
        Brush.linearGradient(listOf(androidx.compose.ui.graphics.lerp(fill, Color.White, 0.10f), fill, androidx.compose.ui.graphics.lerp(fill, Color.Black, 0.06f)))
    }
    val base = if (!shadow) this else this
        .dropShadow(shape, Shadow(
            radius = level.radius,
            color = if (neutral) c.hi.copy(alpha = if (c.isDark) 0.55f else 0.95f) else Color.White.copy(alpha = if (c.isDark) 0.06f else 0.75f),
            offset = DpOffset(-level.distance, -level.distance),
        ))
        .dropShadow(shape, Shadow(
            radius = level.radius,
            color = if (neutral) c.shadow.copy(alpha = if (c.isDark) 0.85f else 0.55f) else fill.copy(alpha = if (c.isDark) 0.45f else 0.40f),
            offset = DpOffset(level.distance, level.distance),
        ))
    return base
        .background(face, shape)
        .clip(shape)
        .bevel(shape, c, neutral)
}

/** 受光倒角：左上亮边渐隐到右下暗边，让边缘有「被打磨过」的厚度。 */
private fun Modifier.bevel(shape: Shape, c: ReliefColors, neutral: Boolean): Modifier = drawWithContent {
    drawContent()
    if (size.width <= 0f || size.height <= 0f) return@drawWithContent
    val path = shapePath(shape)
    val light = if (c.isDark) Color.White.copy(alpha = 0.07f) else Color.White.copy(alpha = if (neutral) 0.85f else 0.38f)
    val dark = if (c.isDark) Color.Black.copy(alpha = 0.25f) else c.shadow.copy(alpha = if (neutral) 0.16f else 0.10f)
    drawPath(
        path,
        Brush.linearGradient(listOf(light, Color.Transparent, dark), start = Offset.Zero, end = Offset(size.width, size.height)),
        style = Stroke(1.2.dp.toPx()),
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.shapePath(shape: Shape): Path = Path().apply {
    when (val outline = shape.createOutline(size, layoutDirection, this@shapePath)) {
        is Outline.Rectangle -> addRect(outline.rect)
        is Outline.Rounded -> addRoundRect(outline.roundRect)
        is Outline.Generic -> addPath(outline.path)
    }
}

/**
 * 内凹槽：输入框、进度轨道、选中项。[depth] 控制凹陷深度（细轨道用 2dp）。
 */
fun Modifier.insetSurface(
    shape: Shape = RoundedCornerShape(Radii.sm),
    tint: Color? = null,
    depth: Dp = 4.dp,
): Modifier = composed {
    val c = LocalReliefColors.current
    val fill = tint ?: if (c.isDark) c.track else androidx.compose.ui.graphics.lerp(c.bg, c.shadow, 0.05f)
    this.background(fill, shape)
        .innerShadow(shape, Shadow(
            radius = depth * 1.6f, color = c.shadow.copy(alpha = if (c.isDark) 0.95f else 0.62f),
            offset = DpOffset(depth * 0.9f, depth * 0.9f),
        ))
        .innerShadow(shape, Shadow(
            radius = depth * 1.6f, color = c.hi.copy(alpha = if (c.isDark) 0.55f else 1f),
            offset = DpOffset(-depth * 0.9f, -depth * 0.9f),
        ))
        .clip(shape)
}

/**
 * 彩色渐变实体（主按钮、悬浮记录键、图标底块）：自带同色辉光与顶部釉面高光。
 */
fun Modifier.gradientSurface(
    shape: Shape,
    brush: Brush? = null,
    glow: Dp = 12.dp,
    shadow: Boolean = true,
): Modifier = composed {
    val c = LocalReliefColors.current
    val b = brush ?: c.brush
    (if (shadow) this
        .dropShadow(shape, Shadow(radius = glow * 0.8f, color = c.hi.copy(alpha = if (c.isDark) 0.35f else 0.9f), offset = DpOffset(-glow / 3, -glow / 3)))
        .dropShadow(shape, Shadow(radius = glow, color = c.mid.copy(alpha = if (c.isDark) 0.45f else 0.42f), offset = DpOffset(glow / 4, glow / 2.4f)))
    else this)
        .background(b, shape)
        .innerShadow(shape, Shadow(radius = 6.dp, color = Color.White.copy(alpha = 0.40f), offset = DpOffset(2.dp, 2.dp)))
        .innerShadow(shape, Shadow(radius = 6.dp, color = Color.Black.copy(alpha = 0.10f), offset = DpOffset((-2).dp, (-2).dp)))
        .clip(shape)
}

/** 旧 API：保持签名，等同于浮雕倒角。 */
fun Modifier.glassSheen(shape: Shape, c: ReliefColors): Modifier = bevel(shape, c, neutral = true)

/** 安静的纯色背景：浮雕需要卡片与背景同色。 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier.fillMaxSize().background(LocalReliefColors.current.bg), content = content)
}

/** 小号凸面块（嵌套摘要）。 */
fun Modifier.tile(c: ReliefColors, shape: Shape = RoundedCornerShape(Radii.sm)): Modifier =
    raisedSurface(shape, c, GlassLevel.Thin, shadow = true, fill = c.glass)
