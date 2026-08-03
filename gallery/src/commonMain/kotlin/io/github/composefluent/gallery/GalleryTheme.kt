package io.github.composefluent.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import io.github.composefluent.ExperimentalFluentApi
import io.github.composefluent.FluentTheme
import io.github.composefluent.LocalContentColor
import io.github.composefluent.background.Mica
import io.github.composefluent.component.NavigationDisplayMode
import io.github.composefluent.darkColors
import io.github.composefluent.lightColors

val LocalStore = compositionLocalOf<Store> { error("Not provided") }

/** Tri-state theme selection: follow the OS, or force light/dark. */
enum class ThemeMode { System, Light, Dark }

/**
 * Window backdrop material, mirroring Nucleus's `WindowsBackdropStyle`.
 * Declared platform-neutrally so the Settings screen (commonMain) can offer it;
 * only the desktop Windows window frame acts on it.
 */
enum class WindowBackdropOption { Default, None, Mica, Acrylic, MicaAlt }

class Store(
    systemDarkMode: Boolean,
    enabledAcrylicPopup: Boolean,
    compactMode: Boolean
) {
    /** Resolved dark-mode flag; kept in sync with [themeMode] by [GalleryTheme]. */
    var darkMode by mutableStateOf(systemDarkMode)

    var themeMode by mutableStateOf(ThemeMode.System)

    var windowBackdrop by mutableStateOf(WindowBackdropOption.Mica)

    var enabledAcrylicPopup by mutableStateOf(enabledAcrylicPopup)

    var compactMode by mutableStateOf(compactMode)

    var navigationDisplayMode by mutableStateOf(NavigationDisplayMode.Left)

    /** Force RTL layout for gallery RTL testing (overrides system layout direction). */
    var rtl by mutableStateOf(false)
}

@OptIn(ExperimentalFluentApi::class)
@Composable
fun GalleryTheme(
    displayMicaLayer: Boolean = true,
    // Pass an existing store to share state across windows (Tao windows own
    // separate compositions, so CompositionLocals do not cross them).
    store: Store? = null,
    content: @Composable () -> Unit
) {
    // Desktop (Tao): Nucleus darkmode-detector — Compose's isSystemInDarkTheme
    // reads Skiko/LocalSystemTheme which is unreliable without an AWT frame.
    // Other targets: Compose foundation isSystemInDarkTheme.
    val systemDarkMode = platformSystemInDarkMode()

    @Suppress("NAME_SHADOWING")
    val store = store ?: remember {
        Store(
            systemDarkMode = systemDarkMode,
            enabledAcrylicPopup = true,
            compactMode = true
        )
    }

    LaunchedEffect(systemDarkMode, store.themeMode) {
        store.darkMode = when (store.themeMode) {
            ThemeMode.System -> systemDarkMode
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
        }
    }
    val layoutDirection = if (store.rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(
        LocalStore provides store,
        LocalLayoutDirection provides layoutDirection,
    ) {
        FluentTheme(
            colors = if (store.darkMode) darkColors() else lightColors(),
            useAcrylicPopup = store.enabledAcrylicPopup,
            compactMode = store.compactMode
        ) {
            if (displayMicaLayer) {
                val gradient = if (store.darkMode) {
                    listOf(
                        Color(0xff282C51),
                        Color(0xff2A344A),
                    )
                } else {
                    listOf(
                        Color(0xffB1D0ED),
                        Color(0xffDAE3EC),
                    )
                }

                Mica(
                    background = {
                        Image(
                            painter = BrushPainter(Brush.linearGradient(gradient)),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    content()
                }
            } else {
                CompositionLocalProvider(
                    LocalContentColor provides FluentTheme.colors.text.text.primary,
                    content = content
                )
            }
        }
    }
}