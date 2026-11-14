package com.example.relief.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.relief.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.relief.icon.HIcon
import com.example.relief.theme.ReliefTheme
import com.example.relief.theme.Motion

/** 选择标签：未选为凸面胶囊，选中后按入内凹并以强调色标注。 */
@Composable
fun HChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null,
    height: Dp = 36.dp,
    subtitle: String? = null,
) {
    val c = ReliefTheme.colors
    val fg by animateColorAsState(if (selected) c.mid else c.ink, Motion.standard(220), label = "chipFg")
    val shape = RoundedCornerShape(Radii.pill)
    Row(
        modifier
            .height(if (subtitle != null) 48.dp else height)
            .then(if (selected) Modifier.insetSurface(shape, depth = 3.dp) else Modifier.glass(shape, GlassLevel.Thin))
            .pressable(onClick = onClick, role = Role.Button, pressedScale = 0.99f)
            .semantics { this.selected = selected }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (icon != null) {
            if (selected) HIcon(icon, size = 16.dp, brush = c.brush) else HIcon(icon, size = 16.dp, tint = fg)
        }
        if (subtitle != null) {
            androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text, style = ReliefTheme.type.buttonSmall, color = fg, maxLines = 1)
                Text(subtitle, style = ReliefTheme.type.caption, color = fg.copy(alpha = 0.8f), maxLines = 1)
            }
        } else {
            Text(text, style = ReliefTheme.type.buttonSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium), color = fg, maxLines = 1)
        }
    }
}

enum class BadgeTone { Accent, Acc2, Amber, Neutral }

@Composable
fun Badge(text: String, modifier: Modifier = Modifier, tone: BadgeTone = BadgeTone.Accent) {
    val c = ReliefTheme.colors
    val (bg, fg) = when (tone) {
        BadgeTone.Accent -> c.soft to c.accent
        BadgeTone.Acc2 -> c.soft2 to c.acc2
        BadgeTone.Amber -> c.amberSoft to c.amberInk
        BadgeTone.Neutral -> c.track to c.ink2
    }
    Box(
        modifier.height(24.dp).clip(RoundedCornerShape(Radii.pill)).background(bg).padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) { Text(text, style = ReliefTheme.type.badge, color = fg, maxLines = 1) }
}

/**
 * 分段控件：仅保留一层浅槽与轻薄滑块。
 */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accentThumb: Boolean = false,
) {
    val c = ReliefTheme.colors
    val shape = RoundedCornerShape(Radii.pill)
    BoxWithConstraints(modifier.height(46.dp).insetSurface(shape, depth = 3.5.dp).padding(4.dp)) {
        val segW = maxWidth / options.size
        val x by animateDpAsState(segW * selectedIndex, Motion.standard(180), label = "seg")
        Box(
            Modifier
                .offset(x = x)
                .width(segW)
                .fillMaxHeight()
                .glass(RoundedCornerShape(Radii.pill), GlassLevel.Thin),
        )
        Row(Modifier.fillMaxWidth().fillMaxHeight()) {
            options.forEachIndexed { i, label ->
                val on = i == selectedIndex
                val fg by animateColorAsState(
                    when {
                        on && accentThumb -> c.mid
                        on -> c.ink
                        else -> c.ink2
                    },
                    Motion.standard(180), label = "segFg",
                )
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(shape)
                        .selectable(selected = on, role = Role.Tab, onClick = { onSelect(i) }),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, style = ReliefTheme.type.buttonSmall, color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

/** 勾选圆：未完成为内凹小孔，完成后变成渐变釉面圆。 */
@Composable
fun CheckCircle(checked: Boolean, onClick: (() -> Unit)?, modifier: Modifier = Modifier, size: Dp = 26.dp, label: String = "") {
    val c = ReliefTheme.colors
    val s by animateFloatAsState(if (checked) 1f else 0.9f, Motion.standard(150), label = "check")
    Box(
        modifier
            .size(size)
            .then(if (checked) Modifier.gradientSurface(CircleShape, glow = 6.dp) else Modifier.insetSurface(CircleShape, depth = 2.5.dp))
            .then(if (onClick != null) Modifier.pressable(onClick = onClick, role = Role.Checkbox, pressedScale = 0.99f) else Modifier)
            .semantics {
                contentDescription = label
                stateDescription = if (checked) "已完成" else "未完成"
            },
        contentAlignment = Alignment.Center,
    ) {
        if (checked) HIcon("check", Modifier.scale(s), size = size * 0.64f, tint = Color.White)
    }
}

/** 五级选择器。 */
@Composable
fun LevelPicker(
    labels: List<String>,
    selected: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = ReliefTheme.colors
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEachIndexed { i, l ->
            val on = selected == i
            val shape = RoundedCornerShape(Radii.xs)
            Box(
                Modifier
                    .weight(1f)
                    .height(42.dp)
                    .then(if (on) Modifier.gradientSurface(shape, glow = 8.dp) else Modifier.glass(shape, GlassLevel.Thin))
                    .pressable(pressedScale = 0.99f, role = Role.RadioButton, onClick = { onSelect(i) })
                    .semantics { this.selected = on },
                contentAlignment = Alignment.Center,
            ) {
                Text(l, style = ReliefTheme.type.caption.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium), color = if (on) c.on else c.ink, maxLines = 1)
            }
        }
    }
}

/** 加减步进器：平整底座与两个轻触按钮，保留 44dp 触控区。 */
@Composable
fun Stepper(
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
    minusEnabled: Boolean = true,
    plusEnabled: Boolean = true,
    buttonSize: Dp = 44.dp,
    content: @Composable () -> Unit,
) {
    val c = ReliefTheme.colors
    Row(
        modifier.insetSurface(RoundedCornerShape(Radii.pill), depth = 3.dp).padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepButton("minus", "减少", onMinus, minusEnabled, buttonSize)
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { content() }
        StepButton("plus", "增加", onPlus, plusEnabled, buttonSize)
    }
}

@Composable
private fun StepButton(icon: String, desc: String, onClick: () -> Unit, enabled: Boolean, size: Dp) {
    val c = ReliefTheme.colors
    Box(
        Modifier
            .size(size)
            .glass(CircleShape, GlassLevel.Thin, shadow = enabled)
            .pressable(enabled = enabled, onClick = onClick, pressedScale = 0.99f)
            .semantics { contentDescription = desc },
        contentAlignment = Alignment.Center,
    ) { if (enabled) HIcon(icon, size = 20.dp, brush = c.brush) else HIcon(icon, size = 20.dp, tint = c.ink2.copy(alpha = 0.4f)) }
}

/** 进度条：内凹轨道 + 渐变釉面填充。 */
@Composable
fun ProgressBar(progress: Float, modifier: Modifier = Modifier, height: Dp = 6.dp, color: Color? = null) {
    val c = ReliefTheme.colors
    val p by animateFloatAsState(progress.coerceIn(0f, 1f), Motion.standard(220), label = "bar")
    val h = height.coerceAtLeast(8.dp)
    Box(modifier.fillMaxWidth().height(h).insetSurface(RoundedCornerShape(h), depth = (h / 4).coerceAtMost(3.dp)).padding(2.dp)) {
        if (p > 0.001f) Box(
            Modifier
                .fillMaxWidth(p.coerceIn(0.04f, 1f))
                .fillMaxHeight()
                .gradientSurface(
                    RoundedCornerShape(h),
                    brush = if (color != null) androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(color, androidx.compose.ui.graphics.lerp(color, Color.White, 0.25f))) else c.hBrush,
                    glow = 4.dp, shadow = false,
                ),
        )
    }
}

/** 三段占比条（如三大营养素）。 */
@Composable
fun MacroBar(protein: Float, carbs: Float, fat: Float, modifier: Modifier = Modifier) {
    val c = ReliefTheme.colors
    Row(modifier.fillMaxWidth().height(12.dp).insetSurface(RoundedCornerShape(6.dp), depth = 2.dp).padding(2.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        val total = (protein + carbs + fat).takeIf { it > 0f } ?: return@Row
        listOf(protein to c.accent, carbs to c.acc2, fat to c.amber).forEach { (v, col) ->
            if (v > 0f) Box(Modifier.weight(v / total).fillMaxHeight().clip(RoundedCornerShape(4.dp)).background(col))
        }
    }
}

/** 无底块的手绘图标，以主渐变上色（列表行、空状态）。 */
@Composable
fun IconChip(icon: String, modifier: Modifier = Modifier, size: Dp = 30.dp, acc2: Boolean = false) {
    val c = ReliefTheme.colors
    Box(
        modifier.size(size),
        contentAlignment = Alignment.Center,
    ) { HIcon(icon, size = size * 0.74f, brush = c.brush) }
}

/**
 * 浮雕图标底块：与卡片同色的凸起小方块，只让手绘线稿带上主渐变，
 * 颜色留在笔触里，底块保持陶瓷质感。[filled] 为 true 时底块略厚一级。
 */
@Composable
fun IconTile(icon: String, modifier: Modifier = Modifier, size: Dp = 40.dp, filled: Boolean = true) {
    val c = ReliefTheme.colors
    val shape = RoundedCornerShape(size * 0.32f)
    Box(
        modifier.size(size).glass(shape, GlassLevel.Thin),
        contentAlignment = Alignment.Center,
    ) {
        HIcon(icon, size = size * (if (filled) 0.58f else 0.56f), brush = c.brush)
    }
}

@Composable
fun Modifier.horizontalScreenPadding() = this.padding(horizontal = 16.dp)
