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

package org.jraf.r2k.arguments

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

class Arguments(av: Array<String>) {
  private val parser = ArgParser("r2k")

  val emailAuthenticationUserName: String by parser.option(
    type = ArgType.String,
    fullName = "email-authentication-user-name",
    shortName = "u",
    description = "Email authentication user name"
  )
    .required()

  val emailAuthenticationPassword: String by parser.option(
    type = ArgType.String,
    fullName = "email-authentication-password",
    shortName = "p",
    description = "Email authentication password"
  )
    .required()

  val emailSmtpHost: String by parser.option(
    type = ArgType.String,
    fullName = "email-smtp-host",
    shortName = "s",
    description = "Email smtp host"
  )
    .default("smtp.gmail.com")

  val emailSmtpPort: Int by parser.option(
    type = ArgType.Int,
    fullName = "email-smtp-port",
    shortName = "o",
    description = "Email smtp port"
  )
    .default(587)

  val emailSmtpTls: Boolean by parser.option(
    type = ArgType.Boolean,
    fullName = "email-smtp-tls",
    shortName = "t",
    description = "Use TLS for email smtp"
  )
    .default(true)

  val kindleEmail: String by parser.option(
    type = ArgType.String,
    fullName = "kindle-email",
    shortName = "k",
    description = "Kindle email address"
  )
    .required()

  val emailFrom: String by parser.option(
    type = ArgType.String,
    fullName = "email-from",
    shortName = "f",
    description = "Email from address"
  )
    .required()


  val pathToIDontCareAboutCookiesExtension: String by parser.option(
    type = ArgType.String,
    fullName = "path-to-i-dont-care-about-cookies-extension",
    shortName = "x",
    description = "Path to the I don't care about cookies extension"
  )
    .required()

  val opmlFile: String by parser.argument(
    type = ArgType.String,
    fullName = "opml-file",
    description = "OPML file"
  )

  init {
    parser.parse(av)
  }
}
