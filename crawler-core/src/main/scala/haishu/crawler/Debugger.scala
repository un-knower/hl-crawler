package haishu.crawler

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.time.{LocalDate, LocalDateTime}

import haishu.crawler.util.Jackson

object Debugger {

  val mapper = Jackson.objectMapper

  val path = "/home/hldev/crawler-log"

  def apply(obj: Any): Unit = log(obj)


  def log(obj: Any) = {
    val o = mapper.writeValueAsString(obj)
    val filePath = Paths.get(s"$path-${LocalDate.now()}.json")

    if (Files.notExists(filePath)) {
      Files.createFile(filePath)
    }

    val str = "//" + LocalDateTime.now() + "\n" + o +"\n"

    Files.write(filePath, str.getBytes, StandardOpenOption.APPEND)

  }
}
