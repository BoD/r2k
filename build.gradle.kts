import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import com.bmuschko.gradle.docker.tasks.image.Dockerfile.CopyFileInstruction
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    id("application")
    id("com.github.johnrengelman.shadow")
    id("com.bmuschko.docker-java-application")
}

group = "org.jraf"
version = "1.0.0"

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation(KotlinX.coroutines.jdk8)
    implementation(KotlinX.cli)
    implementation("com.sun.mail:javax.mail:_")
    implementation("com.rometools:rome:_")
    implementation("com.rometools:rome-opml:_")
    implementation("com.microsoft.playwright:playwright:_")
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
        // Use OpenJ9 instead of the default one
        baseImage.set("adoptopenjdk/openjdk11-openj9:x86_64-ubuntu-jre-11.0.18_10_openj9-0.36.1")
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
    environmentVariable("PLAYWRIGHT_BROWSERS_PATH", "/playwright-browsers")

    // Install curl
    //runCommand("apt-get install -y curl gnupg")

    // Install browser dependencies
    runCommand("apt-get update")
    runCommand(
        """
        apt-get install -y \
          libxcb-shm0\
          libx11-xcb1\            
          libx11-6\               
          libxcb1\                
          libxext6\               
          libxrandr2\             
          libxcomposite1\         
          libxcursor1\            
          libxdamage1\            
          libxfixes3\             
          libxi6\                 
          libgtk-3-0\             
          libpangocairo-1.0-0\    
          libpango-1.0-0\         
          libatk1.0-0\            
          libcairo-gobject2\      
          libcairo2\              
          libgdk-pixbuf2.0-0\     
          libglib2.0-0\           
          libasound2\             
          libxrender1\            
          libdbus-1-3
        """.trimIndent()
    )

    // Install "I Still Don't Care About Cookies" extension
    runCommand("curl https://github.com/OhMyGuus/I-Still-Dont-Care-About-Cookies/releases/download/v1.1.4/ISDCAC-chrome-source.zip -o ISDCAC-chrome-source.zip")
    runCommand("unzip ISDCAC-chrome-source.zip -d /ISDCAC-chrome-source")
    runCommand("rm ISDCAC-chrome-source.zip")

    // Move the COPY instructions to the end
    // See https://github.com/bmuschko/gradle-docker-plugin/issues/1093
    instructions.set(
        instructions.get().sortedBy { instruction ->
            if (instruction.keyword == CopyFileInstruction.KEYWORD) 1 else 0
        }
    )
}

// `./gradlew shadowJarExecutable` to build the "really executable jar"
// `./gradlew refreshVersions` to update dependencies
// `DOCKER_USERNAME=<your docker hub login> DOCKER_PASSWORD=<your docker hub password> ./gradlew dockerPushImage` to build and push the image
