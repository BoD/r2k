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
import org.jraf.r2k.arguments.Arguments
import org.jraf.r2k.email.EmailSender
import org.jraf.r2k.url2pdf.Url2PdfExecutor
import org.jraf.r2k.util.Log

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun main(args: Array<String>) {
    Log.d("Hello World!")
    val arguments = Arguments(args)

    val pdfFile = File("/tmp/A random wikipedia page.pdf")

    Url2PdfExecutor().downloadUrlToPdf(
        "https://en.wikipedia.org/wiki/Special:Random",
        pdfFile
    )

    EmailSender(EmailSender.Config(
        authenticationUserName = arguments.emailAuthenticationUserName,
        authenticationPassword = arguments.emailAuthenticationPassword,
        smtpHost = arguments.emailSmtpHost,
        smtpPort = arguments.emailSmtpPort,
        smtpTls = arguments.emailSmtpTls,
    )).sendEmail(
        fromName = arguments.emailFrom,
        fromAddress = arguments.emailFrom,
        toName = arguments.kindleEmail,
        toAddress = arguments.kindleEmail,
        subject = "convert",
        content = "convert",
        attachment = pdfFile
    )
}

