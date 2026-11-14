package com.example.relief.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.example.relief.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.relief.theme.ReliefMotion
import com.example.relief.theme.ReliefTheme
import com.example.relief.theme.LocalReducedMotion
import kotlin.math.roundToInt

/**
 * 底部凸面弹层：左右下各内缩 8dp、圆角 38、最高 88%；自底部滑入（柔和弹簧）+ 背景压暗。
 * 放在屏幕根部的 Box 中，覆盖在内容之上。
 */
@Composable
fun GlassSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = ReliefTheme.colors
    val reduced = LocalReducedMotion.current
    if (visible) BackHandler(onBack = onBack ?: onDismiss)
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(if (reduced) androidx.compose.animation.core.snap() else androidx.compose.animation.core.tween(450)),
            exit = fadeOut(if (reduced) androidx.compose.animation.core.snap() else androidx.compose.animation.core.tween(350)),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(c.scrim)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
            )
        }
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(if (reduced) androidx.compose.animation.core.snap() else ReliefMotion.soft()) { it + 60 },
            exit = slideOutVertically(if (reduced) androidx.compose.animation.core.snap() else ReliefMotion.soft()) { it + 60 },
        ) {
            var dragY by remember { mutableFloatStateOf(0f) }
            val maxH = LocalConfiguration.current.screenHeightDp.dp * 0.88f
            Column(
                modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .imePadding()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .heightIn(max = maxH)
                    .offset { IntOffset(0, dragY.coerceAtLeast(0f).roundToInt()) }
                    .glass(RoundedCornerShape(Radii.sheet), GlassLevel.Thick)
                    .semantics { paneTitle = title ?: "弹层" }
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 22.dp),
            ) {
                // 拖动把手
                Box(
                    Modifier
                        .fillMaxWidth()
                        .draggable(
                            rememberDraggableState { d -> dragY += d },
                            Orientation.Vertical,
                            onDragStopped = { v -> if (dragY > 140f || v > 1800f) onDismiss(); dragY = 0f },
                        )
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(Modifier.width(38.dp).padding(vertical = 0.dp).insetSurface(RoundedCornerShape(3.dp)).size(38.dp, 5.dp))
                }
                if (title != null) {
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (onBack != null) SmallRoundButton("chevL", "返回", onBack)
                        Text(title, style = ReliefTheme.type.title.copy(fontSize = androidx.compose.ui.unit.TextUnit(19f, androidx.compose.ui.unit.TextUnitType.Sp)), modifier = Modifier.weight(1f))
                        SmallRoundButton("close", "关闭", onDismiss)
                    }
                } else {
                    Spacer(Modifier.size(8.dp))
                }
                Column(Modifier.verticalScroll(rememberScrollState()), content = content)
            }
        }
    }
}
