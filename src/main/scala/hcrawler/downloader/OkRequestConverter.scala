package hcrawler
package downloader

import hcrawler.model.HttpRequestBody
import okhttp3.{Headers, MediaType, Request => OkRequest, RequestBody => OkRequestBody}

import collection.JavaConverters._

/**
  * Created by hldev on 7/24/17.
  */
object OkRequestConverter {

  def convert(request: Request, site: Site): OkRequest = {
    // TODO: Cookie manaee
    new OkRequest.Builder()
      .url(request.url)
      .method(request.method, request.requestBody.map(convert).getOrElse(null))
      .headers(Headers.of(request.headers.asJava))
      .build()
  }

  def convert(requestBody: HttpRequestBody): OkRequestBody = {
    val mediaType = encodeMediaType(requestBody.contentType, requestBody.charset)
    OkRequestBody.create(MediaType.parse(mediaType), requestBody.body)
  }

  def encodeMediaType(contentType: String, charset: String): String = {
    s"$contentType; charset=$charset"
  }

}
