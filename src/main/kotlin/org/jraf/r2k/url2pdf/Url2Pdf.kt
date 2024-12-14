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

package org.jraf.r2k.url2pdf

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import org.jraf.r2k.util.Log
import java.io.File

class Url2Pdf(
  private val tmpDir: File,
  private val pathToIDontCareAboutCookiesExtension: String,
) {
  fun downloadUrlToPdf(url: String, destination: File) {
    Log.d("Downloading $url to $destination")
    val headless = true
    try {
      Playwright.create().use { playwright ->
        playwright.chromium().launchPersistentContext(
          File(tmpDir, "playwright").toPath(),
          BrowserType.LaunchPersistentContextOptions()
            .setHeadless(headless)
            .setArgs(
              listOf(
                "--headless=new",
                "--disable-extensions-except=$pathToIDontCareAboutCookiesExtension",
                "--load-extension=$pathToIDontCareAboutCookiesExtension",
              ),
            )
            .setBypassCSP(true),
        )
          .use { browserContext ->
            browserContext.setDefaultTimeout(45_000.0)
            val page = browserContext.newPage()
            page.navigate(url)

            Log.d("Waiting for the page to finish loading")
            try {
              page.waitForLoadState(LoadState.NETWORKIDLE)
            } catch (t: Throwable) {
              // Happens when closing the browser, this is expected
            }

            // Improve the style for the PDF
            page.addStyleTag(
              Page.AddStyleTagOptions().setContent(
                """
              * {
                color: black!important;
                font-variant-ligatures: none!important;
              }
              """.trimIndent(),
              ),
            )

            page.pdf(
              Page.PdfOptions()
                .setWidth("5in")
                .setHeight("8in")
                .setPrintBackground(false)
                .setPath(destination.toPath()),
            )
          }
      }
    } finally {
      // PlayWright seems to leave some Chrome core dumps behind. Clean that so the disk does not fill up.
      val files = File("/app").listFiles { _, name -> name.startsWith("core.") }
      if (files != null) {
        for (file in files) {
          file.delete()
        }
      }
    }
    Log.d("Done")
  }
}
