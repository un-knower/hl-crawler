package hcrawler.selector

/**
  * Created by hldev on 7/21/17.
  */
object Selectors {

  def css(expr: String) = CssSelector(expr)

  def css(expr: String, attrName: String) = CssSelector(expr, attrName)

}
