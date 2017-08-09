package haishu.crawler

object Messages {

  case class Download(request: Request)
  case object PollRequest
  case class ScheduleRequest(request: Request)
  case class ParseResponse(response: Response)
  case class ProcessItem(item: Item)
  case class ProcessItemNext(item: Item)
  case class ReplyRequest(request: Request)
  case object NoRequest
  case class RetryRequest(request: Request)
}
