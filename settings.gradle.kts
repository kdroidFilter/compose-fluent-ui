// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
pluginManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "FluentDesign"
includeBuild("build-plugin")
include("fluent", "fluent-icons-core", "fluent-icons-extended")
include("decorated-window-fluent")
include("fluent-icons-generator")
include("gallery")
include("gallery-processor")
include("source-generated", "source-generated-processor")
