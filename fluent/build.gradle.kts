import io.github.composefluent.plugin.build.BuildConfig
import io.github.composefluent.plugin.build.applyTargets
import io.github.composefluent.plugin.build.generatedSourceFile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.stability.analyzer)
}

// Compiler reports: ./gradlew :fluent:compileKotlinDesktop -PcomposeReports=true
// Stability baseline:  ./gradlew :fluent:compileKotlinDesktop :fluent:stabilityDump
// Regression check:    ./gradlew :fluent:stabilityCheck
composeCompiler {
    stabilityConfigurationFiles.add(
        rootProject.layout.projectDirectory.file("compose_stability.conf")
    )
    if (providers.gradleProperty("composeReports").orNull == "true") {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }
}

composeStabilityAnalyzer {
    // Library modules: static stability validation only (no runtime trace-all).
    stabilityValidation {
        enabled.set(true)
        outputDir.set(layout.projectDirectory.dir("stability"))
        includeTests.set(false)
        failOnStabilityChange.set(true)
        // Allow first-time clone without baseline to compile; CI can still require the file.
        allowMissingBaseline.set(providers.environmentVariable("CI").orNull != "true")
        stabilityConfigurationFiles.add(
            rootProject.layout.projectDirectory.file("compose_stability.conf")
        )
        ignoredClasses.set(
            listOf(
                // Previews / internal debug helpers if added later
            )
        )
    }
}

group = BuildConfig.group
version = BuildConfig.libraryVersion

kotlin {
    applyTargets()
    sourceSets {
        commonMain.dependencies {
            api(compose.foundation)
            api(project(":fluent-icons-core"))
            implementation(compose.uiUtil)
            implementation(libs.kotlinx.datetime)
            implementation(libs.haze)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

dependencies {
    val processor = (project(":source-generated-processor"))
    add("kspCommonMainMetadata", processor)
}

ksp {
    arg("source.generated.module.name", project.name)
}

// compose-stability-analyzer reads the Android compilation output without declaring it, which
// Gradle rejects as an undeclared dependency regardless of task order — that alone makes
// `stabilityCheck` unrunnable on this KMP + Android module.
//
// Both tasks are wired, not just the check: whichever compilations happen to be present is what
// gets scanned, so dumping with only desktop built and checking with android built reports every
// desktop-only composable (the ContextMenu ones) as removed. Pinning both to the same compilation
// keeps the baseline and the check comparing like with like.
tasks.matching { it.name == "stabilityCheck" || it.name == "stabilityDump" }.configureEach {
    dependsOn("compileAndroidMain", "compileKotlinDesktop")
}

// SourceFilePathProcessor.finish() writes this file straight into :source-generated's source
// tree rather than into this module's build directory. Declaring it as a task output is what
// lets Gradle notice when it is missing: on a fresh clone, or in CI where the task would
// otherwise be restored from the build cache without the file ever being recreated, leaving
// `FluentSourceFile` unresolved in :gallery.
tasks.matching { it.name == "kspCommonMainKotlinMetadata" }.configureEach {
    outputs.file(generatedSourceFile("FluentSourceFile"))
}
