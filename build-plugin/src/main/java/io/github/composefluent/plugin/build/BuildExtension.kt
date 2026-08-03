package io.github.composefluent.plugin.build

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

@OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)
fun KotlinMultiplatformExtension.applyTargets(namespaceModule: String = "") {
    jvm("desktop")

    try {
        androidLibrary {
            compileSdk = 35
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