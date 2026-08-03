package io.github.composefluent.component

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.composefluent.FluentTheme
import io.github.composefluent.animation.FluentDuration

/**
 * A determinate progress bar that displays progress from `0f` to `1f`.
 *
 * In [LayoutDirection.Rtl], the fill grows from right to left.
 *
 * @param progress The current progress, a value between `0f` (0%) and `1f` (100%).
 * @param modifier Modifier for styling and layout.
 * @param color The color of the progress track. Defaults to the accent color of the current theme.
 */
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = FluentTheme.colors.fillAccent.default
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Box(
        modifier = modifier.defaultMinSize(minWidth = 130.dp, minHeight = 3.dp),
        propagateMinConstraints = true,
        contentAlignment = Alignment.CenterStart
    ) {
        Rail()
        Box(Modifier.matchParentSize()) {
            Track(
                progress = progress.coerceIn(0f, 1f),
                color = color,
                rtlMirror = isRtl
            )
        }
    }
}

@Composable
private fun Rail() {
    Box(
        Modifier
            .requiredHeight(1.dp)
            .background(FluentTheme.colors.controlStrong.default, CircleShape)
    )
}

private val TrackWidth = 3.dp

@Composable
private fun Track(
    progress: Float,
    color: Color,
    rtlMirror: Boolean
) {
    val canvasModifier = if (rtlMirror) {
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                // Mirror horizontally so the same LTR math fills right-to-left in RTL.
                scaleX = -1f
                transformOrigin = TransformOrigin.Center
            }
    } else {
        Modifier.fillMaxSize()
    }

    Canvas(canvasModifier) {
        if (progress > 0f) {
            val half = (TrackWidth / 2).toPx()
            drawLine(
                color = color,
                start = Offset(half, half),
                end = Offset(progress * (size.width - half), half),
                strokeWidth = TrackWidth.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

private val LongWidth = 100.dp
private val ShortWidth = 50.dp
private val Easing = CubicBezierEasing(0.5f, 0f, 0.5f, 1.0f)

/**
 * An indeterminate progress bar that displays a continuous, looping animation.
 * This component indicates ongoing activity without specifying a completion percentage.
 *
 * The animation consists of two moving segments: a long bar and a short bar, which loop across the width of the component.
 * In [LayoutDirection.Rtl], the segments travel right-to-left.
 *
 * @param modifier The modifier to apply to this layout.
 * @param color The color of the moving segments. Defaults to `FluentTheme.colors.fillAccent.default`.
 */
@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    color: Color = FluentTheme.colors.fillAccent.default
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Box(
        modifier.defaultMinSize(minWidth = 130.dp, minHeight = 3.dp),
        contentAlignment = Alignment.CenterStart,
        propagateMinConstraints = true
    ) {
        // TODO: In Fluent Design Specification, the undetermined ProgressBar has a rail. But the rail does not present in WinUI3 Gallery
        // Rail()
        Box(Modifier.matchParentSize()) {
            val infinite = rememberInfiniteTransition(label = "progress-bar")
            // Keep State; read .value only in draw to avoid per-frame recomposition.
            val progress = infinite.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = InfiniteRepeatableSpec(
                    animation = tween(
                        durationMillis = FluentDuration.VeryLongDuration * 3,
                        easing = Easing
                    )
                ),
                label = "progress"
            )

            val canvasModifier = if (isRtl) {
                Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .graphicsLayer {
                        // Mirror horizontally to reverse travel direction in RTL.
                        scaleX = -1f
                        transformOrigin = TransformOrigin.Center
                    }
            } else {
                Modifier.fillMaxSize().clip(CircleShape)
            }

            /*
                |               totalWidth                 |
                |          preWidth         |  size.width  |
                |  long  | size.width |short|  size.width  |
                 --------[            ]-----[ display area ]
                |          preWidth         |  size.width  |  long  | size.width |short|
                                            [ display area ]--------[            ]-----
             */
            Canvas(canvasModifier) {
                val p = progress.value
                val trackWidth = TrackWidth.toPx()
                val half = trackWidth / 2

                val shortWidthPx = ShortWidth.toPx()
                val longWidthPx = LongWidth.toPx()

                val preWidth = shortWidthPx + size.width + longWidthPx
                val totalWidth = size.width + preWidth

                val shortOffset = (p * totalWidth + longWidthPx + size.width) - preWidth
                val shortStart = half + shortOffset
                val shortEnd = shortStart + shortWidthPx - half

                val longOffset = (p * totalWidth) - preWidth
                val longStart = half + longOffset
                val longEnd = longStart + longWidthPx - half

                // Short
                drawLine(
                    color = color,
                    start = Offset(shortStart, half),
                    end = Offset(shortEnd, half),
                    strokeWidth = trackWidth,
                    cap = StrokeCap.Round
                )

                // Long
                drawLine(
                    color = color,
                    start = Offset(longStart, half),
                    end = Offset(longEnd, half),
                    strokeWidth = trackWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
