plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":fluent"))
    // Compiled against the Tao backend; the consumer supplies it (and the
    // application DSL) at runtime.
    compileOnly(libs.nucleus.window.tao)
    compileOnly(libs.nucleus.application)
    api(libs.nucleus.core.runtime)
    implementation(compose.desktop.common)
}
