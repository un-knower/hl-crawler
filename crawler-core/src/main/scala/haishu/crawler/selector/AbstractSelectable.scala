package haishu.crawler.selector

/**
 * Created by hldev on 7/21/17.
 */
abstract class AbstractSelectable extends Selectable {
  protected def sourceTexts(): Seq[String]

  // no good ways to collect non-null values
  protected def select(selector: Selector, strings: Seq[String]): Selectable = {
    val results = strings.map(selector.select).filter(_ != null)
    new PlainText(results)
  }

  protected def selectSeq(selector: Selector, strings: Seq[String]): Selectable = {
    val results = strings.flatMap(selector.selectSeq)
    new PlainText(results)
  }

  override def select(selector: Selector): Selectable = select(selector, sourceTexts())

  override def selectSeq(selector: Selector): Selectable = select(selector, sourceTexts())

  override def get(): String = if (all().isEmpty) null else all().head

  def firstSourceText: String = {
    if (sourceTexts().isEmpty) null else sourceTexts().head
  }

  override def all(): Seq[String] = sourceTexts()

  override def toString: String = get()

  override def isMatch = sourceTexts().nonEmpty
}
