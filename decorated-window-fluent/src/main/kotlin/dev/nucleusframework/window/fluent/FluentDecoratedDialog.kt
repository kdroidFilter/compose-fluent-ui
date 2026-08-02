package dev.nucleusframework.window.fluent

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.rememberDialogState
import dev.nucleusframework.application.DecoratedDialog
import dev.nucleusframework.application.NucleusApplicationScope
import dev.nucleusframework.application.NucleusDecoratedDialogScope
import dev.nucleusframework.window.NucleusDecoratedWindowTheme
import io.github.composefluent.ExperimentalFluentApi
import io.github.composefluent.FluentTheme

/**
 * Fluent Design wrapper around the Nucleus [DecoratedDialog]. Use inside
 * `nucleusApplication { … }` — works on the AWT (JBR/JNI) and Tao backends
 * with the same call site.
 */
@OptIn(ExperimentalFluentApi::class)
@Suppress("FunctionNaming", "LongParameterList")
@Composable
fun NucleusApplicationScope.FluentDecoratedDialog(
    onCloseRequest: () -> Unit,
    state: DialogState = rememberDialogState(),
    visible: Boolean = true,
    title: String = "",
    icon: Painter? = null,
    resizable: Boolean = false,
    enabled: Boolean = true,
    focusable: Boolean = true,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable NucleusDecoratedDialogScope.() -> Unit,
) {
    val outerColors = FluentTheme.colors
    val outerTypography = FluentTheme.typography
    val outerCornerRadius = FluentTheme.cornerRadius
    val windowStyle = rememberFluentWindowStyle(outerColors)
    val titleBarStyle = rememberFluentTitleBarStyle(outerColors)

    NucleusDecoratedWindowTheme(
        isDark = outerColors.darkMode,
        windowStyle = windowStyle,
        titleBarStyle = titleBarStyle,
    ) {
        DecoratedDialog(
            onCloseRequest = onCloseRequest,
            state = state,
            visible = visible,
            title = title,
            icon = icon,
            resizable = resizable,
            enabled = enabled,
            focusable = focusable,
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
