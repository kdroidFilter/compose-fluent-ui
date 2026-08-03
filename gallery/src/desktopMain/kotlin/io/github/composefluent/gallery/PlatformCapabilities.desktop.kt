package io.github.composefluent.gallery

import androidx.compose.runtime.Composable
import dev.nucleusframework.darkmodedetector.isSystemInDarkMode
import org.jetbrains.skiko.hostOs

internal actual val supportsWindowBackdrop: Boolean = hostOs.isWindows

@Composable
internal actual fun platformSystemInDarkMode(): Boolean = isSystemInDarkMode()
