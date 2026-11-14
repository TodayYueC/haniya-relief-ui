package com.example.relief.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import com.example.relief.component.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.relief.icon.HIcon
import com.example.relief.theme.ReliefMotion
import com.example.relief.theme.ReliefTheme
import com.example.relief.theme.LocalReducedMotion
import com.example.relief.theme.Motion
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** 柔和凸面卡片：r26、pad16。 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    radius: Dp = Radii.card,
    padding: PaddingValues = PaddingValues(18.dp),
    level: GlassLevel = GlassLevel.Regular,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .glass(RoundedCornerShape(radius), level)
            .then(if (onClick != null) Modifier.pressable(pressedScale = 1f, onClick = onClick) else Modifier)
            .padding(padding),
        content = content,
    )
}

/** 卡片标题行：图标 + 标题 + 说明 + 右侧操作。 */
@Composable
fun CardHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: String? = null,
    caption: String? = null,
    acc2: Boolean = false,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val c = ReliefTheme.colors
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        if (icon != null) IconTile(icon, size = 36.dp)
        Column(Modifier.weight(1f)) {
            Text(title, style = ReliefTheme.type.h3, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (caption != null) Text(caption, style = ReliefTheme.type.caption, color = c.ink2, maxLines = 2)
        }
        trailing()
    }
}

/** 页面内分组标题。 */
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier, caption: String? = null, action: (@Composable () -> Unit)? = null) {
    val c = ReliefTheme.colors
    Row(modifier.fillMaxWidth().padding(top = 22.dp, bottom = 12.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text, style = ReliefTheme.type.title, modifier = Modifier.semantics { heading() })
        Spacer(Modifier.weight(1f))
        if (caption != null) Text(caption, style = ReliefTheme.type.caption, color = c.ink2)
        action?.invoke()
    }
}

/** 小标签（表单字段名）。 */
@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(text, style = ReliefTheme.type.label, color = ReliefTheme.colors.ink2, modifier = modifier.padding(top = 16.dp, bottom = 8.dp))
}

/** 设置/列表行：最小高度 52。 */
@Composable
fun ListRow(
    title: String,
    modifier: Modifier = Modifier,
    icon: String? = null,
    subtitle: String? = null,
    value: String? = null,
    titleColor: Color? = null,
    chevron: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val c = ReliefTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .then(if (onClick != null) Modifier.pressable(pressedScale = 1f, onClick = onClick) else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (icon != null) IconTile(icon, size = 34.dp, filled = false)
        Column(Modifier.weight(1f)) {
            Text(title, style = ReliefTheme.type.bodyMedium, color = titleColor ?: c.ink, maxLines = 2)
            if (subtitle != null) Text(subtitle, style = ReliefTheme.type.caption, color = c.ink2)
        }
        // 值最多占约一半宽度，避免长文案（如德语）把标题挤成逐字换行。
        if (value != null) Text(
            value, style = ReliefTheme.type.body, color = c.ink2, maxLines = 2,
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 156.dp),
        )
        if (trailing != null) trailing()
        else if (chevron && onClick != null) HIcon("chevR", size = 18.dp, tint = c.ink2.copy(alpha = 0.7f))
    }
}

@Composable
fun Divider(modifier: Modifier = Modifier) {
    val c = ReliefTheme.colors
    // 刻线：一道暗线 + 一道亮线，像在陶瓷面上刻出的细槽。
    Column(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(c.shadow.copy(alpha = if (c.isDark) 0.6f else 0.22f)))
        Box(Modifier.fillMaxWidth().height(1.dp).background(c.hi.copy(alpha = if (c.isDark) 0.35f else 0.9f)))
    }
}

/** 开关行。 */
@Composable
fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: String? = null,
    enabled: Boolean = true,
) {
    ListRow(
        title = title, subtitle = subtitle, icon = icon, chevron = false, modifier = modifier,
        trailing = { LiquidSwitch(checked, onCheckedChange, enabled = enabled, contentDescription = title) },
    )
}

/** 内凹输入框：h48、r16，聚焦时以强调色标明输入位置。 */
@Composable
fun GlassField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    minHeight: Dp = 48.dp,
    keyboardType: KeyboardType = KeyboardType.Text,
    textStyle: TextStyle? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    imeAction: androidx.compose.ui.text.input.ImeAction = androidx.compose.ui.text.input.ImeAction.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    val c = ReliefTheme.colors
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(Radii.sm)
    val style = (textStyle ?: ReliefTheme.type.body.copy(fontSize = androidx.compose.ui.unit.TextUnit(15f, androidx.compose.ui.unit.TextUnitType.Sp))).copy(color = c.ink)
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .insetSurface(shape, depth = 3.5.dp)
            .border(1.2.dp, if (focused) c.mid.copy(alpha = 0.55f) else Color.Transparent, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = style,
            cursorBrush = SolidColor(c.accent),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            modifier = Modifier.weight(1f).onFocusChanged { focused = it.isFocused },
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) Text(placeholder, style = style.copy(color = c.ink2))
                    inner()
                }
            },
        )
        trailing?.invoke()
    }
}

/** 空状态：说明 + 记录入口。 */
@Composable
fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: String = "note",
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val c = ReliefTheme.colors
    Column(modifier.fillMaxWidth().padding(vertical = 18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        IconTile(icon, size = 56.dp, filled = false, modifier = Modifier.padding(bottom = 6.dp))
        Text(title, style = ReliefTheme.type.h3, color = c.ink)
        if (subtitle != null) Text(subtitle, style = ReliefTheme.type.caption, color = c.ink2, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        if (action != null && onAction != null) GlassButton(action, onAction, size = ButtonSize.Small, modifier = Modifier.padding(top = 4.dp))
    }
}

/** 二级页顶部：返回 + 标题 + 副标题 + 右侧操作。 */
@Composable
fun PageHeader(
    title: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val c = ReliefTheme.colors
    Row(
        modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(top = 12.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (onBack != null) IconCircleButton("chevL", "返回", onBack)
        Column(Modifier.weight(1f)) {
            Text(title, style = ReliefTheme.type.pageTitle, modifier = Modifier.semantics { heading() }, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) Text(subtitle, style = ReliefTheme.type.caption, color = c.ink2, maxLines = 1)
        }
        actions()
    }
}

/** 一级页大标题。 */
@Composable
fun TabHeader(title: String, modifier: Modifier = Modifier, actions: @Composable RowScope.() -> Unit = {}) {
    Row(
        modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(top = 14.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(title, style = ReliefTheme.type.display, modifier = Modifier.weight(1f).semantics { heading() })
        actions()
    }
}

/** Surfaces stay still; movement is reserved for navigation and user actions. */
@Suppress("UNUSED_PARAMETER")
fun Modifier.staggerIn(index: Int, key: Any? = Unit): Modifier = this

/** 展开区：柔和弹簧过渡高度。 */
@Composable
fun Expandable(expanded: Boolean, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val alpha by animateFloatAsState(if (expanded) 1f else 0f, Motion.standard(350), label = "exp")
    Column(modifier.animateContentSize(Motion.soft())) {
        if (expanded) Column(Modifier.graphicsLayer { this.alpha = alpha }, content = content)
    }
}

@Composable
fun Chevron(open: Boolean, modifier: Modifier = Modifier) {
    val r by animateFloatAsState(if (open) 180f else 0f, Motion.spring(), label = "chev")
    HIcon("chevD", modifier.rotate(r), size = 18.dp, tint = ReliefTheme.colors.ink2)
}

/** 今天 / 昨天 / 前天 / 其他日期。 */
@Composable
fun DateChips(selected: LocalDate, onSelect: (LocalDate) -> Unit, modifier: Modifier = Modifier, today: LocalDate = LocalDate.now()) {
    var picker by remember { mutableStateOf(false) }
    val quick = listOf(today to "今天", today.minusDays(1) to "昨天", today.minusDays(2) to "前天")
    Row(modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        quick.forEach { (d, l) -> HChip(l, selected == d, { onSelect(d) }) }
        val other = quick.none { it.first == selected }
        HChip(if (other) "${selected.monthValue}月${selected.dayOfMonth}日" else "其他日期", other, { picker = true }, icon = "cal")
    }
    if (picker) HDatePicker(selected, onDismiss = { picker = false }, onPick = { onSelect(it); picker = false }, maxDate = today)
}

@Composable
fun HDatePicker(initial: LocalDate, onDismiss: () -> Unit, onPick: (LocalDate) -> Unit, maxDate: LocalDate? = LocalDate.now()) {
    val c = ReliefTheme.colors
    val maxMillis = maxDate?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initial.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = maxMillis == null || utcTimeMillis <= maxMillis
        },
    )
    val colors = DatePickerDefaults.colors(
        containerColor = c.glass2,
        titleContentColor = c.ink2,
        headlineContentColor = c.ink,
        weekdayContentColor = c.ink2,
        subheadContentColor = c.ink2,
        yearContentColor = c.ink,
        currentYearContentColor = c.accent,
        selectedYearContainerColor = c.accent,
        selectedYearContentColor = c.on,
        dayContentColor = c.ink,
        selectedDayContainerColor = c.accent,
        selectedDayContentColor = c.on,
        todayDateBorderColor = c.accent,
        todayContentColor = c.accent,
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { onPick(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()) } ?: onDismiss()
            }) { Text("确定", color = c.accent) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消", color = c.ink2) } },
        colors = colors,
        shape = RoundedCornerShape(Radii.lg),
    ) { DatePicker(state, colors = colors, showModeToggle = false) }
}

/** 键值小块（标签 + 数值）。 */
@Composable
fun KeyValue(key: String, value: String, modifier: Modifier = Modifier) {
    val c = ReliefTheme.colors
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(key, style = ReliefTheme.type.caption, color = c.ink2)
        Text(value, style = com.example.relief.theme.NumStyles.tiny.copy(fontSize = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp)), color = c.ink)
    }
}

/** 统计小块（内凹）。 */
@Composable
fun StatTile(label: String, value: String, modifier: Modifier = Modifier, unit: String? = null, sub: String? = null, onClick: (() -> Unit)? = null) {
    val c = ReliefTheme.colors
    Column(
        modifier
            .insetSurface(RoundedCornerShape(Radii.sm), depth = 3.dp)
            .then(if (onClick != null) Modifier.pressable(pressedScale = 0.97f, onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(label, style = ReliefTheme.type.caption, color = c.ink2, maxLines = 1)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = com.example.relief.theme.NumStyles.xs, color = c.ink, maxLines = 1)
            if (unit != null) Text(" $unit", style = ReliefTheme.type.caption, color = c.ink2, modifier = Modifier.padding(bottom = 2.dp))
        }
        if (sub != null) Text(sub, style = ReliefTheme.type.caption, color = c.ink2, maxLines = 2)
    }
}

@Composable
fun VSpace(h: Dp) = Spacer(Modifier.height(h))

@Composable
fun HSpace(w: Dp) = Spacer(Modifier.size(w, 1.dp))
