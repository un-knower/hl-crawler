package haishu.crawler

import java.util.UUID

import haishu.crawler.util.HttpConstant

case class Site(
    domain: String = null,
    userAgent: String = "",
    defaultCookies: Map[String, String] = Map(),
    cookies: Map[String, Map[String, String]] = Map(),
    charset: String = "UTF-8",
    sleepTime: Int = 5000,
    retryTimes: Int = 0,
    cycleRetryTimes: Int = 0,
    retrySleepTime: Int = 1000,
    timeOut: Int = 5000,
    acceptStatCodes: Set[Int] = Set(HttpConstant.StatusCode.OK),
    headers: Map[String, String] = Map(),
    useGzip: Boolean = true,
    disableCookieManagement: Boolean = false) { self =>

  def cookie(name: String, value: String): Site = copy(defaultCookies = defaultCookies + (name -> value))

  def cookie(domain: String, name: String, value: String): Site = {
    if (cookies.contains(domain)) copy(cookies = cookies.updated(domain, cookies(domain) + (name -> value)))
    else copy(cookies = cookies + (domain -> Map(name -> value)))
  }

  def userAgent(userAgent: String): Site = copy(userAgent = userAgent)

  def domain(domain: String): Site = copy(domain = domain)

  def charset(charset: String): Site = copy(charset = charset)

  def sleepTime(sleepTime: Int): Site = copy(sleepTime = sleepTime)

  def retryTimes(retryTimes: Int): Site = copy(retryTimes = retryTimes)

  def cycleRetryTimes(cycleRetryTimes: Int): Site = copy(cycleRetryTimes = cycleRetryTimes)

  def retrySleepTime(retrySleepTime: Int): Site = copy(retrySleepTime = retrySleepTime)

  def timeOut(timeOut: Int): Site = copy(timeOut = timeOut)

  def acceptStatCode(acceptStatCodes: Set[Int]): Site = copy(acceptStatCodes = acceptStatCodes)

  def headers(headers: Map[String, String]): Site = copy(headers = headers)

  def header(name: String, value: String): Site = copy(headers = headers + (name -> value))

  def useGzip(useGzip: Boolean): Site = copy(useGzip = useGzip)

  def disableCookieManagement(disableCookieManagement: Boolean): Site = copy(disableCookieManagement = disableCookieManagement)

  def toTask: Task = new Task {

    override def uuid: String = {
      if (domain != null) domain
      else UUID.randomUUID().toString
    }

    override def site: Site = self

  }

  override def toString: String = {
    "Site{" +
      "domain='" + domain + '\'' +
      ", userAgent='" + userAgent + '\'' +
      ", cookies=" + defaultCookies +
      ", charset='" + charset + '\'' +
      ", sleepTime=" + sleepTime +
      ", retryTimes=" + retryTimes +
      ", cycleRetryTimes=" + cycleRetryTimes +
      ", timeOut=" + timeOut +
      ", acceptStatCodes=" + acceptStatCodes +
      ", headers=" + headers +
      '}'
  }
}
