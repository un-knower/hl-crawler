package haishu.crawler

import java.net.Proxy

case class RequestMeta(
    redirect: Boolean = true,
    retryTimes: Int = 0,
    mergeCookies: Boolean = true,
    downloadTimeout: Int = 5000,
    proxy: Option[Proxy] = None) {

  def retry = {
    require(retryTimes > 0, "Request's retryTimes can't be zero")
    copy(retryTimes = retryTimes - 1)
  }

}
