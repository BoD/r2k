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
            )
          )
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
                            """.trimIndent()
            )
          )

          page.pdf(
            Page.PdfOptions()
              .setWidth("5in")
              .setHeight("8in")
              .setPrintBackground(false)
              .setPath(destination.toPath())
          )
        }
    }
    Log.d("Done")
  }
}
