package hcrawler.selector

import com.typesafe.scalalogging.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.util.control.NonFatal

/**
  * Created by hldev on 7/21/17.
  */
class Html(val document: Document) extends HtmlNode {

}

object Html {

  lazy val log = Logger("Html")

  def apply(text: String) = {
    try {
      new Html(Jsoup.parse(text))
    } catch {
      case NonFatal(e) =>
        log.warn("parse document error ", e)
    }
  }

  def apply(text: String, url: String) = {
    try {
      new Html(Jsoup.parse(text, url))
    } catch {
      case NonFatal(e) =>
        log.warn("parse document error ", e)
    }
  }
}