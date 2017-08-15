package haishu.crawler.example

import haishu.crawler.Crawler

import scala.io.StdIn
object Main extends App {

  Crawler.submit(new BycmwSpider)

  StdIn.readLine("Press Enter to exit")

  Crawler.terminate()

}
