package io.github.composefluent.gallery

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

internal actual val supportsWindowBackdrop: Boolean = false

@Composable
internal actual fun platformSystemInDarkMode(): Boolean = isSystemInDarkTheme()
