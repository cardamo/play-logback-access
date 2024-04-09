package org.databrary.logback

import ch.qos.logback.access.spi._
import ch.qos.logback.core.Context
import play.api.mvc.{RequestHeader, Result}

import java.util
import scala.jdk.CollectionConverters._

final case class PlayAdapter(requestTime : Long, request : RequestHeader, result : Result) extends ServerAdapter {
  def getRequestTimestamp: Long = requestTime
  def getContentLength: Long = IAccessEvent.SENTINEL // no way to get this here
  def getStatusCode: Int = result.header.status
  def buildResponseHeaderMap: util.Map[String, String] = result.header.headers.asJava
}

object PlayAccessEvent {
  private final val portPattern = ":([0-9]{1,5})$".r.pattern
}

final case class PlayAccessEvent(context: Context, requestTime : Long, request : RequestHeader, result : Result, user : Option[String]) extends IAccessEvent {
  private val timestamp = System.currentTimeMillis
  private lazy val adapter = PlayAdapter(requestTime, request, result)

  /* these are only necessary for TeeHttpServletResponse and AccessEventDiscriminator.getSessionAttribute, so it's not worth it: */
  def getRequest: Null = null
  def getResponse: Null = null

  def getTimeStamp: Long = timestamp
  def getElapsedTime: Long = if (requestTime != -1) timestamp - requestTime else -1

  def getRequestURI: String = request.path
  def getRequestURL: String = request.method + " " + request.uri + " " + getProtocol
  def getRemoteHost: String = request.remoteAddress
  def getRemoteUser: String = user.orElse(request match {
      case a : play.api.mvc.Security.AuthenticatedRequest[_, _] => Some(a.user.toString)
      case _ => None
    }).getOrElse(IAccessEvent.NA)
  def getProtocol: String = request.version
  def getMethod: String = request.method
  lazy val (hostName, hostPort) = {
    val h = request.host
    val m = PlayAccessEvent.portPattern.matcher(request.host)
    if (m.find)
      (h.substring(0, m.start), m.group(1).toInt)
    else
      (h, IAccessEvent.SENTINEL /* if (request.secure) 443 else 80 */)
  }
  def getServerName: String = hostName
  def getRemoteAddr: String = request.remoteAddress
  def getRequestHeader(key : String): String = request.headers.get(key.toLowerCase).getOrElse(IAccessEvent.NA)
  def getRequestHeaderNames: util.Enumeration[String] = request.headers.keys.iterator.asJavaEnumeration
  def getRequestHeaderMap: util.Map[String, String] = request.headers.toSimpleMap.asJava
  def getRequestParameterMap: util.Map[String, Array[String]] = request.queryString.view.mapValues(_.toArray).toMap.asJava
  def getAttribute(key : String): String = IAccessEvent.NA
  def getRequestParameter(key : String): Array[String] = request.queryString.get(key).fold(Array(IAccessEvent.NA))(_.toArray)
  def getCookie(key : String): String = request.cookies.get(key).fold(IAccessEvent.NA)(_.value)
  def getContentLength: Long = result.body.contentLength.getOrElse(IAccessEvent.SENTINEL)
  def getStatusCode: Int = result.header.status
  /* these could be implemented for full logging, but may be too costly: */
  def getRequestContent = ""
  def getResponseContent = ""
  def getLocalPort: Int = hostPort
  def getServerAdapter: PlayAdapter = adapter
  def getResponseHeader(key : String): String = result.header.headers.getOrElse(key, IAccessEvent.NA)
  def getResponseHeaderMap: util.Map[String, String] = result.header.headers.asJava
  def getResponseHeaderNameList: util.List[String] = result.header.headers.keys.toSeq.asJava
  def prepareForDeferredProcessing(): Unit = {
    /* what am I supposed to be doing here? */
  }
  def getElapsedSeconds: Long = getElapsedTime / 1000
  def getQueryString: String = request.rawQueryString
  // there is no session id in Play and threads mean little because of async http engine
  def getThreadName: String = IAccessEvent.NA
  def getSessionID: String = IAccessEvent.NA
  def setThreadName(threadName: String): Unit = {}

  def getSequenceNumber: Long = context.getSequenceNumberGenerator.nextSequenceNumber()
}
