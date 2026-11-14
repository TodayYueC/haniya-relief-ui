package com.example.relief.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.relief.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.relief.icon.HIcon
import com.example.relief.theme.ReliefMotion
import com.example.relief.theme.ReliefTheme
import com.example.relief.theme.LocalReducedMotion
import kotlinx.coroutines.delay

data class ToastMessage(
    val text: String,
    val undo: (() -> Unit)? = null,
    val icon: String = "check",
    val id: Long = System.nanoTime(),
    val durationMs: Long = if (undo != null) 3400 else 2400,
)

/** 全局提示条状态（可撤销）。 */
@Stable
class ToastState {
    var current by mutableStateOf<ToastMessage?>(null)
        private set

    fun show(text: String, undo: (() -> Unit)? = null, icon: String = "check", durationMs: Long? = null) {
        current = ToastMessage(text, undo, icon).let { if (durationMs != null) it.copy(durationMs = durationMs) else it }
    }

    fun dismiss() {
        current = null
    }
}

val LocalToast = staticCompositionLocalOf { ToastState() }

/** 提示条：自顶部落下，3.4 秒后收回；可带「撤销」。 */
@Composable
fun ToastHost(state: ToastState, modifier: Modifier = Modifier) {
    val c = ReliefTheme.colors
    val reduced = LocalReducedMotion.current
    var shown by remember { mutableStateOf<ToastMessage?>(null) }
    val msg = state.current
    LaunchedEffect(msg?.id) {
        if (msg != null) {
            shown = msg
            delay(msg.durationMs)
            if (state.current?.id == msg.id) state.dismiss()
        }
    }
    Box(modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(top = 14.dp), contentAlignment = Alignment.TopCenter) {
        AnimatedVisibility(
            visible = msg != null,
            enter = slideInVertically(if (reduced) androidx.compose.animation.core.snap() else ReliefMotion.spring()) { -it * 2 } + fadeIn(),
            exit = slideOutVertically(if (reduced) androidx.compose.animation.core.snap() else ReliefMotion.soft()) { -it * 2 } + fadeOut(),
        ) {
            val m = shown ?: return@AnimatedVisibility
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentWidth()
                    .heightIn(min = 48.dp)
                    .glass(RoundedCornerShape(Radii.pill), GlassLevel.Thick)
                    .semantics { liveRegion = LiveRegionMode.Polite }
                    .padding(start = 16.dp, end = if (m.undo != null) 6.dp else 18.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                HIcon(m.icon, size = 18.dp, tint = c.acc2)
                Text(m.text, style = ReliefTheme.type.bodyMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp)), maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                if (m.undo != null) {
                    Box(
                        Modifier
                            .heightIn(min = 36.dp)
                            .clip(RoundedCornerShape(Radii.pill))
                            .background(c.soft)
                            .pressable(onClick = {
                                m.undo.invoke()
                                state.show("已撤销")
                            })
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("撤销", style = ReliefTheme.type.label, color = c.accent) }
                }
            }
        }
    }
}
