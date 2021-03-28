package org.databrary.logback

import ch.qos.logback.access.spi._
import play.api.mvc.{RequestHeader,Result}
import scala.collection.JavaConverters.{asJavaEnumerationConverter,mapAsJavaMapConverter,seqAsJavaListConverter}

final case class PlayAdapter(requestTime : Long, request : RequestHeader, result : Result) extends ServerAdapter {
  def getRequestTimestamp = requestTime
  def getContentLength = IAccessEvent.SENTINEL // no way to get this here
  def getStatusCode = result.header.status
  def buildResponseHeaderMap = result.header.headers.asJava
}

object PlayAccessEvent {
  private final val portPattern = ":([0-9]{1,5})$".r.pattern
}

final case class PlayAccessEvent(requestTime : Long, request : RequestHeader, result : Result, user : Option[String]) extends IAccessEvent {
  private[this] val timestamp = System.currentTimeMillis
  private[this] lazy val adapter = PlayAdapter(requestTime, request, result)

  /* these are only necessary for TeeHttpServletResponse and AccessEventDiscriminator.getSessionAttribute, so it's not worth it: */
  def getRequest = null
  def getResponse = null

  def getTimeStamp = timestamp
  def getElapsedTime = if (requestTime != -1) timestamp - requestTime else -1

  def getRequestURI = request.path
  def getRequestURL = request.method + " " + request.uri + " " + getProtocol
  def getRemoteHost = request.remoteAddress
  def getRemoteUser = user.orElse(request match {
      case a : play.api.mvc.Security.AuthenticatedRequest[_, _] => Some(a.user.toString)
      case _ => None
    }).getOrElse(IAccessEvent.NA)
  def getProtocol = request.version
  def getMethod = request.method
  lazy val (hostName, hostPort) = {
    val h = request.host
    val m = PlayAccessEvent.portPattern.matcher(request.host)
    if (m.find)
      (h.substring(0, m.start), m.group(1).toInt)
    else
      (h, IAccessEvent.SENTINEL /* if (request.secure) 443 else 80 */)
  }
  def getServerName = hostName
  def getRemoteAddr = request.remoteAddress
  def getRequestHeader(key : String) = request.headers.get(key.toLowerCase).getOrElse(IAccessEvent.NA)
  def getRequestHeaderNames = request.headers.keys.iterator.asJavaEnumeration
  def getRequestHeaderMap = request.headers.toSimpleMap.asJava
  def getRequestParameterMap = request.queryString.mapValues(_.toArray).toMap.asJava
  def getAttribute(key : String) = IAccessEvent.NA
  def getRequestParameter(key : String) = request.queryString.get(key).fold(Array(IAccessEvent.NA))(_.toArray)
  def getCookie(key : String) = request.cookies.get(key).fold(IAccessEvent.NA)(_.value)
  def getContentLength = result.body.contentLength.getOrElse(IAccessEvent.SENTINEL)
  def getStatusCode = result.header.status
  /* these could be implemented for full logging, but may be too costly: */
  def getRequestContent = ""
  def getResponseContent = ""
  def getLocalPort = hostPort
  def getServerAdapter = adapter
  def getResponseHeader(key : String) = result.header.headers.getOrElse(key, IAccessEvent.NA)
  def getResponseHeaderMap = result.header.headers.asJava
  def getResponseHeaderNameList = result.header.headers.keys.toSeq.asJava
  def prepareForDeferredProcessing() {
    /* what am I supposed to be doing here? */
  }
  def getElapsedSeconds: Long = getElapsedTime / 1000
  def getQueryString: String = request.rawQueryString
  // there is no session id in Play and threads mean little because of async http engine
  def getThreadName: String = IAccessEvent.NA
  def getSessionID: String = IAccessEvent.NA
  def setThreadName(threadName: String): Unit = {}
}
