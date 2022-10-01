package org.jraf.r2k.url2pdf

import java.io.File
import org.jraf.r2k.util.Log
import org.jraf.r2k.util.runCommand

class Url2PdfExecutor(private val tmpDir: File) {
    private val scriptFile = File(tmpDir, "url2pdf.js")

    private fun installScriptIfNeeded() {
        if (!scriptFile.exists()) {
            Log.d("Installing url2pdf.js")
            val scriptStream =
                Thread.currentThread().contextClassLoader.getResourceAsStream("org/jraf/r2k/scripts/url2pdf.js")
            val scriptText = scriptStream!!.bufferedReader().readText()
            scriptFile.writeText(scriptText)
            scriptFile.setExecutable(true)
            // Commented out because puppeteer is installed in the Docker image
//            runCommand(tmpDir, "npm", "install", "puppeteer")
            Log.d("Done")
        }
    }

    fun downloadUrlToPdf(url: String, destination: File) {
        Log.d("Downloading $url to $destination")
        installScriptIfNeeded()
        runCommand(tmpDir, "node", scriptFile.absolutePath, url, destination.absolutePath)
        Log.d("Done")
    }
}
