package hcrawler.selector

import org.jsoup.nodes.Element

/**
  * Created by hldev on 7/21/17.
  */
class CssSelector(expr: String, attrName: String) {

  private def getValue(element: Element): String = {
    if (attrName == null) element.outerHtml
    else if ("innerHtml".equalsIgnoreCase(attrName)) element.html
    else if ("allText".equalsIgnoreCase(attrName)) element.text
    else element.attr(attrName)
  }

  protected def getText(element: Element): String = {

  }
}

object CssSelector {
  def apply(expr: String, attrName: String): CssSelector = new CssSelector(expr, attrName)

  def apply(expr: String): CssSelector = new CssSelector(expr, null)
}
