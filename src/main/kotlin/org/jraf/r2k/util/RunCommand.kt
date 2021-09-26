package org.jraf.r2k.util

import java.io.File
import java.util.concurrent.TimeUnit

fun runCommand(workingDir: File, vararg command: String): String {
    Log.d("runCommand workingDir=$workingDir command=${command.asList()}")
    val process = ProcessBuilder(command.asList())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    val success = process.waitFor(1, TimeUnit.MINUTES)
    if (!success) {
        process.destroyForcibly()
        Log.w("Timeout reached while executing the command")
        throw Exception("Timeout reached while executing the command")
    }
    val res = process.inputStream.bufferedReader().readText().trim()
    Log.d("Command executed successfully")
    return res
}