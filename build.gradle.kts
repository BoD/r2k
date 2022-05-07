import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.KOTLIN
    kotlin("kapt") version Versions.KOTLIN
    application
    id("com.github.johnrengelman.shadow") version Versions.SHADOW_PLUGIN
    id("com.github.ben-manes.versions") version Versions.BEN_MANES_VERSIONS_PLUGIN
}

group = "org.jraf"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8", Versions.KOTLIN))
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", Versions.COROUTINES)
    implementation("org.jetbrains.kotlinx", "kotlinx-cli", Versions.KOTLINX_CLI)
    implementation("com.sun.mail", "javax.mail", Versions.JAVAX_MAIL)
    implementation("com.rometools", "rome", Versions.ROME)
    implementation("com.rometools", "rome-opml", Versions.ROME)

    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

application {
    mainClass.set("MainKt")
}

tasks {
    named<ShadowJar>("shadowJar") {
        // Do not minimize because Rome and Javax Mail are loading classes dynamically
        // minimize()
    }

    // Configuration for gradle-versions-plugin
    // Run `./gradlew dependencyUpdates` to see latest versions of dependencies
    withType<DependencyUpdatesTask> {
        resolutionStrategy {
            componentSelection {
                all {
                    if (
                        setOf("alpha", "beta", "rc", "preview", "eap", "m1", "m2").any {
                            candidate.version.contains(it, true)
                        }
                    ) {
                        reject("Non stable")
                    }
                }
            }
        }
    }

    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = Versions.GRADLE
    }
}

// Implements https://github.com/brianm/really-executable-jars-maven-plugin maven plugin behaviour.
// To check details how it works, see http://skife.org/java/unix/2011/06/20/really_executable_jars.html.
tasks.register<DefaultTask>("shadowJarExecutable") {
    description = "Creates a self-executable file, that runs the generated shadow jar"
    group = "Distribution"

    inputs.files(tasks.named("shadowJar"))
    val origFile = inputs.files.singleFile
    outputs.files(File(origFile.parentFile, origFile.nameWithoutExtension + "-executable"))

    doLast {
        val execFile: File = outputs.files.files.first()
        val out = execFile.outputStream()
        out.write("#!/bin/sh\n\nexec java -jar \"\$0\" \"\$@\"\n\n".toByteArray())
        out.write(inputs.files.singleFile.readBytes())
        out.flush()
        out.close()
        execFile.setExecutable(true, false)
    }
}

// Run `./gradlew shadowJarExecutable` to build the "really executable jar"
