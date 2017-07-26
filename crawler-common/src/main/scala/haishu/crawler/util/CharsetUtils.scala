package haishu.crawler.util

import java.nio.charset.Charset

import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import collection.JavaConverters._

/**
 * Created by hldev on 7/25/17.
 */
object CharsetUtils {
  def detectCharset(contentBytes: Array[Byte]): Option[String] = {
    val defaultCharset = Charset.defaultCharset()
    val content = new String(contentBytes, defaultCharset)

    if (StringUtils.isNotEmpty(content)) {
      val doc = Jsoup.parse(content)
      val metas = doc.select("meta").asScala

      metas.map { meta =>
        // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        var metaContent = meta.attr("content")
        val metaCharset = meta.attr("charset")
        if (metaContent.indexOf("charset") != -1) {
          metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length)
          metaContent.split("=")(1)
        } // 2.2、html5 <meta charset="UTF-8" /> if (StringUtils.isNotEmpty(metaCharset))
        else metaCharset
      }.find(_.nonEmpty)
    } else None
  }
}
