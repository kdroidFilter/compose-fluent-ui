package io.github.composefluent.gallery

import androidx.compose.runtime.Composable

/**
 * Whether the current platform supports switching the native window backdrop
 * material (Mica/Acrylic). Only true on desktop Windows.
 */
internal expect val supportsWindowBackdrop: Boolean

/**
 * Live OS dark-mode flag for [ThemeMode.System].
 *
 * Desktop uses Nucleus [dev.nucleusframework.darkmodedetector.isSystemInDarkMode]
 * (JNI registry/portal observers). Other targets use Compose foundation.
 */
@Composable
internal expect fun platformSystemInDarkMode(): Boolean
