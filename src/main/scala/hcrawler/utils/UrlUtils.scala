package hcrawler
package utils

import java.net.URL
import java.net.MalformedURLException

object UrlUtils {

  def canonicalizeUrl(url: String, refer: String): String = {
    var base: URL = null
    try { 
      try { 
        base = new URL(refer)
      } catch {
        case e: MalformedURLException => 
          val abs = new URL(refer)
          return abs.toExternalForm()
      }
      // workaround: java resolves '//path/file + ?foo' to '//path/?foo', not '//path/file?foo' as desired
      val url2 = if (url.startsWith("?")) (base.getPath() + url) else url
      val abs = new URL(base, url)
      return encodeIllegalCharacterInUrl(abs.toExternalForm())
    } catch {
      case e: MalformedURLException => ""
    }
  }

  def encodeIllegalCharacterInUrl(url: String): String = {
    //TODO more charator support
    url.replace(" ", "%20");
  }
}