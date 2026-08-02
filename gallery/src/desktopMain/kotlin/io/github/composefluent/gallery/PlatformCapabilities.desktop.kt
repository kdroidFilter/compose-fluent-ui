package io.github.composefluent.gallery

import org.jetbrains.skiko.hostOs

internal actual val supportsWindowBackdrop: Boolean = hostOs.isWindows
