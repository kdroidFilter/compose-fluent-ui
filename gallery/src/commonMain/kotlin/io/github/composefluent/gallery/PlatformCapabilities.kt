package io.github.composefluent.gallery

/**
 * Whether the current platform supports switching the native window backdrop
 * material (Mica/Acrylic). Only true on desktop Windows.
 */
internal expect val supportsWindowBackdrop: Boolean
