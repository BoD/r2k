package org.jraf.r2k.util

import java.text.SimpleDateFormat
import java.util.Date

object Log {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    private fun getTimestamp(): String? {
        return dateFormat.format(Date())
    }

    fun w(e: Throwable?, message: Any?) {
        System.err.println("${getTimestamp()} W $message")
        e?.printStackTrace()
    }

    fun w(message: Any?) = w(null, message)


    fun d(e: Throwable?, message: Any?) {
        System.err.println("${getTimestamp()} D $message")
        e?.printStackTrace()
    }

    fun d(message: Any?) = d(null, message)
}