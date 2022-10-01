import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    kotlin("kapt")
    application
    id("com.github.johnrengelman.shadow")
}

group = "org.jraf"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(KotlinX.coroutines.jdk8)
    implementation(KotlinX.cli)
    implementation("com.sun.mail:javax.mail:_")
    implementation("com.rometools:rome:_")
    implementation("com.rometools:rome-opml:_")
}

application {
    mainClass.set("MainKt")
}

tasks {
    named<ShadowJar>("shadowJar") {
        // Do not minimize because Rome and Javax Mail are loading classes dynamically
        // minimize()
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
