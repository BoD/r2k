import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    kotlin("kapt")
    application
    id("com.github.johnrengelman.shadow")
    id("com.bmuschko.docker-java-application")
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

docker {
    javaApplication {
        maintainer.set("BoD <BoD@JRAF.org>")
        ports.set(emptyList())
        images.add("bodlulu/${rootProject.name}:latest")
        jvmArgs.set(listOf("-Xms16m", "-Xmx128m"))
    }
    registryCredentials {
        username.set(System.getenv("DOCKER_USERNAME"))
        password.set(System.getenv("DOCKER_PASSWORD"))
    }

}

tasks.withType<DockerBuildImage> {
    platform.set("linux/amd64")
}

tasks.withType<Dockerfile> {
    // Install chrome, node, puppeteer, and dependencies
    // This is heavily based on https://github.com/puppeteer/puppeteer/blob/main/docker/Dockerfile
    runCommand("apt-get update")
    runCommand("apt-get install -y curl gnupg")
    runCommand(
        """
            apt-get update \
            && apt-get install -y wget gnupg \
            && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
            && sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list' \
            && apt-get update \
            && apt-get install -y google-chrome-stable fonts-ipafont-gothic fonts-wqy-zenhei fonts-thai-tlwg fonts-khmeros fonts-kacst fonts-freefont-ttf libxss1 \
              --no-install-recommends \
            && rm -rf /var/lib/apt/lists/*
        """.trimIndent()
    )
    runCommand("curl -fsSL https://deb.nodesource.com/setup_18.x | bash -")
    runCommand("apt-get install -y nodejs")
    runCommand("npm install -g puppeteer-core")
    runCommand(
        """
            groupadd -r pptruser && useradd -r -g pptruser -G audio,video pptruser \
            && mkdir -p /home/pptruser/Downloads \
            && chown -R pptruser:pptruser /home/pptruser
        """.trimIndent()
    )
    user("pptruser")
    environmentVariable("NODE_PATH", "/usr/lib/node_modules")
    environmentVariable("MALLOC_ARENA_MAX", "4")
}

// `./gradlew shadowJarExecutable` to build the "really executable jar"
// `./gradlew refreshVersions` to update dependencies
// `DOCKER_USERNAME=<your docker hub login> DOCKER_PASSWORD=<your docker hub password> ./gradlew dockerPushImage` to build and push the image
