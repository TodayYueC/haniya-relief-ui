package com.example.relief.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.relief.theme.ReliefMotion
import com.example.relief.theme.ReliefTheme
import com.example.relief.theme.LocalReducedMotion
import com.example.relief.theme.Motion
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 开关：浅槽、实心滑块、短缓动；保持 52×32 尺寸与 20dp 行程。
 * 保留点击、拖动与 Role.Switch 语义，使用共用材质绘制柔影。
 */
@Composable
fun LiquidSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    val c = ReliefTheme.colors
    val reduced = LocalReducedMotion.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val travelPx = with(density) { 20.dp.toPx() }
    val latestChecked by rememberUpdatedState(checked)
    val latestOnChange by rememberUpdatedState(onCheckedChange)
    val latestReduced by rememberUpdatedState(reduced)

    val pos = remember { Animatable(if (checked) 1f else 0f) }
    var pressed by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }
    var first by remember { mutableStateOf(true) }

    LaunchedEffect(checked, reduced) {
        if (first) {
            first = false
            pos.snapTo(if (checked) 1f else 0f)
            return@LaunchedEffect
        }
        if (reduced) {
            pos.snapTo(if (checked) 1f else 0f)
            return@LaunchedEffect
        }
        pos.animateTo(if (checked) 1f else 0f, tween(180, easing = ReliefMotion.StandardEasing))
    }

    val fill by androidx.compose.animation.core.animateFloatAsState(if (checked) 1f else 0f, Motion.standard(200), label = "track")
    val restKnob = if (c.isDark) lerp(c.glass2, Color.White, 0.10f) else lerp(c.bg, Color.White, 0.55f)
    val knobColor by animateColorAsState(
        if (pressed) lerp(restKnob, c.bg, 0.08f) else restKnob,
        Motion.standard(100), label = "knob",
    )

    Box(
        modifier
            .size(52.dp, 32.dp)
            .alpha(if (enabled) 1f else 0.45f)
            .semantics(mergeDescendants = true) {
                role = Role.Switch
                toggleableState = ToggleableState(checked)
                if (contentDescription != null) this.contentDescription = contentDescription
                onClick { if (enabled) latestOnChange(!latestChecked); enabled }
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown()
                    pressed = true
                    var moved = 0f
                    val startPos = pos.value
                    while (true) {
                        val ev = awaitPointerEvent()
                        val ch = ev.changes.firstOrNull { it.id == down.id } ?: break
                        if (!ch.pressed) break
                        val dx = ch.positionChange().x
                        moved += dx
                        if (abs(moved) > viewConfiguration.touchSlop) {
                            dragging = true
                            ch.consume()
                            scope.launch { pos.snapTo((startPos + moved / travelPx).coerceIn(0f, 1f)) }
                        }
                    }
                    pressed = false
                    if (dragging) {
                        dragging = false
                        val target = pos.value > 0.5f
                        if (target != latestChecked) {
                            latestOnChange(target)
                        } else {
                            scope.launch {
                                if (latestReduced) pos.snapTo(if (target) 1f else 0f)
                                else pos.animateTo(if (target) 1f else 0f, tween(180, easing = ReliefMotion.StandardEasing))
                            }
                        }
                    } else {
                        latestOnChange(!latestChecked)
                    }
                }
            },
    ) {
        Box(Modifier.size(52.dp, 32.dp).insetSurface(RoundedCornerShape(16.dp), depth = 3.dp)) {
            if (fill > 0f) Box(
                Modifier.size(52.dp, 32.dp).alpha(fill)
                    .gradientSurface(RoundedCornerShape(16.dp), brush = c.hBrush, shadow = false),
            )
        }
        Box(
            Modifier
                .align(AbsoluteAlignment.TopLeft)
                .absoluteOffset {
                    val inset = with(density) { 2.dp.roundToPx() }
                    IntOffset(inset + (pos.value.coerceIn(0f, 1f) * travelPx).roundToInt(), inset)
                }
                .size(28.dp)
                .dropShadow(CircleShape, Shadow(radius = 4.dp, color = c.shadow.copy(alpha = if (c.isDark) 0.9f else 0.55f), offset = DpOffset(1.5.dp, 2.dp)))
                .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(lerp(knobColor, Color.White, 0.5f), knobColor)), CircleShape)
                .glassSheen(CircleShape, c),
        )
    }
}
