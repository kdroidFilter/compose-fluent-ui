import io.github.composefluent.plugin.build.BuildConfig

plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.maven.publish)
}

group = BuildConfig.group
version = BuildConfig.libraryVersion

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":fluent"))
    // `api` rather than `compileOnly`: consumers of the published artifact build
    // their window against the Tao backend and the Nucleus application DSL, so
    // both belong on their compile classpath instead of being supplied by hand.
    api(libs.nucleus.window.tao)
    api(libs.nucleus.application)
    api(libs.nucleus.core.runtime)
    implementation(compose.desktop.common)
}
