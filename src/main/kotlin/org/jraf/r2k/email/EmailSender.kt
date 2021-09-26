package org.jraf.r2k.email

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
import org.jraf.r2k.util.Log

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