// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.ktlint) apply true
    id("jacoco")
}

val jacocoVersion = libs.versions.jacoco.get().toString()

val fileFilter =
    listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*$[0-9]*.*",
        "**/*Component*.*",
        "**/*BR*.*",
        "**/Manifest*.*",
        "**/*Companion*.*",
        "**/*Module*.*",
        "**/*Dagger*.*",
        "**/*Hilt*.*",
        "**/*MembersInjector*.*",
        "**/*_Factory*.*",
        "**/*_Provide*Factory*.*",
        "**/*Extensions*.*",
    )

subprojects {
    val subproject = this
    plugins.withId("com.android.library") {
        configureAndroidModule(subproject)
    }
    plugins.withId("com.android.application") {
        configureAndroidModule(subproject)
    }
}

fun configureAndroidModule(project: Project) {
    with(project) {
        apply(plugin = "jacoco")
        apply(plugin = "io.gitlab.arturbosch.detekt")
        apply(plugin = "org.jlleitschuh.gradle.ktlint")

        extensions.configure<JacocoPluginExtension> {
            toolVersion = jacocoVersion
        }

        tasks.withType<Test> {
            extensions.configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
        }

        tasks.register<JacocoReport>("jacocoTestReport") {
            dependsOn("testDebugUnitTest")

            reports {
                xml.required.set(true)
                html.required.set(true)
            }

            val debugTree =
                fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
                    exclude(fileFilter)
                }
            val mainSrc = "${project.projectDir}/src/main/java"

            sourceDirectories.setFrom(files(mainSrc))
            classDirectories.setFrom(files(debugTree))
            executionData.setFrom(
                fileTree(project.layout.buildDirectory.get()) {
                    include("jacoco/testDebugUnitTest.exec")
                },
            )
        }
    }
}

// Detekt configuration
detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$projectDir/config/detekt/detekt.yml"))
}

// Ktlint configuration
ktlint {
    android.set(true)
    ignoreFailures.set(false)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}
