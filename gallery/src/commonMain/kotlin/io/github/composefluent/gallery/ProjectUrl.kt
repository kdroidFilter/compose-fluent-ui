package io.github.composefluent.gallery

import io.github.composefluent.build.BuildKonfig

object ProjectUrl {

    const val ROOT = BuildKonfig.PROJECT_URL

    const val FRAMEWORK = "https://developer.android.com/develop/ui/compose"

    const val UI_DESIGN = "https://fluent2.microsoft.design/"

    const val FEED_BACK = "$ROOT/issues/new/choose"

    /**
     * Git ref the source links resolve against: the published tag on a release build,
     * so the links keep working forever; the checked-out branch otherwise.
     */
    private const val REF = BuildKonfig.SOURCE_REF

    fun componentCodeOf(path: String): String {
        return "$ROOT/tree/$REF/$path"
    }

    fun galleryCodeOf(path: String): String {
        return "$ROOT/tree/$REF/gallery/src/$path"
    }

    //TODO documentation redirection
    fun documentationOf(path: String): String {
        return ROOT
    }

}
