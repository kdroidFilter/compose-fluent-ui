package io.github.composefluent.plugin.build

import org.gradle.api.JavaVersion

object BuildConfig {

    const val group = "dev.nucleusframework.composefluent"

    const val packageName = "io.github.composefluent"

    const val repositoryUrl = "https://github.com/kdroidFilter/compose-fluent-ui"

    internal const val snapshotLibraryVersion = "0.1.0-SNAPSHOT"

    val isRelease = System.getenv("PROJECT_BUILD_TYPE") == "release"

    /**
     * Release version exported by the CI from the pushed tag (`RELEASE_VERSION=v1.2.3`).
     * Blank or non-numeric values (e.g. a branch name) are ignored so that manual
     * `workflow_dispatch` runs on a branch fall back to the snapshot version.
     */
    val releaseVersion: String? =
        System
            .getenv("RELEASE_VERSION")
            ?.removePrefix("v")
            ?.takeIf { it.isNotBlank() && it.first().isDigit() }

    var libraryVersion: String = snapshotLibraryVersion
        internal set

    var integerVersionName: String = ""
        internal set

    var branch: String = "dev"
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