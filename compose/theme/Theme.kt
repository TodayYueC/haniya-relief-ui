package com.example.relief.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.LocalTextStyle

val LocalReliefColors = staticCompositionLocalOf { ReliefPalettes.Platinum }
val LocalReliefType = staticCompositionLocalOf { ReliefType() }




object ReliefTheme {
    val colors: ReliefColors @Composable get() = LocalReliefColors.current
    val type: ReliefType @Composable get() = LocalReliefType.current
}

/** Static materials: switching theme replaces a complete, balanced palette together. */
@Composable
fun ReliefTheme(
    theme: ReliefPalette = ReliefPalette.PLATINUM,
    reducedMotion: Boolean = rememberSystemReducedMotion(),
    content: @Composable () -> Unit,
) {
    val t = ReliefPalettes.of(theme)
    val colors = t
    val scheme = (if (t.isDark) darkColorScheme() else lightColorScheme()).copy(
        primary = colors.accent, onPrimary = colors.on,
        primaryContainer = colors.soft, onPrimaryContainer = colors.accent,
        secondary = colors.acc2, onSecondary = colors.on,
        secondaryContainer = colors.soft2, onSecondaryContainer = colors.acc2,
        background = colors.bg, onBackground = colors.ink,
        surface = colors.glass2, onSurface = colors.ink,
        surfaceVariant = colors.track, onSurfaceVariant = colors.ink2,
        surfaceTint = Color.Transparent,
        surfaceContainer = colors.glass, surfaceContainerLow = colors.bg,
        surfaceContainerLowest = colors.bg, surfaceContainerHigh = colors.glass2,
        surfaceContainerHighest = colors.track,
        outline = colors.line, outlineVariant = colors.line,
        error = colors.danger, onError = Color.White, scrim = colors.scrim,
    )
    val type = ReliefType()
    MaterialTheme(colorScheme = scheme) {
        CompositionLocalProvider(
            LocalReliefColors provides colors,
            LocalReliefType provides type,
            LocalReducedMotion provides reducedMotion,
            LocalContentColor provides colors.ink,
            LocalTextStyle provides type.body.merge(TextStyle(color = colors.ink)),
            LocalTextSelectionColors provides TextSelectionColors(colors.accent, colors.soft),
            content = content,
        )
    }
}
