package com.example.relief.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import com.example.relief.theme.ReliefMotion
import com.example.relief.theme.LocalReliefColors
import com.example.relief.theme.LocalReducedMotion
import kotlinx.coroutines.launch

/**
 * 按压：内容轻微下沉，同时在面上叠一层「按入」光影（左上暗、右下亮），
 * 跟在 [glass] 之后时会被其形状裁切，看起来像浮雕被按进去。
 */
fun Modifier.pressable(
    enabled: Boolean = true,
    pressedScale: Float = 0.99f,
    role: Role? = Role.Button,
    onClickLabel: String? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val scale = remember { Animatable(1f) }
    val sink = remember { Animatable(0f) }
    val reduced = LocalReducedMotion.current
    val c = LocalReliefColors.current
    val last = remember { longArrayOf(0L) }
    val target = pressedScale.coerceAtMost(0.985f)
    LaunchedEffect(interaction, reduced) {
        interaction.interactions.collect { i ->
            launch {
                when (i) {
                    is PressInteraction.Press -> if (reduced) scale.snapTo(target) else scale.animateTo(target, ReliefMotion.press())
                    is PressInteraction.Release, is PressInteraction.Cancel ->
                        if (reduced) scale.snapTo(1f) else scale.animateTo(1f, ReliefMotion.spring())
                }
            }
            launch {
                when (i) {
                    is PressInteraction.Press -> if (reduced) sink.snapTo(1f) else sink.animateTo(1f, tween(90))
                    is PressInteraction.Release, is PressInteraction.Cancel -> if (reduced) sink.snapTo(0f) else sink.animateTo(0f, tween(220))
                }
            }
        }
    }
    this
        .drawWithContent {
            drawContent()
            val s = sink.value
            if (s > 0f) {
                drawRect(
                    Brush.linearGradient(
                        listOf(c.shadow.copy(alpha = (if (c.isDark) 0.35f else 0.20f) * s), Color.Transparent, c.hi.copy(alpha = (if (c.isDark) 0.10f else 0.35f) * s)),
                        start = Offset.Zero, end = Offset(size.width, size.height),
                    ),
                )
            }
        }
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .clickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled,
            role = role,
            onClickLabel = onClickLabel,
            onClick = {
                val now = android.os.SystemClock.uptimeMillis()
                if (now - last[0] >= 500) {
                    last[0] = now
                    onClick()
                }
            },
        )
}
