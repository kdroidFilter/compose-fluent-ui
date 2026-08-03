package io.github.composefluent.gallery.window

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import dev.nucleusframework.window.DecoratedWindowScope
import dev.nucleusframework.window.LocalWindowChromeInsets
import dev.nucleusframework.window.TitleBarPlacement
import dev.nucleusframework.window.WindowAppearance
import dev.nucleusframework.window.WindowAppearanceMode
import dev.nucleusframework.window.WindowBackground
import dev.nucleusframework.window.WindowControls
import dev.nucleusframework.window.WindowScaffold
import dev.nucleusframework.window.WindowsBackdrop
import dev.nucleusframework.window.WindowsBackdropStyle
import dev.nucleusframework.window.noWindowDrag
import dev.nucleusframework.window.windowDragArea
import io.github.composefluent.ExperimentalFluentApi
import io.github.composefluent.FluentTheme
import io.github.composefluent.animation.FluentDuration
import io.github.composefluent.animation.FluentEasing
import io.github.composefluent.component.FontIconDefaults
import io.github.composefluent.component.FontIconSize
import io.github.composefluent.component.Icon
import io.github.composefluent.component.NavigationDefaults
import io.github.composefluent.component.NavigationDisplayMode
import io.github.composefluent.component.SubtleButton
import io.github.composefluent.component.Text
import io.github.composefluent.component.TooltipBox
import io.github.composefluent.gallery.GalleryTheme
import io.github.composefluent.gallery.LocalStore
import io.github.composefluent.gallery.Store
import io.github.composefluent.gallery.ThemeMode
import io.github.composefluent.gallery.WindowBackdropOption
import io.github.composefluent.icons.Icons
import io.github.composefluent.icons.regular.Desktop
import io.github.composefluent.icons.regular.TextDirectionHorizontalLtr
import io.github.composefluent.icons.regular.TextDirectionHorizontalRtl
import io.github.composefluent.icons.regular.WeatherMoon
import io.github.composefluent.icons.regular.WeatherSunny
import org.jetbrains.skiko.hostOs

/**
 * Window chrome adapter over the Nucleus decorated window (Tao backend).
 *
 * Keeps the historical call contract of the old AWT/JNA `WindowFrame`, but the
 * native hit-testing, caption buttons, Snap Layouts flyout and the Windows
 * Mica/Acrylic backdrop are all provided by Nucleus.
 */
@Composable
fun DecoratedWindowScope.WindowFrame(
    onCloseRequest: () -> Unit,
    icon: Painter? = null,
    title: String = "",
    state: WindowState,
    backButtonVisible: Boolean = true,
    backButtonEnabled: Boolean = false,
    backButtonClick: () -> Unit = {},
    captionBarHeight: Dp = 48.dp,
    // The application-level Store, re-provided inside this window's own
    // composition (Tao windows have separate ComposeScenes).
    store: Store? = null,
    content: @Composable (windowInset: WindowInsets, captionBarInset: WindowInsets) -> Unit
) {
    // On Windows the native backdrop replaces the faux mica layer (Nucleus
    // degrades pre-22H2/Win10 to a real acrylic material, so the Compose-drawn
    // mica stays off on all Windows versions).
    GalleryTheme(displayMicaLayer = !hostOs.isWindows, store = store) {
        val store = LocalStore.current
        val backdropStyle = when (store.windowBackdrop) {
            WindowBackdropOption.Default -> WindowsBackdropStyle.Default
            WindowBackdropOption.None -> WindowsBackdropStyle.None
            WindowBackdropOption.Mica -> WindowsBackdropStyle.Mica
            WindowBackdropOption.Acrylic -> WindowsBackdropStyle.Acrylic
            WindowBackdropOption.MicaAlt -> WindowsBackdropStyle.MicaAlt
        }
        // Always the resolved Fluent background for the current theme: Nucleus
        // derives the DWM immersive-dark flag (which themes Mica/Acrylic light
        // or dark) and the default Acrylic tint from this colour's luminance.
        // The window-level transparency needed for the backdrop to show is
        // managed by WindowsBackdrop itself — never pass Transparent here.
        WindowBackground(FluentTheme.colors.background.mica.base)
        WindowAppearance(
            if (FluentTheme.colors.darkMode) WindowAppearanceMode.Dark else WindowAppearanceMode.Light
        )
        if (hostOs.isWindows) {
            WindowsBackdrop(backdropStyle)
        }
        val isCollapsed =
            store.navigationDisplayMode == NavigationDisplayMode.LeftCollapsed
        WindowScaffold(
            titleBarPlacement = TitleBarPlacement.Overlay(autoHideInFullscreen = false),
            titleBar = {
                CaptionBar(
                    icon = if (isCollapsed) null else icon,
                    title = if (isCollapsed) "" else title,
                    backButtonVisible = backButtonVisible && !isCollapsed,
                    backButtonEnabled = backButtonEnabled,
                    backButtonClick = backButtonClick,
                    captionBarHeight = captionBarHeight
                )
            }
        ) { paddingValues ->
            content(WindowInsets(top = paddingValues.calculateTopPadding()), WindowInsets(0))
        }
    }
}

@Composable
private fun DecoratedWindowScope.CaptionBar(
    icon: Painter?,
    title: String,
    backButtonVisible: Boolean,
    backButtonEnabled: Boolean,
    backButtonClick: () -> Unit,
    captionBarHeight: Dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(captionBarHeight)
            .windowDragArea()
            // Keeps the bar content clear of the macOS traffic lights;
            // zero insets on Windows/Linux where WindowControls draws inline.
            .padding(LocalWindowChromeInsets.current.controlsInsets)
    ) {
        AnimatedContent(
            targetState = backButtonVisible,
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = expandHorizontally(),
                    initialContentExit = shrinkHorizontally(),
                    sizeTransform = SizeTransform { _, _ ->
                        tween(
                            FluentDuration.ShortDuration,
                            easing = FluentEasing.FastInvokeEasing
                        )
                    }
                )
            }
        ) {
            if (it) {
                val interactionSource = remember { MutableInteractionSource() }
                NavigationDefaults.BackButton(
                    onClick = backButtonClick,
                    disabled = !backButtonEnabled,
                    interaction = interactionSource,
                    icon = { FontIconDefaults.BackIcon(interactionSource, size = FontIconSize(10f)) },
                    // Opt out of the caption drag area so the press reaches the button.
                    modifier = Modifier.noWindowDrag()
                )
            } else {
                Spacer(modifier = Modifier.width(14.dp).height(36.dp))
            }
        }
        if (icon != null) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.padding(start = 6.dp).size(16.dp)
            )
        }
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = FluentTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        RtlModeButton(modifier = Modifier.noWindowDrag())
        ThemeModeButton(modifier = Modifier.noWindowDrag())
        // macOS shows the native traffic lights (left side, already reserved
        // via controlsInsets above) — composing WindowControls there as well
        // would reserve the footprint a second time.
        if (!hostOs.isMacOS) {
            WindowControls(modifier = Modifier.noWindowDrag())
        }
    }
}

/** Toggles gallery layout direction between LTR and RTL. */
@OptIn(ExperimentalFoundationApi::class, ExperimentalFluentApi::class)
@Composable
private fun RtlModeButton(modifier: Modifier = Modifier) {
    val store = LocalStore.current
    val label = if (store.rtl) "Layout: RTL" else "Layout: LTR"
    val icon = if (store.rtl) {
        Icons.Regular.TextDirectionHorizontalRtl
    } else {
        Icons.Regular.TextDirectionHorizontalLtr
    }
    TooltipBox(tooltip = { Text(label) }) {
        SubtleButton(
            onClick = { store.rtl = !store.rtl },
            iconOnly = true,
            modifier = modifier,
            content = { Icon(imageVector = icon, contentDescription = label) }
        )
    }
}

/** Cycles System -> Light -> Dark -> System. */
@OptIn(ExperimentalFoundationApi::class, ExperimentalFluentApi::class)
@Composable
private fun ThemeModeButton(modifier: Modifier = Modifier) {
    val store = LocalStore.current
    val (icon, label) = when (store.themeMode) {
        ThemeMode.System -> Icons.Regular.Desktop to "Theme: System"
        ThemeMode.Light -> Icons.Regular.WeatherSunny to "Theme: Light"
        ThemeMode.Dark -> Icons.Regular.WeatherMoon to "Theme: Dark"
    }
    TooltipBox(tooltip = { Text(label) }) {
        SubtleButton(
            onClick = {
                val entries = ThemeMode.entries
                store.themeMode = entries[(store.themeMode.ordinal + 1) % entries.size]
            },
            iconOnly = true,
            modifier = modifier,
            content = { Icon(imageVector = icon, contentDescription = label) }
        )
    }
}
