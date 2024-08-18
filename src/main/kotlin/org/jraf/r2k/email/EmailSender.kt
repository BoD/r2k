/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2024-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.r2k.email

import org.jraf.r2k.util.Log
import java.io.File
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class EmailSender(config: Config) {
  private val session = createSession(config)

  private fun createSession(config: Config): Session {
    val sessionProperties = Properties()
    sessionProperties["mail.smtp.host"] = config.smtpHost
    sessionProperties["mail.smtp.port"] = config.smtpPort
    sessionProperties["mail.smtp.auth"] = true
    sessionProperties["mail.smtp.starttls.enable"] = config.smtpTls

    val session = Session.getInstance(sessionProperties,
      object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
          return PasswordAuthentication(config.authenticationUserName, config.authenticationPassword)
        }
      })
    return session
  }

  fun sendEmail(
    fromName: String,
    fromAddress: String,
    toName: String,
    toAddress: String,
    subject: String,
    content: String,
    attachment: File,
  ) {
    Log.d("Sending an email to $toAddress with attachment $attachment")
    val mimeBodyPart = MimeBodyPart().apply {
      setContent(content, "text/html")
    }

    val attachmentBodyPart = MimeBodyPart().apply {
      attachFile(attachment)
    }

    val message = MimeMessage(session).apply {
      setFrom(InternetAddress(fromAddress, fromName))
      setRecipient(
        Message.RecipientType.TO,
        InternetAddress(toAddress, toName)
      )
      this.subject = subject
      setContent(MimeMultipart().apply {
        addBodyPart(mimeBodyPart)
        addBodyPart(attachmentBodyPart)
      })
    }

    Transport.send(message)
    Log.d("Done")
  }

  data class Config(
    val authenticationUserName: String,
    val authenticationPassword: String,
    val smtpHost: String,
    val smtpPort: Int,
    val smtpTls: Boolean,
  )
}
