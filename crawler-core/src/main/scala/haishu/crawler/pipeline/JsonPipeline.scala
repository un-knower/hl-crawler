package haishu.crawler.pipeline

import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import com.fasterxml.jackson.databind.ObjectMapper
import haishu.crawler.Item
import haishu.crawler.util.Jackson

class JsonPipeline(path: Path) extends Pipeline {

  val mapper: ObjectMapper = Jackson.objectMapper

  var items: Vector[Item] = Vector[Item]()

  override def process(item: Item): Option[Item] = {
    items = items :+ item
    Some(item)
  }


  override def onOpen(): Unit = {
    if (Files.notExists(path)) Files.createFile(path)
  }

  override def onClose(): Unit = {
    val json = mapper.writeValueAsString(items)
    Files.write(path, json.getBytes, StandardOpenOption.APPEND)
  }
}

object JsonPipeline {
  def apply(path: Path): JsonPipeline = new JsonPipeline(path)

  def apply(path: String): JsonPipeline = new JsonPipeline(Paths.get(path))
}
