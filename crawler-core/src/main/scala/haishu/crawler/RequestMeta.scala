package haishu.crawler

import java.net.Proxy

import scala.concurrent.duration.Duration

case class RequestMeta(
    redirect: Boolean = true,
    retryTimes: Int = 0,
    mergeCookies: Boolean = true,
    downloadTimeout: Duration = Config.downloadTimtout,
    proxy: Option[Proxy] = None) {

  def retry: RequestMeta = {
    require(retryTimes > 0, "Request's retryTimes can't be zero")
    copy(retryTimes = retryTimes - 1)
  }

  def timeout(d: Duration): RequestMeta = copy(downloadTimeout = d)

  def redirect(r: Boolean): RequestMeta = copy(redirect = r)

  def proxy(p: Proxy): RequestMeta = copy(proxy = Some(p))

}
