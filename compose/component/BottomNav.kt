package com.example.relief.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.relief.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.relief.icon.HIcon
import com.example.relief.theme.ReliefTheme
import com.example.relief.theme.Motion

data class NavItem(val key: String, val label: String, val icon: String)

@Composable
fun navigationContentBottomPadding() = 112.dp

/**
 * 悬浮浮雕导航：只用图标；整条为凸面胶囊，选中项被「按入」成内凹槽，图标以主渐变点亮；
 * 右侧渐变釉面圆键用于快速记录。
 */
@Composable
fun GlassBottomNav(
    items: List<NavItem>,
    selected: Int,
    onSelect: (Int) -> Unit,
    onQuickRecord: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = ReliefTheme.colors
    val h = 62.dp
    val quickLabel = tr("快速记录")
    Row(
        modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            Modifier.weight(1f).height(h)
                .glass(RoundedCornerShape(Radii.lg), GlassLevel.Thick)
                .padding(5.dp),
        ) {
            items.forEachIndexed { i, item ->
                val on = i == selected
                val label = tr(item.label)
                val well by animateFloatAsState(if (on) 1f else 0f, Motion.standard(200), label = "navWell")
                val col by animateColorAsState(if (on) c.mid else c.ink2, Motion.standard(160), label = "navCol")
                Box(
                    Modifier.weight(1f).fillMaxHeight()
                        .clip(RoundedCornerShape(Radii.md))
                        .selectable(selected = on, role = Role.Tab, onClick = { onSelect(i) })
                        .semantics { contentDescription = label },
                    contentAlignment = Alignment.Center,
                ) {
                    if (well > 0.01f) Box(
                        Modifier.fillMaxSize().padding(horizontal = 1.dp).alpha(well)
                            .insetSurface(RoundedCornerShape(Radii.md), depth = 3.dp),
                    )
                    if (on) HIcon(item.icon, size = 25.dp, brush = c.brush) else HIcon(item.icon, size = 25.dp, tint = col)
                }
            }
        }
        Box(
            Modifier.size(58.dp)
                .gradientSurface(CircleShape, glow = 14.dp)
                .pressable(onClick = onQuickRecord, pressedScale = 0.96f, onClickLabel = "快速记录")
                .semantics { contentDescription = quickLabel },
            contentAlignment = Alignment.Center,
        ) { HIcon("plus", size = 26.dp, tint = Color.White) }
    }
}
