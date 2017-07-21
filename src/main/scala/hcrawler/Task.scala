package hcrawler

/**
  * Created by hldev on 7/21/17.
  */
trait Task {
  def UUID: String
  def site: Site
}
