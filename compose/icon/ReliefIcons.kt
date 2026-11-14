package com.example.relief.icon

import android.graphics.PathMeasure
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser

/**
 * 24×24 手绘钢笔线稿图标。
 */
object ReliefIcons {
    private val paths = mapOf(
        "home" to "M3.2 10.6C6.1 8.2 9.2 5.4 12 3.3C15 5.7 18.1 8.2 20.8 10.8M4.6 10.1C4.4 13.6 4.8 17.2 4.5 20.4C6 20.7 7.5 20.5 9.1 20.6L9.2 14.8C11 14.6 12.8 14.9 14.7 14.8L14.9 20.6C16.4 20.5 18 20.7 19.5 20.4C19.7 16.9 19.4 13.7 19.6 10.1",
        "timer" to "M9.1 2.5Q12 2.2 14.9 2.6M12.1 2.7L11.9 5.3M12.2 6C7.9 5.8 4.4 9.1 4.6 13.6C4.4 18.1 7.9 21.2 12.2 20.9C16.7 21 20.1 17.8 19.8 13.3C19.6 9.1 16.6 6 12.2 6M12 9.5Q11.8 11.7 12.1 14L15 15.8M17.6 5.5L19.1 7.1",
        "chart" to "M3.5 20.8Q11.8 20.5 20.6 20.9M7.1 16.9Q6.8 13.8 7.2 10.7M12.1 17Q11.8 11.9 12.2 6.7M17.1 17.1Q17.4 12.6 17 8.7",
        "spark" to "M12.1 3.4Q12.9 6.3 14 8.3Q16.2 9.1 18.8 10.1Q16.2 11 13.9 11.9Q12.9 14.4 12 16.8Q11.1 14.1 10.2 11.9Q7.7 10.8 5.2 10Q7.8 9.1 10.2 8.2Q11.3 5.9 12.1 3.4M19.2 3.4L19 6.9M17.5 5.1L20.8 5.2M5.1 17.1L4.9 20.8M3.2 19.1L6.8 18.9",
        "user" to "M12.1 3.6C9.9 3.4 8.1 5.2 8.2 7.6C8.1 10 9.8 11.8 12 11.7C14.4 11.8 16 10 15.9 7.6C16 5.2 14.3 3.5 12.1 3.6M4.6 20.7C4.7 16.4 7.8 13.9 12 14C16.1 13.8 19.3 16.5 19.5 20.6",
        "plus" to "M12.1 5.1Q11.8 11.9 12.1 18.8M5.1 12.1Q12 11.8 18.9 12",
        "minus" to "M5.2 12.1Q12 11.8 18.9 12",
        "drop" to "M12 3.2C10.2 5.5 6 10.2 6.1 14C6 17.7 8.4 20.8 12.1 20.6C15.8 20.8 18.1 17.7 17.9 14C18 10.1 13.9 5.3 12 3.2M8.7 14.8Q8.9 17.1 10.8 17.9",
        "scale" to "M4 7a3 3 0 0 1 3-3h10a3 3 0 0 1 3 3v10a3 3 0 0 1-3 3H7a3 3 0 0 1-3-3zM8.5 10a5 5 0 0 1 7 0M12 10l1.2-1.6",
        "run" to "M14 4.5a1.5 1.5 0 1 0 0 .01M8 21l3-6 3 2.5V21M10.5 14l1.3-5 3.2 3H19M11.8 9 8 10l-2 3",
        "moon" to "M19.8 14.8C15.9 16.4 10.9 14 9.3 10.4C8.3 8.1 8.4 6 9.3 4C5.7 5.3 3.8 8.1 4 11.9C3.8 16.8 7.7 20.3 12.4 20.2C15.9 20.3 18.9 18 19.8 14.8",
        "check" to "M5.2 12.6Q7.4 14.5 9.5 16.8Q14.2 11.9 18.9 7.2",
        "habit" to "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0zM8 12.5l2.8 2.8L16 10",
        "bowl" to "M3 11h18a9 9 0 0 1-18 0zM8 7.5c0-1.5 1-2 1-3.5M12 7.5c0-1.5 1-2 1-3.5M16 7.5c0-1.5 1-2 1-3.5",
        "pulse" to "M3 12h4l2.5-6 4 12 2.5-6H21",
        "note" to "M6.2 3.4Q10.6 3.2 14.9 3.5L18.8 7.3Q19.1 13.7 18.8 20.6Q12.3 20.8 6.1 20.5Q5.8 12 6.2 3.4M14.4 3.8L14.6 7.8L18.5 7.7M9.1 12.1Q12.5 11.8 15.9 12.2M9.2 16.1Q11.6 15.9 14.1 16.2",
        "bell" to "M6 16v-5a6 6 0 0 1 12 0v5l1.5 2h-15zM10 21h4",
        "layout" to "M4 4h7v7H4zM13 4h7v4h-7zM13 10h7v10h-7zM4 13h7v7H4z",
        "chevR" to "M9.1 6.2Q11.9 8.9 14.8 12.1Q12 15.2 9.2 17.8",
        "chevL" to "M14.9 6.2Q12.1 9 9.2 12Q12 15.1 14.8 17.8",
        "chevU" to "M6.2 14.9Q9.1 12.2 12 9.2Q15 12 17.8 14.8",
        "chevD" to "M6.2 9.1Q9.2 12 12.1 14.8Q15.1 12 17.8 9.2",
        "close" to "M6.3 6.1Q12.1 11.8 17.8 18M17.9 6.2Q12 12.1 6.2 17.8",
        "eyeOff" to "M3 3l18 18M10.6 6.1A9.8 9.8 0 0 1 12 6c5 0 9 6 9 6a16 16 0 0 1-2.6 3.2M6.6 7.6C4.3 9.2 3 12 3 12s4 6 9 6c1.5 0 2.9-.5 4.1-1.2",
        "eye" to "M3 12s4-6 9-6 9 6 9 6-4 6-9 6-9-6-9-6zM12 14.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z",
        "shrink" to "M4 14h6v6M20 10h-6V4M14 10l7-7M3 21l7-7",
        "expand" to "M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7",
        "refresh" to "M20 11a8 8 0 0 0-14.5-4.5L4 8M4 4v4h4M4 13a8 8 0 0 0 14.5 4.5L20 16M20 20v-4h-4",
        "send" to "M5 12h14M13 6l6 6-6 6",
        "arrow" to "M5 12h14M13 6l6 6-6 6",
        "sliders" to "M4 7h10M18 7h2M4 17h4M12 17h8M16 5v4M10 15v4",
        "cal" to "M4 6h16v14H4zM4 10h16M8 3v4M16 3v4",
        "camera" to "M4 8h3l2-3h6l2 3h3v11H4zM12 17a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z",
        "image" to "M4 5h16v14H4zM4 16l5-5 4 4 3-3 4 4M15.5 9.5v.1",
        "shield" to "M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6zM9 12l2 2 4-4",
        "shieldAlert" to "M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6zM12 9v4M12 16v.1",
        "target" to "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0zM16 12a4 4 0 1 1-8 0 4 4 0 0 1 8 0z",
        "db" to "M4 6c0-1.7 3.6-3 8-3s8 1.3 8 3-3.6 3-8 3-8-1.3-8-3zM4 6v12c0 1.7 3.6 3 8 3s8-1.3 8-3V6M4 12c0 1.7 3.6 3 8 3s8-1.3 8-3",
        "globe" to "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0zM3 12h18M12 3c2.5 2.5 3.5 5.5 3.5 9s-1 6.5-3.5 9c-2.5-2.5-3.5-5.5-3.5-9S9.5 5.5 12 3z",
        "help" to "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0zM9.5 9.5a2.5 2.5 0 1 1 3.5 2.3c-.6.3-1 .8-1 1.5V14M12 17.2v.1",
        "info" to "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0zM12 11v6M12 7.5v.1",
        "clock" to "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0zM12 7v5l3 2",
        "play" to "M8 5l11 7-11 7z",
        "stop" to "M7 7h10v10H7z",
        "ruler" to "M4 16 16 4l4 4L8 20zM8 12l2 2M11 9l2 2M14 6l2 2",
        "lock" to "M6 11h12v10H6zM8 11V8a4 4 0 0 1 8 0v3",
        "list" to "M8 6h12M8 12h12M8 18h12M4 6h.01M4 12h.01M4 18h.01",
        "link" to "M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1 1M14 10a4 4 0 0 0-5.7 0l-3 3a4 4 0 0 0 5.7 5.7l1-1",
        "qr" to "M4 4h6v6H4zM14 4h6v6h-6zM4 14h6v6H4zM14 14h2v2h-2zM18 18h2v2h-2zM14 18h2M18 14h2",
        "search" to "M10.6 4.1C6.9 4 4 7.1 4.2 10.8C4.1 14.6 7 17.4 10.8 17.2C14.6 17.4 17.3 14.3 17.2 10.6C17.3 6.9 14.4 4 10.6 4.1M15.5 15.7Q17.9 18 20.2 20.1",
        "trash" to "M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13M10 11v6M14 11v6",
        "edit" to "M4 20h4L19 9l-4-4L4 16zM13 7l4 4",
        "copy" to "M8 8h12v12H8zM4 16V4h12",
        "download" to "M12 4v11M7 10l5 5 5-5M4 20h16",
        "upload" to "M12 16V5M7 10l5-5 5 5M4 20h16",
        "drag" to "M9 6h.01M15 6h.01M9 12h.01M15 12h.01M9 18h.01M15 18h.01",
        "pause" to "M8 5v14M16 5v14",
        "flame" to "M12 21c-3.9 0-7-2.8-7-6.6 0-3.1 2.1-5 3.6-6.6.4 1.6 1.4 2.6 2.4 3 0-3.2 1.6-5.8 4-7.8.3 2.8 1.6 4.6 2.8 6.1 1 1.3 1.2 2.6 1.2 3.9C19 18 15.9 21 12 21z",
        "heart" to "M12.1 20.1C9 18.1 4.7 14.6 4.8 9.8C4.7 6.1 9.1 4.3 12 7.5C15 4.2 19.3 6.1 19.2 9.9C19.4 14.5 15.2 18 12.1 20.1",
        "logout" to "M15 4h4v16h-4M10 8l-4 4 4 4M6 12h10",
        "key" to "M14 10a4 4 0 1 0-3.5 4L8 16.5V19h2.5v-2h2v-2l1.3-1.3A4 4 0 0 0 14 10z",
        "palette" to "M12 21a9 9 0 1 1 9-9c0 2-1.5 3-3.5 3H16a2 2 0 0 0-1.5 3.3c.4.5.4 1.2 0 1.7-.6.6-1.5 1-2.5 1zM7.5 11.5v.1M10 7.5v.1M14.5 7.5v.1",
    )

    private val cache = HashMap<String, ImageVector>()

    /**
     * 手绘线稿：主笔触带低频抖动、起笔回锋与收笔出头，闭合图形故意不完全接上；
     * 再叠一条更细更淡的「复笔」，像钢笔速写时第二次描线。固定种子 + 缓存，重组不抖。
     */
    fun get(name: String): ImageVector = cache.getOrPut(name) {
        val data = paths[name] ?: paths.getValue("info")
        val seed = name.hashCode()
        val builder = ImageVector.Builder(name = name, defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f)
        sketch(data, seed, amplitude = 0.34f, overshoot = 0.55f, trim = 0f).forEach { d ->
            builder.addPath(
                pathData = addPathNodes(d), fill = null,
                stroke = SolidColor(Color.Black), strokeAlpha = 1f, strokeLineWidth = 1.6f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round,
            )
        }
        sketch(data, seed * 31 + 7, amplitude = 0.55f, overshoot = 0.2f, trim = 0.08f).forEach { d ->
            builder.addPath(
                pathData = addPathNodes(d), fill = null,
                stroke = SolidColor(Color.Black), strokeAlpha = 0.34f, strokeLineWidth = 0.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round,
            )
        }
        builder.build()
    }

    /** 可重复的伪随机（0..1）。 */
    private fun rnd(seed: Int, i: Int): Float {
        var x = seed * 374761393 + i * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0xffff) / 65535f
    }

    private fun f(v: Float) = String.format(java.util.Locale.US, "%.2f", v)

    private fun sketch(data: String, seed: Int, amplitude: Float, overshoot: Float, trim: Float): List<String> {
        val path = PathParser.createPathFromPathData(data)
        val measure = PathMeasure(path, false)
        val pos = FloatArray(2)
        val tan = FloatArray(2)
        val out = mutableListOf<String>()
        var contour = 0
        do {
            val length = measure.length
            if (length <= 0f) continue
            val cs = seed + contour * 977
            contour++
            if (length < 1.2f) {
                // 点与极短笔画：画成一个小小的落笔点。
                measure.getPosTan(0f, pos, tan)
                val dx = (rnd(cs, 1) - .5f) * .3f
                out += "M${f(pos[0])} ${f(pos[1])}L${f(pos[0] + .15f + dx)} ${f(pos[1] + .1f)}"
                continue
            }
            val closed = measure.isClosed
            // 低频抖动：两组正弦叠加，频率随种子略变，避免所有图标同一种「手抖」。
            val p1 = rnd(cs, 2) * 6.28f
            val p2 = rnd(cs, 3) * 6.28f
            val w1 = 6.28f / (6.5f + rnd(cs, 4) * 3f)
            val w2 = 6.28f / (2.6f + rnd(cs, 5) * 1.2f)
            val amp = amplitude * (0.7f + rnd(cs, 6) * 0.6f) * (length / 6f).coerceIn(0.35f, 1f)
            fun wobble(s: Float) = amp * (0.65f * kotlin.math.sin(w1 * s + p1) + 0.35f * kotlin.math.sin(w2 * s + p2))

            val startS = length * trim
            val endS = if (closed) length + (length * 0.12f).coerceIn(0.8f, 2.2f) else length * (1f - trim)
            val step = 0.9f
            val n = ((endS - startS) / step).toInt().coerceAtLeast(2)
            val pts = ArrayList<Pair<Float, Float>>(n + 3)
            for (i in 0..n) {
                val s = startS + (endS - startS) * i / n
                val wrapped = if (closed) s % length else s
                measure.getPosTan(wrapped, pos, tan)
                // 闭合图形收尾时向外偏一点，形成手绘的「没对上」。
                val drift = wobble(s) + if (closed && s > length) (s - length) * 0.28f else 0f
                pts += (pos[0] - tan[1] * drift) to (pos[1] + tan[0] * drift)
            }
            if (!closed && overshoot > 0f) {
                // 起笔略早、收笔出头。
                measure.getPosTan(startS, pos, tan)
                val o0 = overshoot * (0.4f + rnd(cs, 7) * 0.5f)
                pts.add(0, (pts.first().first - tan[0] * o0) to (pts.first().second - tan[1] * o0))
                measure.getPosTan(length * (1f - trim), pos, tan)
                val o1 = overshoot * (0.6f + rnd(cs, 8) * 0.6f)
                pts += (pts.last().first + tan[0] * o1) to (pts.last().second + tan[1] * o1)
            }
            // 用中点二次贝塞尔平滑连接采样点。
            val sb = StringBuilder()
            sb.append('M').append(f(pts[0].first)).append(' ').append(f(pts[0].second))
            for (i in 1 until pts.size - 1) {
                val (x, y) = pts[i]
                val (nx, ny) = pts[i + 1]
                sb.append('Q').append(f(x)).append(' ').append(f(y)).append(' ')
                    .append(f((x + nx) / 2)).append(' ').append(f((y + ny) / 2))
            }
            sb.append('L').append(f(pts.last().first)).append(' ').append(f(pts.last().second))
            out += sb.toString()
        } while (measure.nextContour())
        return out.ifEmpty { listOf(data) }
    }

    fun has(name: String) = name in paths
}

/**
 * 图标。默认跟随文字色；传入 [brush] 时以渐变上色（例如强调色图标）。
 */
@Composable
fun HIcon(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null,
    brush: Brush? = null,
) {
    if (brush == null) {
        Icon(ReliefIcons.get(name), contentDescription, modifier.size(size), tint = tint)
    } else {
        Image(
            rememberVectorPainter(ReliefIcons.get(name)),
            contentDescription,
            modifier
                .size(size)
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithContent {
                    drawContent()
                    drawRect(brush, blendMode = BlendMode.SrcIn)
                },
        )
    }
}
