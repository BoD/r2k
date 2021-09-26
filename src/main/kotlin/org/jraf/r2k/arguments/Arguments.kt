/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2021-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalCli::class)

package org.jraf.r2k.arguments

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.default
import kotlinx.cli.required

@Suppress("PropertyName", "PrivatePropertyName")
class Arguments(av: Array<String>) {
    private val parser = ArgParser("r2k")

    val emailAuthenticationUserName: String by parser.option(ArgType.String,
        fullName = "email-authentication-user-name",
        shortName = "u",
        description = "Email authentication user name")
        .required()

    val emailAuthenticationPassword: String by parser.option(ArgType.String,
        fullName = "email-authentication-password",
        shortName = "p",
        description = "Email authentication password")
        .required()

    val emailSmtpHost: String by parser.option(ArgType.String,
        fullName = "email-smtp-host",
        shortName = "s",
        description = "Email smtp host")
        .default("smtp.gmail.com")

    val emailSmtpPort: Int by parser.option(ArgType.Int,
        fullName = "email-smtp-port",
        shortName = "o",
        description = "Email smtp port")
        .default(587)

    val emailSmtpTls: Boolean by parser.option(ArgType.Boolean,
        fullName = "email-smtp-tls",
        shortName = "t",
        description = "Use TLS for email smtp")
        .default(true)

    val kindleEmail: String by parser.option(ArgType.String,
        fullName = "kindle-email",
        shortName = "k",
        description = "Kindle email address")
        .required()

    val emailFrom: String by parser.option(ArgType.String,
        fullName = "email-from",
        shortName = "f",
        description = "Email from address")
        .required()


//    private val accountsStr: List<String> by parser.argument(ArgType.String, "accounts", description = "Accounts").vararg()
//    val accountArguments: List<AccountArgument> get() = accountsStr.map(String::toAccountArgument)

    init {
        parser.parse(av)
    }
}
