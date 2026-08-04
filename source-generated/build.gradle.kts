import io.github.composefluent.plugin.build.applyTargets
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    applyTargets(namespaceModule = ".generated")
    sourceSets {
        commonMain.dependencies {
            implementation(project(":fluent-icons-core"))
            implementation(project(":fluent-icons-extended"))
            implementation(compose.ui)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

// This module has no sources of its own in git: `src/` is ignored, and every file in it is
// written by the KSP processors of the three modules below, which reach out of their own
// build directory to generate straight into this source tree (SourceFilePathProcessor.finish()
// and IconSourceProcessor). Nothing declared that relationship, so on a fresh checkout Gradle
// was free to compile this module before those processors had run — leaving `FluentSourceFile`
// and the icon item lists unresolved in :gallery.
val sourceGenerators = listOf(
    ":fluent",
    ":fluent-icons-core",
    ":fluent-icons-extended",
).map { "$it:kspCommonMainKotlinMetadata" }

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    dependsOn(sourceGenerators)
}
