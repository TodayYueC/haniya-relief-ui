package com.example.relief.theme

import android.provider.Settings
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember

/** 动效规范（附录 A.3）。 */
object ReliefMotion {
    val StandardEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

    /** 弹性：位移、缩放。 */
    fun <T> spring(): FiniteAnimationSpec<T> = tween(220, easing = StandardEasing)

    /** 柔和：弹层、卡片展开、进度环。 */
    fun <T> soft(): FiniteAnimationSpec<T> = tween(280, easing = StandardEasing)

    /** 标准缓出：颜色、透明度、主题切换。 */
    fun <T> standard(durationMs: Int = 700): FiniteAnimationSpec<T> = tween(durationMs, easing = StandardEasing)

    fun <T> press(): FiniteAnimationSpec<T> = tween(140, easing = StandardEasing)
}

/** 系统开启「移除动画」时为 true。 */
val LocalReducedMotion = staticCompositionLocalOf { false }

@Composable
fun rememberSystemReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        }.getOrDefault(false)
    }
}

/** 根据「减少动态效果」返回动画或瞬时完成。 */
object Motion {
    @Composable @ReadOnlyComposable
    fun <T> spring(): FiniteAnimationSpec<T> = if (LocalReducedMotion.current) snap() else ReliefMotion.spring()

    @Composable @ReadOnlyComposable
    fun <T> soft(): FiniteAnimationSpec<T> = if (LocalReducedMotion.current) snap() else ReliefMotion.soft()

    @Composable @ReadOnlyComposable
    fun <T> standard(durationMs: Int = 700): FiniteAnimationSpec<T> =
        if (LocalReducedMotion.current) snap() else ReliefMotion.standard(durationMs)

    @Composable @ReadOnlyComposable
    fun <T> tween(durationMs: Int): AnimationSpec<T> =
        if (LocalReducedMotion.current) snap() else tween(durationMs, easing = ReliefMotion.StandardEasing)
}
