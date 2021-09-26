package org.jraf.r2k.feed

import java.io.File
import java.net.URL
import java.util.Date
import com.rometools.opml.feed.opml.Opml
import com.rometools.opml.feed.opml.Outline
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.WireFeedInput
import com.rometools.rome.io.XmlReader

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
            val firstEntry = getFirstEntryForFeed(feed.xmlUrl)
            Entry(title = feed.title,
                url = firstEntry.link,
                publishedDate = firstEntry.publishedDate ?: firstEntry.updatedDate)
        }
    }

    data class Entry(val title: String, val url: String, val publishedDate: Date)
}