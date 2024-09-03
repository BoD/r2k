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

package org.jraf.r2k.feed

import com.rometools.opml.feed.opml.Opml
import com.rometools.opml.feed.opml.Outline
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.WireFeedInput
import com.rometools.rome.io.XmlReader
import org.jraf.r2k.util.Log
import java.io.File
import java.net.URL
import java.util.Date

class FeedReader {
  private fun getFeedList(opmlFile: File): List<Outline> {
    val opml = WireFeedInput().build(opmlFile) as Opml
    return opml.outlines
  }

  private fun getFirstEntryForFeed(feedUrl: String): SyndEntry {
    val syndFeed = SyndFeedInput().build(XmlReader(URL(feedUrl)))
    return syndFeed.entries.first()
  }

  fun getFirstEntryOfAllFeeds(opmlFile: File): List<Entry> {
    return getFeedList(opmlFile).map { feed ->
      Log.d("Get first entry of ${feed.xmlUrl}")
      val firstEntry = try {
        getFirstEntryForFeed(feed.xmlUrl)
      } catch (t: Throwable) {
        Log.w(t, "Error while getting first entry of ${feed.xmlUrl}, skipping it")
        null
      }
      firstEntry?.let { entry ->
        Entry(
          title = feed.title + ": " + entry.title,
          url = entry.link,
          publishedDate = entry.publishedDate ?: entry.updatedDate
        )
      }
    }.filterNotNull()
  }

  data class Entry(val title: String, val url: String, val publishedDate: Date)
}
