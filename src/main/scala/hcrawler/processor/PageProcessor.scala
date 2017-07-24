package hcrawler
package processor

/**
  * Created by hldev on 7/24/17.
  */
trait PageProcessor {

  def process(page: Page): Unit

  def site: Site

}
