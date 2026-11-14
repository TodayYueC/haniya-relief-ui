package com.example.relief.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import com.example.relief.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.relief.theme.ReliefMotion
import com.example.relief.theme.ReliefTheme
import com.example.relief.theme.LocalReducedMotion
import com.example.relief.theme.Motion
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import com.example.relief.theme.ReliefColors
import kotlin.math.cos
import kotlin.math.sin

/** 环形凹槽：左上暗、右下亮的渐变描边，模拟刻进表面的圆槽。 */
private fun DrawScope.grooveArc(c: ReliefColors, tl: Offset, arc: Size, sw: Float) {
    drawArc(c.track, 0f, 360f, false, tl, arc, style = Stroke(sw))
    drawArc(
        Brush.linearGradient(
            listOf(c.shadow.copy(alpha = if (c.isDark) 0.75f else 0.45f), Color.Transparent, c.hi.copy(alpha = if (c.isDark) 0.35f else 0.95f)),
            start = tl, end = Offset(tl.x + arc.width, tl.y + arc.height),
        ),
        0f, 360f, false, tl, arc, style = Stroke(sw),
    )
    // 槽底再压一圈更深的细线，让凹陷有深度。
    drawArc(c.shadow.copy(alpha = if (c.isDark) 0.35f else 0.12f), 0f, 360f, false, tl, arc, style = Stroke(sw * 0.35f))
}

private fun ringBrush(c: ReliefColors, color: Color?, tl: Offset, arc: Size): Brush =
    if (color != null && color != c.accent) Brush.linearGradient(listOf(color, lerp(color, Color.White, 0.25f)), tl, Offset(tl.x + arc.width, tl.y + arc.height))
    else Brush.linearGradient(listOf(c.accent, c.acc2), tl, Offset(tl.x + arc.width, tl.y + arc.height))

/** 进度环（柔和弹簧过渡），可选末端滑块。 */
@Composable
fun ProgressRing(
    progress: Float,
    size: Dp,
    stroke: Dp,
    modifier: Modifier = Modifier,
    color: Color? = null,
    knob: Boolean = false,
    description: String? = null,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val c = ReliefTheme.colors
    val p by animateFloatAsState(progress.coerceIn(0f, 1f), Motion.soft(), label = "ring")
    val col = color ?: c.accent
    Box(
        modifier.size(size).then(if (description != null) Modifier.semantics { contentDescription = description } else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = stroke.toPx()
            val inset = sw / 2 + if (knob) 6.dp.toPx() else 0f
            val arcSize = Size(this.size.width - inset * 2, this.size.height - inset * 2)
            val tl = Offset(inset, inset)
            grooveArc(c, tl, arcSize, sw)
            val brush = ringBrush(c, color, tl, arcSize)
            if (p > 0f) {
                // 同色辉光 + 釉面弧
                drawArc(col.copy(alpha = 0.18f), -90f, 360f * p, false, tl, arcSize, style = Stroke(sw * 1.6f, cap = StrokeCap.Round))
                drawArc(brush, -90f, 360f * p, false, tl, arcSize, style = Stroke(sw * 0.78f, cap = StrokeCap.Round))
                drawArc(Color.White.copy(alpha = 0.28f), -90f, 360f * p, false, tl + Offset(-sw * 0.12f, -sw * 0.12f), arcSize, style = Stroke(sw * 0.18f, cap = StrokeCap.Round))
            }
            if (knob && p > 0f) {
                val r = arcSize.width / 2
                val a = Math.toRadians((p * 360f - 90f).toDouble())
                val center = Offset(this.size.width / 2 + (r * cos(a)).toFloat(), this.size.height / 2 + (r * sin(a)).toFloat())
                drawCircle(c.shadow.copy(alpha = 0.55f), 11.dp.toPx(), center + Offset(1.5.dp.toPx(), 2.dp.toPx()))
                drawCircle(Brush.linearGradient(listOf(Color.White, lerp(c.bg, Color.White, 0.4f)), center - Offset(10f, 10f), center + Offset(10f, 10f)), 10.dp.toPx(), center)
                drawCircle(brush, 4.dp.toPx(), center)
            }
        }
        content()
    }
}

/** 描绘进度动画（1.2s 标准缓出），range 变化时重播。 */
@Composable
private fun rememberDrawProgress(key: Any?): Float {
    val reduced = LocalReducedMotion.current
    val a = remember(key) { Animatable(if (reduced) 1f else 0f) }
    LaunchedEffect(key) { if (!reduced) a.animateTo(1f, tween(1200, easing = ReliefMotion.StandardEasing)) }
    return a.value
}

data class ChartSeries(val values: List<Float?>, val color: Color, val width: Dp, val alpha: Float = 1f, val dashed: Boolean = false)

/**
 * 折线图：支持多条线（如每日记录 + 7 日平均），null 处断开；带 3 条网格线与标签。
 */
@Composable
fun LineChart(
    series: List<ChartSeries>,
    modifier: Modifier = Modifier,
    animKey: Any? = null,
    yLabels: Int = 3,
    format: (Float) -> String = { "%.1f".format(it) },
    endDot: Boolean = true,
    minRange: Float = 0.4f,
    description: String = "趋势图",
) {
    val c = ReliefTheme.colors
    val prog = rememberDrawProgress(animKey)
    val all = series.flatMap { s -> s.values.filterNotNull() }
    if (all.isEmpty()) return
    var lo = all.min()
    var hi = all.max()
    if (hi - lo < minRange) {
        val mid = (hi + lo) / 2
        lo = mid - minRange / 2
        hi = mid + minRange / 2
    }
    val pad = (hi - lo) * 0.12f
    lo -= pad
    hi += pad
    val type = ReliefTheme.type
    Row(modifier.semantics { contentDescription = description }) {
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
            for (i in 0 until yLabels) {
                val v = hi - (hi - lo) * i / (yLabels - 1)
                Text(format(v), style = com.example.relief.theme.NumStyles.caption, color = c.ink2)
            }
        }
        Canvas(Modifier.weight(1f).fillMaxHeight()) {
            val w = size.width
            val h = size.height
            val top = 8.dp.toPx()
            val bottom = h - 8.dp.toPx()
            for (i in 0 until yLabels) {
                val y = top + (bottom - top) * i / (yLabels - 1)
                drawLine(c.line, Offset(8.dp.toPx(), y), Offset(w, y), 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)))
            }
            series.forEachIndexed { si, s ->
                val n = s.values.size
                if (n == 0) return@forEachIndexed
                val path = Path()
                var started = false
                var last: Offset? = null
                s.values.forEachIndexed { i, v ->
                    if (v == null) {
                        started = false
                        return@forEachIndexed
                    }
                    val x = 8.dp.toPx() + (w - 14.dp.toPx()) * (if (n == 1) 0.5f else i.toFloat() / (n - 1))
                    val y = top + (bottom - top) * (hi - v) / (hi - lo)
                    if (!started) path.moveTo(x, y) else path.lineTo(x, y)
                    started = true
                    last = Offset(x, y)
                }
                // 最后一条实线下方铺一层由浅到无的柔光面。
                if (si == series.lastIndex && !s.dashed && prog >= 1f && s.values.count { it != null } > 1 && s.values.all { it != null }) {
                    val firstX = 8.dp.toPx()
                    val lastX = 8.dp.toPx() + (w - 14.dp.toPx())
                    val area = Path().apply {
                        addPath(path)
                        lineTo(lastX, bottom)
                        lineTo(firstX, bottom)
                        close()
                    }
                    drawPath(area, Brush.verticalGradient(listOf(s.color.copy(alpha = 0.20f), s.color.copy(alpha = 0f)), top, bottom))
                }
                val measure = PathMeasure().apply { setPath(path, false) }
                val drawn = Path()
                measure.getSegment(0f, measure.length * prog, drawn, true)
                drawPath(
                    if (prog >= 1f) path else drawn, s.color.copy(alpha = s.alpha),
                    style = Stroke(s.width.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = if (s.dashed) PathEffect.dashPathEffect(floatArrayOf(10f, 8f)) else null),
                )
                if (endDot && s == series.last() && prog >= 1f) last?.let {
                    drawCircle(c.shadow.copy(alpha = 0.45f), 7.dp.toPx(), it + Offset(1.dp.toPx(), 1.5.dp.toPx()))
                    drawCircle(Color.White, 6.5.dp.toPx(), it)
                    drawCircle(s.color, 3.5.dp.toPx(), it)
                }
            }
        }
    }
    @Suppress("UNUSED_EXPRESSION") type
}

/** 迷你趋势线。 */
@Composable
fun Sparkline(values: List<Float>, modifier: Modifier = Modifier, color: Color? = null) {
    val c = ReliefTheme.colors
    val col = color ?: c.accent
    if (values.size < 2) return
    val prog = rememberDrawProgress(values)
    Canvas(modifier) {
        val lo = values.min() - 0.1f
        val hi = values.max() + 0.1f
        val path = Path()
        var end = Offset.Zero
        values.forEachIndexed { i, v ->
            val x = 4.dp.toPx() + (size.width - 8.dp.toPx()) * i / (values.size - 1)
            val y = 4.dp.toPx() + (size.height - 8.dp.toPx()) * (hi - v) / (hi - lo)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            end = Offset(x, y)
        }
        val m = PathMeasure().apply { setPath(path, false) }
        val seg = Path()
        m.getSegment(0f, m.length * prog, seg, true)
        val b = if (color == null) Brush.horizontalGradient(listOf(c.accent, c.acc2)) else Brush.horizontalGradient(listOf(col, col))
        drawPath(seg, b, style = Stroke(2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        if (prog >= 1f) {
            drawCircle(Color.White, 5.dp.toPx(), end)
            drawCircle(if (color == null) c.acc2 else col, 3.dp.toPx(), end)
        }
    }
}

/** 柱状图。highlight 为高亮柱索引。 */
@Composable
fun BarChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    max: Float? = null,
    highlight: Int? = null,
    color: Color? = null,
    target: Float? = null,
    barHeight: Dp = 46.dp,
    description: String = "柱状图",
) {
    val c = ReliefTheme.colors
    val col = color ?: c.accent
    val top = (max ?: values.maxOrNull() ?: 1f).coerceAtLeast(target ?: 0f).coerceAtLeast(0.0001f)
    val prog = rememberDrawProgress(values)
    Row(modifier.fillMaxWidth().semantics { contentDescription = description }, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        values.forEachIndexed { i, v ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.fillMaxWidth().height(barHeight).clip(RoundedCornerShape(8.dp)), contentAlignment = Alignment.BottomCenter) {
                    Box(Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).then(Modifier).let { it })
                    Canvas(Modifier.fillMaxSize()) {
                        val cr = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                        drawRoundRect(c.track, cornerRadius = cr)
                        drawRoundRect(
                            Brush.linearGradient(listOf(c.shadow.copy(alpha = if (c.isDark) 0.6f else 0.30f), Color.Transparent, c.hi.copy(alpha = if (c.isDark) 0.25f else 0.8f)), Offset.Zero, Offset(size.width, size.height)),
                            cornerRadius = cr, style = Stroke(1.5.dp.toPx()),
                        )
                        val h = size.height * (v / top).coerceIn(0f, 1f) * prog
                        if (h > 0f) {
                            val a = if (highlight == null || highlight == i) 1f else 0.42f
                            val fill = if (color == null) Brush.verticalGradient(listOf(c.acc2.copy(alpha = a), c.accent.copy(alpha = a)), size.height - h, size.height)
                            else Brush.verticalGradient(listOf(lerp(col, Color.White, 0.25f).copy(alpha = a), col.copy(alpha = a)), size.height - h, size.height)
                            drawRoundRect(
                                fill,
                                topLeft = Offset(0f, size.height - h),
                                size = Size(size.width, h),
                                cornerRadius = cr,
                            )
                        }
                        if (target != null) {
                            val ty = size.height * (1 - target / top)
                            drawLine(c.ink2.copy(alpha = 0.5f), Offset(0f, ty), Offset(size.width, ty), 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
                        }
                    }
                }
                if (labels.isNotEmpty()) {
                    Text(
                        labels.getOrElse(i) { "" },
                        style = ReliefTheme.type.caption,
                        color = if (highlight == i) c.ink else c.ink2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

/** 小环形进度。 */
@Composable
fun MiniRing(progress: Float, modifier: Modifier = Modifier.size(34.dp), color: Color? = null) {
    val c = ReliefTheme.colors
    val p by animateFloatAsState(progress.coerceIn(0f, 1f), Motion.soft(), label = "mini")
    Canvas(modifier) {
        val sw = 4.dp.toPx()
        val tl = Offset(sw / 2, sw / 2)
        val arc = Size(size.width - sw, size.height - sw)
        grooveArc(c, tl, arc, sw)
        if (p > 0f) drawArc(ringBrush(c, color, tl, arc), -90f, 360f * p, false, tl, arc, style = Stroke(sw * 0.8f, cap = StrokeCap.Round))
    }
}
