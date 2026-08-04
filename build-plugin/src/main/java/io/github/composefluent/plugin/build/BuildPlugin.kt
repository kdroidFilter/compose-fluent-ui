package io.github.composefluent.plugin.build

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import io.github.composefluent.plugin.build.BuildConfig.androidVersionCode
import io.github.composefluent.plugin.build.BuildConfig.branch
import io.github.composefluent.plugin.build.BuildConfig.defaultLibraryVersion
import io.github.composefluent.plugin.build.BuildConfig.integerVersionName
import io.github.composefluent.plugin.build.BuildConfig.isRelease
import io.github.composefluent.plugin.build.BuildConfig.isReleaseBuild
import io.github.composefluent.plugin.build.BuildConfig.libraryVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

class BuildPlugin : Plugin<Project> {
    override fun apply(target: Project) {

        setupLibraryVersion(target)

        target.allprojects.forEach { project ->
            project.afterEvaluate {

                project.extensions.findByType<MavenPublishBaseExtension>()?.apply {
                    setupMavenPortalPublishing(project)
                }
            }
        }
    }

    private fun MavenPublishBaseExtension.setupMavenPortalPublishing(target: Project) {
        publishToMavenCentral()
        coordinates(target.group.toString(), target.name, target.version.toString())

        pom {
            name.set("Compose Fluent UI")
            description.set("A Fluent Design UI library for Compose Multiplatform.")
            inceptionYear.set("2025")
            url.set(BuildConfig.repositoryUrl)
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("konyaco")
                    name.set("Yaco")
                    url.set("https://github.com/konyaco")
                }
                developer {
                    id.set("sanlorng")
                    name.set("Sanlorng")
                    url.set("https://github.com/sanlorng")
                }
                developer {
                    id.set("kdroidFilter")
                    name.set("Elie G.")
                    url.set("https://github.com/kdroidFilter")
                }
            }
            scm {
                url.set(BuildConfig.repositoryUrl)
                connection.set("scm:git:git://github.com/NucleusFramework/compose-fluent-ui.git")
                developerConnection.set("scm:git:ssh://github.com/NucleusFramework/compose-fluent-ui.git")
            }
        }

        // The CI signs with an in-memory key (ORG_GRADLE_PROJECT_signingInMemoryKey);
        // local publishes stay unsigned unless a GPG keyring is configured.
        if (target.hasProperty("signingInMemoryKey") || target.hasProperty("signing.keyId")) {
            signAllPublications()
        }
    }


    private fun setupLibraryVersion(target: Project) {
        val providers = target.providers

        val currentBranch = providers.exec {
            commandLine("git", "branch", "--show-current")
            isIgnoreExitValue = true
        }.standardOutput
            .asText
            .orNull
            ?.trim()
            ?.takeIf { it.isNotEmpty() }

        // Latest reachable tag, used when a Pages/preview build asks for the
        // released version without going through a tag push.
        val latestTag = providers.exec {
            commandLine("git", "describe", "--abbrev=0", "--tags")
            isIgnoreExitValue = true
        }.standardOutput.asText.orNull
            ?.trim()
            ?.removePrefix("v")
            ?.takeIf { it.isNotBlank() && it.first().isDigit() }

        // Tag-driven: the CI exports RELEASE_VERSION=<tag>. Everything else — local
        // builds, PR checks — simply builds the current version.
        val resolvedRelease = BuildConfig.releaseVersion ?: latestTag?.takeIf { isRelease }
        libraryVersion = resolvedRelease ?: defaultLibraryVersion
        isReleaseBuild = resolvedRelease != null

        // A release pins the gallery's source deep links to the tag so they keep
        // resolving forever; a dev build follows whatever branch is checked out.
        branch = resolvedRelease?.let { "v$it" } ?: currentBranch ?: "master"

        // Native installers and app stores only accept a plain X.Y.Z.
        val versionParts = libraryVersion.substringBefore("-")
            .split(".")
            .let { parts -> (0..2).map { parts.getOrNull(it)?.toIntOrNull() ?: 0 } }
        integerVersionName = versionParts.joinToString(".")

        val (major, minor, patch) = versionParts
        androidVersionCode = major * 10_000 + minor * 100 + patch
    }
}
