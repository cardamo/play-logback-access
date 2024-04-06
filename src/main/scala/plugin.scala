package org.databrary

import java.io.File
import java.net.URL

import org.apache.pekko.stream.Materializer
import ch.qos.logback.access.joran.JoranConfigurator
import ch.qos.logback.access.spi.IAccessEvent
import ch.qos.logback.core.spi._
import ch.qos.logback.{core => Logback}
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.ExecutionContext

object PlayLogbackAccess {
  final val defaultConfigFile = new File("conf", "logback-access.xml")
}

final class PlayLogbackAccess(configs : Iterable[URL])
  extends Logback.ContextBase with AppenderAttachable[IAccessEvent] with FilterAttachable[IAccessEvent] {

  private val aai = new AppenderAttachableImpl[IAccessEvent]
  private val fai = new FilterAttachableImpl[IAccessEvent]

  override def start(): Unit = {
    val jc = new JoranConfigurator
    jc.setContext(this)
    configs.foreach(jc.doConfigure)
    super.start()
  }

  override def stop(): Unit = {
    detachAndStopAllAppenders
    super.stop()
  }

  /** Log a completed request.
    * @param requestTime the time at which the request was received
    */
  def log(requestTime : Long = -1, request : RequestHeader, result : Result, user : Option[String] = None): Unit = {
    val ev = logback.PlayAccessEvent(this, requestTime, request, result, user)
    if (getFilterChainDecision(ev) != FilterReply.DENY)
      aai.appendLoopOnAppenders(ev)
  }

  def detachAndStopAllAppenders = aai.detachAndStopAllAppenders
  def getAppender(a : String) = aai.getAppender(a)
  def isAttached(a : Logback.Appender[IAccessEvent]) = aai.isAttached(a)
  def iteratorForAppenders = aai.iteratorForAppenders
  def addAppender(a : Logback.Appender[IAccessEvent]) = aai.addAppender(a)
  def detachAppender(a : Logback.Appender[IAccessEvent]) = aai.detachAppender(a)
  def detachAppender(a : String) = aai.detachAppender(a)

  def clearAllFilters = fai.clearAllFilters
  def getCopyOfAttachedFiltersList = fai.getCopyOfAttachedFiltersList
  def addFilter(f : Logback.filter.Filter[IAccessEvent]) = fai.addFilter(f)
  def getFilterChainDecision(e : IAccessEvent) = fai.getFilterChainDecision(e)
}
