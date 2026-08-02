package io.github.composefluent.gallery

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import dev.nucleusframework.application.NucleusApplicationScope
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.darkmodedetector.getPlatformDarkModeDetector
import dev.nucleusframework.graalvm.GraalVmInitializer
import dev.nucleusframework.window.fluent.FluentDecoratedWindow
import fluentdesign.gallery.generated.resources.Res
import fluentdesign.gallery.generated.resources.icon
import io.github.composefluent.ExperimentalFluentApi
import io.github.composefluent.FluentThemeConfiguration
import io.github.composefluent.component.ContentDialogHostState
import io.github.composefluent.darkColors
import io.github.composefluent.gallery.component.rememberComponentNavigator
import io.github.composefluent.gallery.window.WindowFrame
import io.github.composefluent.lightColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skiko.hostOs

/**
 * Exposes the application scope so nested screens (e.g. the TabView demo) can
 * declare secondary windows.
 */
val LocalNucleusApplicationScope = staticCompositionLocalOf<NucleusApplicationScope?> { null }

@OptIn(ExperimentalFluentApi::class)
fun main() {
    // Must be the very first call for GraalVM native-image builds; a no-op on
    // a regular JVM.
    GraalVmInitializer.initialize()
    nucleusApplication {
        val state = rememberWindowState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(1280.dp, 720.dp)
        )
        val title = "Compose Fluent Design Gallery"
        val icon = painterResource(Res.drawable.icon)
        // The gallery Store lives at application level so the Fluent window
        // chrome and every window share the same state. Seed from the Nucleus
        // OS detector so ThemeMode.System is correct on the first frame (Tao
        // has no AWT LocalSystemTheme); GalleryTheme keeps it live afterwards.
        val store = remember {
            Store(
                systemDarkMode = getPlatformDarkModeDetector().isDark(),
                enabledAcrylicPopup = true,
                compactMode = true,
            )
        }
        // FluentDecoratedWindow reads the ambient FluentTheme colors for its
        // native chrome styling, so the colors must wrap the call. The full
        // FluentTheme composable emits layout nodes, which is illegal in the
        // application composition — FluentThemeConfiguration only provides
        // the locals.
        FluentThemeConfiguration(
            colors = if (store.darkMode) darkColors() else lightColors(),
            contentDialogHostState = remember { ContentDialogHostState() }
        ) {
            FluentDecoratedWindow(
                onCloseRequest = ::exitApplication,
                state = state,
                title = title,
                icon = icon,
                minimumSize = DpSize(800.dp, 600.dp)
            ) {
                val navigator = rememberComponentNavigator()
                CompositionLocalProvider(
                    LocalNucleusApplicationScope provides this@nucleusApplication
                ) {
                    WindowFrame(
                        onCloseRequest = ::exitApplication,
                        icon = icon,
                        title = title,
                        state = state,
                        backButtonEnabled = navigator.canNavigateUp,
                        backButtonClick = { navigator.navigateUp() },
                        backButtonVisible = hostOs.isWindows,
                        store = store
                    ) { windowInset, contentInset ->
                        App(
                            windowInset = windowInset,
                            contentInset = contentInset,
                            navigator = navigator,
                            title = title,
                            icon = icon
                        )
                    }
                }
            }
        }
    }
}
