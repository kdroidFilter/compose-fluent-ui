package dev.nucleusframework.window.fluent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.nucleusframework.core.runtime.LinuxDesktopEnvironment
import dev.nucleusframework.core.runtime.Platform
import dev.nucleusframework.window.styling.DecoratedWindowColors
import dev.nucleusframework.window.styling.DecoratedWindowMetrics
import dev.nucleusframework.window.styling.DecoratedWindowStyle
import dev.nucleusframework.window.styling.TitleBarColors
import dev.nucleusframework.window.styling.TitleBarMetrics
import dev.nucleusframework.window.styling.TitleBarStyle
import io.github.composefluent.Colors

private const val INACTIVE_BORDER_ALPHA = 0.5f

private val isKde =
    Platform.Current == Platform.Linux && LinuxDesktopEnvironment.Current == LinuxDesktopEnvironment.KDE

/**
 * Maps the Fluent color scheme onto the Nucleus window style: the window
 * background is Fluent's bottom-most solid layer, the frame border its
 * surface stroke.
 */
@Composable
fun rememberFluentWindowStyle(colors: Colors): DecoratedWindowStyle =
    remember(colors.background, colors.stroke) {
        DecoratedWindowStyle(
            colors =
                DecoratedWindowColors(
                    background = colors.background.solid.base,
                    border = colors.stroke.surface.default,
                    borderInactive = colors.stroke.surface.default.copy(alpha = INACTIVE_BORDER_ALPHA),
                ),
            metrics = DecoratedWindowMetrics(borderWidth = 1.dp),
        )
    }

/**
 * Maps the Fluent color scheme onto the Nucleus title-bar style. The bar
 * shares the window's bottom layer (Fluent title bars are not separate
 * surfaces), with the primary text color as content.
 */
@Composable
fun rememberFluentTitleBarStyle(colors: Colors): TitleBarStyle =
    remember(colors.background, colors.text, colors.stroke) {
        TitleBarStyle(
            colors =
                TitleBarColors(
                    background = colors.background.solid.base,
                    inactiveBackground = colors.background.solid.base,
                    content = colors.text.text.primary,
                    border = colors.stroke.surface.default,
                    fullscreenControlButtonsBackground = colors.background.solid.base,
                ),
            metrics =
                TitleBarMetrics(
                    height = 48.dp,
                    titlePaneButtonSize = if (isKde) DpSize(28.dp, 28.dp) else DpSize(40.dp, 40.dp),
                ),
        )
    }
