package haishu.crawler.pipeline

/**
 * Created by hldev on 7/25/17.
 */
trait CollectorPipeline[T] extends Pipeline {
  def collected: Seq[T]
}
