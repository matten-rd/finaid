package com.strand.finaid.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*

/**
 * Material Motion Constants
 */
private const val DefaultMotionDuration = 300
private const val ProgressThreshold = 0.35f
// 35% of specified value
private val Int.ForOutgoing: Int
    get() = (this * ProgressThreshold).toInt()
// 65% of specified value
private val Int.ForIncoming: Int
    get() = this - this.ForOutgoing

/**
 * Material Fade Through
 */
fun materialFadeThroughIn(
    initialScale: Float = 0.92f,
    durationMillis: Int = DefaultMotionDuration
): EnterTransition = fadeIn(
    animationSpec = tween(
        durationMillis = durationMillis.ForIncoming,
        delayMillis = durationMillis.ForOutgoing,
        easing = LinearOutSlowInEasing
    )
) + scaleIn(
    animationSpec = tween(
        durationMillis = durationMillis.ForIncoming,
        delayMillis = durationMillis.ForOutgoing,
        easing = LinearOutSlowInEasing
    ),
    initialScale = initialScale
)

fun materialFadeThroughOut(
    durationMillis: Int = DefaultMotionDuration,
): ExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = durationMillis.ForOutgoing,
        delayMillis = 0,
        easing = FastOutLinearInEasing
    )
)

/**
 * Material Shared Axis Z
 */
fun materialSharedAxisZIn(
    forward: Boolean,
    durationMillis: Int = DefaultMotionDuration,
): EnterTransition = fadeIn(
    animationSpec = tween(
        durationMillis = durationMillis.ForIncoming,
        delayMillis = durationMillis.ForOutgoing,
        easing = LinearOutSlowInEasing
    )
) + scaleIn(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = FastOutSlowInEasing
    ),
    initialScale = if (forward) 0.8f else 1.1f
)

fun materialSharedAxisZOut(
    forward: Boolean,
    durationMillis: Int = DefaultMotionDuration,
): ExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = durationMillis.ForOutgoing,
        delayMillis = 0,
        easing = FastOutLinearInEasing
    )
) + scaleOut(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = FastOutSlowInEasing
    ),
    targetScale = if (forward) 1.1f else 0.8f
)

/**
 * Material Elevation Scale
 */
fun materialElevationScaleIn(
    initialAlpha: Float = 0.85f,
    initialScale: Float = 0.85f,
    durationMillis: Int = DefaultMotionDuration,
): EnterTransition = fadeIn(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = LinearEasing
    ),
    initialAlpha = initialAlpha
) + scaleIn(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = FastOutSlowInEasing
    ),
    initialScale = initialScale
)

fun materialElevationScaleOut(
    targetAlpha: Float = 0.85f,
    targetScale: Float = 0.85f,
    durationMillis: Int = DefaultMotionDuration,
): ExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = LinearEasing
    ),
    targetAlpha = targetAlpha
) + scaleOut(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = FastOutSlowInEasing
    ),
    targetScale = targetScale
)