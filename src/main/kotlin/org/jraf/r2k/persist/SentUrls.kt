package org.jraf.r2k.persist

import java.io.File

class SentUrls(private val file: File) {
    private val urls = mutableSetOf<String>()

    fun add(url: String): Boolean {
        return urls.add(url).also { if (it) save() }
    }

    operator fun plusAssign(url: String) {
        add(url)
    }

    operator fun contains(url: String): Boolean {
        return urls.contains(url)
    }

    fun save() {
        file.writeText(urls.joinToString("\n"))
    }

    fun load() {
        if (file.exists()) {
            urls.addAll(file.readLines())
        }
    }
}
