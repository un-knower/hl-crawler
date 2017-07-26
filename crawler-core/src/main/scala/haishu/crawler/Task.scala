package haishu.crawler

/**
 * Created by hldev on 7/21/17.
 */
trait Task {
  def uuid: String
  def site: Site
}
