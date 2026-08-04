package io.github.composefluent.plugin.build

import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

/**
 * A file that the KSP source processors write directly into `:source-generated`'s source tree
 * instead of their own build directory (see `SourceFilePathProcessor` and `IconSourceProcessor`).
 *
 * Generators must declare these as task outputs. Without that, Gradle cannot tell the files are
 * gone — they live outside the task's build directory — so a cache hit or an up-to-date check
 * leaves `:source-generated` with no sources at all.
 */
fun Project.generatedSourceFile(objectName: String): RegularFile =
    rootProject.layout.projectDirectory.file(
        "source-generated/src/commonMain/kotlin/io/github/composefluent/source/generated/$objectName.kt"
    )

@OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)
fun KotlinMultiplatformExtension.applyTargets(namespaceModule: String = "") {
    jvm("desktop")

    try {
        androidLibrary {
            // Was a duplicated literal, which let the library modules drift away from the
            // value :gallery compiles against.
            compileSdk = BuildConfig.Android.compileSdkVersion
            namespace = "${BuildConfig.packageName}$namespaceModule"
        }
    } catch (_: IllegalStateException) {
        // handle exception when android library plugin was not applied
        androidTarget()
    }

    jvmToolchain(BuildConfig.Jvm.jvmToolchainVersion)
    wasmJs { browser() }
    js { browser() }
    // iosX64 dropped: Compose Multiplatform 1.11 no longer publishes iosx64 artifacts.
    iosArm64()
    iosSimulatorArm64()

    applyHierarchyTemplate {
        sourceSetTrees(KotlinSourceSetTree.main, KotlinSourceSetTree.test)

        common {
            group("skiko") {
                withCompilations {
                    it.target.name != "android"
                }
            }

            group("jvm") {
                withJvm()
                withAndroidTarget()
                withCompilations { it.target.name == "android" }
            }

            group("web") {
                withJs()
                withWasmJs()
            }

            group("apple") {
                withApple()
            }

            group("ios") {
                withIos()
            }
        }
    }
}