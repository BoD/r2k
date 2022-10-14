package org.jraf.r2k.util

import java.io.File
import java.util.concurrent.TimeUnit

fun runCommand(workingDir: File, vararg command: String) {
    Log.d("runCommand workingDir=$workingDir command=${command.asList()}")
    val process = ProcessBuilder(command.asList())
        .directory(workingDir)
        .redirectOutput(File("/dev/null"))
        .redirectError(File("/dev/null"))
        .start()

    val success = process.waitFor(1, TimeUnit.MINUTES)
    if (!success) {
        process.destroyForcibly()
        Log.w("Timeout reached while executing the command")
        throw Exception("Timeout reached while executing the command")
    }
    process.destroyForcibly()
    Log.d("Command executed successfully")
}
