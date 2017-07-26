package haishu.crawler.util

import java.net.{MalformedURLException, URL}

object UrlUtils {

  def canonicalizeUrl(url: String, refer: String): String = try {
    val base = new URL(refer)
    // workaround: java resolves '//path/file + ?foo' to '//path/?foo', not '//path/file?foo' as desired
    val url2 = if (url.startsWith("?")) base.getPath() + url else url
    val abs = new URL(base, url)
    encodeIllegalCharacterInUrl(abs.toExternalForm())
  } catch {
    case e: MalformedURLException =>
      try {
        new URL(refer).toExternalForm()
      } catch {
        case _: MalformedURLException => ""
      }
  }

  def encodeIllegalCharacterInUrl(url: String): String = {
    //TODO more charator support
    url.replace(" ", "%20")
  }

}
