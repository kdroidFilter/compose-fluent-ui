package io.github.composefluent.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.composefluent.FluentTheme
import io.github.composefluent.animation.FluentDuration

/**
 * Defines the pre-defined sizes for the [ProgressRing].
 */
object ProgressRingSize {
    val Large = 64.dp
    val Medium = 32.dp
    val Small = 16.dp
}

/**
 * A composable function that creates a progress ring with a specified progress value.
 *
 * @param progress The progress value of the ring, ranging from 0.0 to 1.0.
 * @param modifier The modifier to be applied to the progress ring.
 * @param size The size of the progress ring. Defaults to [ProgressRingSize.Medium].
 * @param width The width of the progress ring's stroke. Defaults to 3/32 of the size.
 * @param color The color of the progress ring. Defaults to the accent color in the Fluent theme.
 */
@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = ProgressRingSize.Medium,
    width: Dp = size * 3 / 32,
    color: Color = FluentTheme.colors.fillAccent.default
) {
    ProgressRing(
        modifier = modifier,
        startProvider = { 0f },
        lengthProvider = { progress * 360f },
        width = width,
        color = color,
        size = size
    )
}

/**
 * An indeterminate [ProgressRing] that animates a ring to show progress.
 *
 * Animation values are read only during draw, so the infinite loop does not recompose every frame.
 *
 * @param modifier The modifier to be applied to the ProgressRing.
 * @param size The size of the ProgressRing. Default is [ProgressRingSize.Medium].
 * @param width The width of the ring stroke. Default is calculated based on the size.
 * @param color The color of the ring. Default is [FluentTheme.colors.fillAccent.default].
 */
@Composable
fun ProgressRing(
    modifier: Modifier = Modifier,
    size: Dp = ProgressRingSize.Medium,
    width: Dp = size * 3 / 32,
    color: Color = FluentTheme.colors.fillAccent.default
) {
    val infinite = rememberInfiniteTransition(label = "progress-ring")
    val length = infinite.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                easing = LinearEasing,
                durationMillis = (FluentDuration.VeryLongDuration * 1.5f).toInt()
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "length"
    )
    val progress = infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                easing = LinearEasing,
                durationMillis = FluentDuration.VeryLongDuration
            )
        ),
        label = "progress"
    )

    ProgressRing(
        modifier = modifier,
        startProvider = { progress.value - length.value },
        lengthProvider = { length.value },
        width = width,
        size = size,
        color = color
    )
}

@Composable
private fun ProgressRing(
    modifier: Modifier,
    startProvider: () -> Float,
    lengthProvider: () -> Float,
    width: Dp,
    size: Dp,
    color: Color
) {
    val density = LocalDensity.current
    val widthPx = remember(density, width) { with(density) { width.toPx() } }
    Box(modifier.size(size)) {
        Canvas(Modifier.fillMaxSize()) {
            drawArc(
                color = color,
                startAngle = startProvider() - 90f,
                sweepAngle = lengthProvider(),
                useCenter = false,
                size = this.size,
                style = Stroke(widthPx, cap = StrokeCap.Round)
            )
        }
    }
}
