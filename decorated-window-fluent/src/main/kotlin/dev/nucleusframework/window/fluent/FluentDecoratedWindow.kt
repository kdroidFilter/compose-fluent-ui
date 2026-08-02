package dev.nucleusframework.window.fluent

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusApplicationScope
import dev.nucleusframework.application.NucleusDecoratedWindowScope
import dev.nucleusframework.window.NucleusDecoratedWindowTheme
import dev.nucleusframework.window.styling.TitleBarStyle
import io.github.composefluent.ExperimentalFluentApi
import io.github.composefluent.FluentTheme

/**
 * Fluent Design wrapper around the Nucleus [DecoratedWindow]. Picks the
 * window and title-bar styles from the ambient [FluentTheme] colors and wraps
 * the window with [NucleusDecoratedWindowTheme], the exact counterpart of the
 * Jewel / Material integrations.
 *
 * Use inside `nucleusApplication { … }` — works on the AWT (JBR/JNI) and Tao
 * backends with the same call site.
 *
 * Theme tokens captured from the outer composition are re-provided inside the
 * window content, which matters on Tao (each window owns its own ComposeScene
 * and CompositionLocals do not propagate across scenes).
 */
@OptIn(ExperimentalFluentApi::class)
@Suppress("FunctionNaming", "LongParameterList")
@Composable
fun NucleusApplicationScope.FluentDecoratedWindow(
    onCloseRequest: () -> Unit,
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    title: String = "",
    icon: Painter? = null,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    // Materialise Compose Popup layers as native transparent windows
    // (NSPanel / WS_POPUP HWND / Tao popup window on Linux) so menus can
    // extend past the window bounds. Honoured by the Tao backend; ignored by AWT.
    nativePopupLayers: Boolean = false,
    // Hide this window from the OS taskbar/Dock while it stays visible and
    // focusable (Tao backend; on Linux effective on X11/XWayland only).
    // No-op on AWT.
    hiddenFromDock: Boolean = false,
    minimumSize: DpSize? = null,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    titleBarStyle: TitleBarStyle? = null,
    content: @Composable NucleusDecoratedWindowScope.() -> Unit,
) {
    val outerColors = FluentTheme.colors
    val outerTypography = FluentTheme.typography
    val outerCornerRadius = FluentTheme.cornerRadius
    val windowStyle = rememberFluentWindowStyle(outerColors)
    val resolvedTitleBarStyle = titleBarStyle ?: rememberFluentTitleBarStyle(outerColors)

    NucleusDecoratedWindowTheme(
        isDark = outerColors.darkMode,
        windowStyle = windowStyle,
        titleBarStyle = resolvedTitleBarStyle,
    ) {
        DecoratedWindow(
            onCloseRequest = onCloseRequest,
            state = state,
            visible = visible,
            title = title,
            icon = icon,
            resizable = resizable,
            enabled = enabled,
            focusable = focusable,
            alwaysOnTop = alwaysOnTop,
            nativePopupLayers = nativePopupLayers,
            hiddenFromDock = hiddenFromDock,
            minimumSize = minimumSize,
            onPreviewKeyEvent = onPreviewKeyEvent,
            onKeyEvent = onKeyEvent,
        ) {
            FluentTheme(
                colors = outerColors,
                typography = outerTypography,
                cornerRadius = outerCornerRadius,
            ) {
                content()
            }
        }
    }
}
