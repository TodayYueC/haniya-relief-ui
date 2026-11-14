package com.example.relief.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.relief.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.relief.icon.HIcon
import com.example.relief.theme.ReliefTheme

enum class ButtonSize(val height: Dp, val hPad: Dp) { Large(48.dp, 20.dp), Small(40.dp, 16.dp) }

/** 主按钮：渐变釉面胶囊 + 同色辉光。 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Large,
    icon: String? = null,
    enabled: Boolean = true,
    color: Color? = null,
) {
    val c = ReliefTheme.colors
    val shape = RoundedCornerShape(Radii.pill)
    val brush = if (color != null) androidx.compose.ui.graphics.Brush.linearGradient(listOf(color, androidx.compose.ui.graphics.lerp(color, Color.White, 0.18f))) else c.brush
    Row(
        modifier
            .height(size.height)
            .alpha(if (enabled) 1f else 0.5f)
            .gradientSurface(shape, brush, glow = if (size == ButtonSize.Large) 14.dp else 10.dp, shadow = enabled)
            .pressable(enabled = enabled, pressedScale = 0.99f, onClick = onClick)
            .padding(horizontal = size.hPad),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) HIcon(icon, size = if (size == ButtonSize.Large) 20.dp else 18.dp, tint = c.on)
        Text(
            text,
            style = if (size == ButtonSize.Large) ReliefTheme.type.button else ReliefTheme.type.buttonSmall,
            color = c.on, maxLines = 1, overflow = TextOverflow.Ellipsis,
        )
    }
}

/** 次按钮：同色凸面胶囊。 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Large,
    icon: String? = null,
    enabled: Boolean = true,
    contentColor: Color? = null,
) {
    val c = ReliefTheme.colors
    val shape = RoundedCornerShape(Radii.pill)
    Row(
        modifier
            .height(size.height)
            .alpha(if (enabled) 1f else 0.5f)
            .glass(shape, GlassLevel.Thin, shadow = enabled)
            .pressable(enabled = enabled, pressedScale = 0.99f, onClick = onClick)
            .padding(horizontal = size.hPad),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val fg = contentColor ?: c.ink
        if (icon != null) {
            if (contentColor == null) HIcon(icon, size = 18.dp, brush = c.brush) else HIcon(icon, size = 18.dp, tint = fg)
        }
        Text(
            text, style = if (size == ButtonSize.Large) ReliefTheme.type.button else ReliefTheme.type.buttonSmall,
            color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis,
        )
    }
}

/** 44dp 圆形凸面图标按钮。 */
@Composable
fun IconCircleButton(
    icon: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    tint: Color? = null,
    level: GlassLevel = GlassLevel.Regular,
    iconSize: Dp = 20.dp,
) {
    val c = ReliefTheme.colors
    Box(
        modifier
            .size(size)
            .glass(CircleShape, GlassLevel.Thin)
            .pressable(onClick = onClick, pressedScale = 0.99f, onClickLabel = contentDescription)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) { HIcon(icon, size = iconSize, tint = tint ?: c.ink) }
}

/** 小号的圆形按钮（弹层关闭、返回）。 */
@Composable
fun SmallRoundButton(icon: String, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val c = ReliefTheme.colors
    Box(
        modifier
            .size(36.dp)
            .glass(CircleShape, GlassLevel.Thin)
            .pressable(onClick = onClick, pressedScale = 0.99f)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) { HIcon(icon, size = 18.dp, tint = c.ink2) }
}

/** 文字链接按钮。 */
@Composable
fun TextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color? = null,
    icon: String? = null,
) {
    val c = ReliefTheme.colors
    Row(
        modifier
            .clip(RoundedCornerShape(Radii.xs))
            .pressable(onClick = onClick, pressedScale = 0.99f)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text, style = ReliefTheme.type.label, color = color ?: c.mid, textAlign = TextAlign.Center)
        if (icon != null) HIcon(icon, size = 16.dp, tint = color ?: c.mid)
    }
}

/** 撑满宽度的底部主按钮行。 */
@Composable
fun ButtonRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically, content = content)
}

val DefaultContentPadding = PaddingValues(horizontal = 16.dp)

@Suppress("unused")
private val ButtonRole = Role.Button
