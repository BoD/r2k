/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2020-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import org.jraf.r2k.arguments.Arguments
import org.jraf.r2k.email.EmailSender
import org.jraf.r2k.feed.FeedReader
import org.jraf.r2k.url2pdf.Url2PdfExecutor
import org.jraf.r2k.util.Log

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun main(args: Array<String>) {
    Log.d("Hello World!")
    val arguments = Arguments(args)

    val tmpDir = File("/tmp/r2k")
    tmpDir.mkdirs()

    val sentEntryUrlList = mutableSetOf<String>()
    val url2PdfExecutor = Url2PdfExecutor(tmpDir)
    val emailSender = EmailSender(EmailSender.Config(
        authenticationUserName = arguments.emailAuthenticationUserName,
        authenticationPassword = arguments.emailAuthenticationPassword,
        smtpHost = arguments.emailSmtpHost,
        smtpPort = arguments.emailSmtpPort,
        smtpTls = arguments.emailSmtpTls,
    ))

    while (true) {
        try {
            Log.d("Fetching entry list")
            val entryList = FeedReader().getFirstEntryOfAllFeeds(File(arguments.opmlFile))
            Log.d(entryList)
            for (entry in entryList) {
                if (entry.url in sentEntryUrlList) continue
                val pdfFile = File(tmpDir, "${entry.title} ${formatDate(entry.publishedDate)}.pdf")
                if (pdfFile.exists()) {
                    Log.d("$pdfFile already present: ignore")
                    sentEntryUrlList += entry.url
                    continue
                }

                url2PdfExecutor.downloadUrlToPdf(
                    url = entry.url,
                    destination = pdfFile
                )

                emailSender.sendEmail(
                    fromName = arguments.emailFrom,
                    fromAddress = arguments.emailFrom,
                    toName = arguments.kindleEmail,
                    toAddress = arguments.kindleEmail,
                    subject = "convert",
                    content = "convert",
                    attachment = pdfFile
                )
                sentEntryUrlList += entry.url
            }
        } catch (t: Throwable) {
            Log.w(t, "Caught exception in main loop")
        }

        Log.d("Sleep 4 hours")
        TimeUnit.HOURS.sleep(4)
    }
}

private fun formatDate(date: Date): String = SimpleDateFormat("yyyy-MM-dd").format(date)