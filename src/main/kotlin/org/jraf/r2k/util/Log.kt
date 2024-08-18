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
