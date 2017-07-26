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

  def toTask(): Task = new Task {

    override def uuid(): String = {
      if (domain != null) domain
      else UUID.randomUUID().toString
    }

    override def site(): Site = self

  }

  override def toString(): String = {
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

/*
class Site {
  private var domain = null
  private var userAgent = null
  private val defaultCookies = new util.LinkedHashMap[String, String]
  private val cookies = new util.HashMap[String, util.Map[String, String]]
  private var charset = null
  private var sleepTime = 5000
  private var retryTimes = 0
  private var cycleRetryTimes = 0
  private var retrySleepTime = 1000
  private var timeOut = 5000
  private var acceptStatCode = Site.DEFAULT_STATUS_CODE_SET
  private val headers = new util.HashMap[String, String]
  private var useGzip = true
  private var disableCookieManagement = false

  /**
    * Add a cookie with domain {@link #getDomain()}
    *
    * @param name  name
    * @param value value
    * @return this
    */
  def addCookie(name: String, value: String) = {
    defaultCookies.put(name, value)
    this
  }

  /**
    * Add a cookie with specific domain.
    *
    * @param domain domain
    * @param name   name
    * @param value  value
    * @return this
    */
  def addCookie(domain: String, name: String, value: String) = {
    if (!cookies.containsKey(domain)) cookies.put(domain, new util.HashMap[String, String])
    cookies.get(domain).put(name, value)
    this
  }

  /**
    * set user agent
    *
    * @param userAgent userAgent
    * @return this
    */
  def setUserAgent(userAgent: String) = {
    this.userAgent = userAgent
    this
  }

  /**
    * get cookies
    *
    * @return get cookies
    */
  def getCookies = defaultCookies

  /**
    * get cookies of all domains
    *
    * @return get cookies
    */
  def getAllCookies = cookies

  /**
    * get user agent
    *
    * @return user agent
    */
  def getUserAgent = userAgent

  /**
    * get domain
    *
    * @return get domain
    */
  def getDomain = domain

  /**
    * set the domain of site.
    *
    * @param domain domain
    * @return this
    */
  def setDomain(domain: String) = {
    this.domain = domain
    this
  }

  /**
    * Set charset of page manually.<br>
    * When charset is not set or set to null, it can be auto detected by Http header.
    *
    * @param charset charset
    * @return this
    */
  def setCharset(charset: String) = {
    this.charset = charset
    this
  }

  /**
    * get charset set manually
    *
    * @return charset
    */
  def getCharset = charset

  def getTimeOut = timeOut

  /**
    * set timeout for downloader in ms
    *
    * @param timeOut timeOut
    * @return this
    */
  def setTimeOut(timeOut: Int) = {
    this.timeOut = timeOut
    this
  }

  /**
    * Set acceptStatCode.<br>
    * When status code of http response is in acceptStatCodes, it will be processed.<br>
    * {200} by default.<br>
    * It is not necessarily to be set.<br>
    *
    * @param acceptStatCode acceptStatCode
    * @return this
    */
  def setAcceptStatCode(acceptStatCode: util.Set[Integer]) = {
    this.acceptStatCode = acceptStatCode
    this
  }

  /**
    * get acceptStatCode
    *
    * @return acceptStatCode
    */
  def getAcceptStatCode = acceptStatCode

  /**
    * Set the interval between the processing of two pages.<br>
    * Time unit is micro seconds.<br>
    *
    * @param sleepTime sleepTime
    * @return this
    */
  def setSleepTime(sleepTime: Int) = {
    this.sleepTime = sleepTime
    this
  }

  /**
    * Get the interval between the processing of two pages.<br>
    * Time unit is micro seconds.<br>
    *
    * @return the interval between the processing of two pages,
    */
  def getSleepTime = sleepTime

  /**
    * Get retry times immediately when download fail, 0 by default.<br>
    *
    * @return retry times when download fail
    */
  def getRetryTimes = retryTimes

  def getHeaders = headers

  /**
    * Put an Http header for downloader. <br>
    * Use {@link #addCookie(String, String)} for cookie and {@link #setUserAgent(String)} for user-agent. <br>
    *
    * @param key key of http header, there are some keys constant in { @link HttpConstant.Header}
    * @param value value of header
    * @return this
    */
  def addHeader(key: String, value: String) = {
    headers.put(key, value)
    this
  }

  /**
    * Set retry times when download fail, 0 by default.<br>
    *
    * @param retryTimes retryTimes
    * @return this
    */
  def setRetryTimes(retryTimes: Int) = {
    this.retryTimes = retryTimes
    this
  }

  /**
    * When cycleRetryTimes is more than 0, it will add back to scheduler and try download again. <br>
    *
    * @return retry times when download fail
    */
  def getCycleRetryTimes = cycleRetryTimes

  /**
    * Set cycleRetryTimes times when download fail, 0 by default. <br>
    *
    * @param cycleRetryTimes cycleRetryTimes
    * @return this
    */
  def setCycleRetryTimes(cycleRetryTimes: Int) = {
    this.cycleRetryTimes = cycleRetryTimes
    this
  }

  def isUseGzip = useGzip

  def getRetrySleepTime = retrySleepTime

  /**
    * Set retry sleep times when download fail, 1000 by default. <br>
    *
    * @param retrySleepTime retrySleepTime
    * @return this
    */
  def setRetrySleepTime(retrySleepTime: Int) = {
    this.retrySleepTime = retrySleepTime
    this
  }

  /**
    * Whether use gzip. <br>
    * Default is true, you can set it to false to disable gzip.
    *
    * @param useGzip useGzip
    * @return this
    */
  def setUseGzip(useGzip: Boolean) = {
    this.useGzip = useGzip
    this
  }

  def isDisableCookieManagement = disableCookieManagement

  /**
    * Downloader is supposed to store response cookie.
    * Disable it to ignore all cookie fields and stay clean.
    * Warning: Set cookie will still NOT work if disableCookieManagement is true.
    *
    * @param disableCookieManagement disableCookieManagement
    * @return this
    */
  def setDisableCookieManagement(disableCookieManagement: Boolean) = {
    this.disableCookieManagement = disableCookieManagement
    this
  }

  def toTask = new Task() {
    def getUUID = {
      var uuid = thisSite.getDomain
      if (uuid == null) uuid = UUID.randomUUID.toString
      uuid
    }

    def getSite
    =
      return thisSite
  }

  override def equals(o: Any) = {
    if (this eq o) return true
    if (o == null || (getClass ne o.getClass)) return false
    val site = o.asInstanceOf[Site]
    if (cycleRetryTimes != site.cycleRetryTimes) return false
    if (retryTimes != site.retryTimes) return false
    if (sleepTime != site.sleepTime) return false
    if (timeOut != site.timeOut) return false
    if (if (acceptStatCode != null) !acceptStatCode == site.acceptStatCode
    else site.acceptStatCode != null) return false
    if (if (charset != null) !charset == site.charset
    else site.charset != null) return false
    if (if (defaultCookies != null) !defaultCookies == site.defaultCookies
    else site.defaultCookies != null) return false
    if (if (domain != null) !domain == site.domain
    else site.domain != null) return false
    if (if (headers != null) !headers == site.headers
    else site.headers != null) return false
    if (if (userAgent != null) !userAgent == site.userAgent
    else site.userAgent != null) return false
    true
  }

  override def hashCode = {
    var result = if (domain != null) domain.hashCode
    else 0
    result = 31 * result + (if (userAgent != null) userAgent.hashCode
    else 0)
    result = 31 * result + (if (defaultCookies != null) defaultCookies.hashCode
    else 0)
    result = 31 * result + (if (charset != null) charset.hashCode
    else 0)
    result = 31 * result + sleepTime
    result = 31 * result + retryTimes
    result = 31 * result + cycleRetryTimes
    result = 31 * result + timeOut
    result = 31 * result + (if (acceptStatCode != null) acceptStatCode.hashCode
    else 0)
    result = 31 * result + (if (headers != null) headers.hashCode
    else 0)
    result
  }

  override def toString = "Site{" + "domain='" + domain + '\'' + ", userAgent='" + userAgent + '\'' + ", cookies=" + defaultCookies + ", charset='" + charset + '\'' + ", sleepTime=" + sleepTime + ", retryTimes=" + retryTimes + ", cycleRetryTimes=" + cycleRetryTimes + ", timeOut=" + timeOut + ", acceptStatCode=" + acceptStatCode + ", headers=" + headers + '}'
}
*/
