package io.github.composefluent.plugin.build

import org.gradle.api.JavaVersion

object BuildConfig {

    const val group = "dev.nucleusframework.composefluent"

    const val packageName = "io.github.composefluent"

    const val repositoryUrl = "https://github.com/NucleusFramework/compose-fluent-ui"

    /** Version used when the build is not driven by a release tag. */
    internal const val defaultLibraryVersion = "1.0.0"

    val isRelease = System.getenv("PROJECT_BUILD_TYPE") == "release"

    /**
     * Release version exported by the CI from the pushed tag (`RELEASE_VERSION=v1.2.3`).
     * Blank or non-numeric values (e.g. a branch name) are ignored so that a manual
     * `workflow_dispatch` run on a branch falls back to [defaultLibraryVersion].
     */
    val releaseVersion: String? =
        System
            .getenv("RELEASE_VERSION")
            ?.removePrefix("v")
            ?.takeIf { it.isNotBlank() && it.first().isDigit() }

    var libraryVersion: String = defaultLibraryVersion
        internal set

    var integerVersionName: String = ""
        internal set

    /**
     * Monotonically increasing Android version code derived from [integerVersionName]
     * as `major * 10000 + minor * 100 + patch`, so 1.0.0 is 10000 and every later
     * release sorts above it.
     */
    var androidVersionCode: Int = 0
        internal set

    /**
     * Git ref the gallery deep-links its source code to. A release build pins it to the
     * published tag so the links keep working forever; otherwise it is the current branch.
     */
    var branch: String = "master"
        internal set

    /** True when building from a release tag (or with `PROJECT_BUILD_TYPE=release`). */
    var isReleaseBuild: Boolean = false
        internal set

    object Android {
        const val compileSdkVersion = 35

        const val minSdkVersion = 24
    }

    object Jvm {
        const val jvmToolchainVersion = 17
        val javaVersion = JavaVersion.VERSION_17
    }
}