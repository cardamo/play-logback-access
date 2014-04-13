package play.logback.access

import java.io.File
import java.net.URL
import scala.concurrent.{ExecutionContext,Future}
import play.api.Plugin
import play.api.libs.concurrent
import play.api.mvc.{RequestHeader,SimpleResult,Filter}
import ch.qos.logback.access.joran.JoranConfigurator
import ch.qos.logback.access.spi.IAccessEvent
import ch.qos.logback.{core => logback}
import ch.qos.logback.core.spi._

object PlayLogbackAccess {
  final val defaultConfigFile = new File("conf", "logback-access.xml")
}

final class PlayLogbackAccess(configs : Iterable[URL])(implicit executionContext : ExecutionContext) extends
  logback.ContextBase with AppenderAttachable[IAccessEvent] with FilterAttachable[IAccessEvent] {

  private[this] val aai = new AppenderAttachableImpl[IAccessEvent]
  private[this] val fai = new FilterAttachableImpl[IAccessEvent]

  override def start {
    val jc = new JoranConfigurator
    jc.setContext(this)
    configs.foreach(jc.doConfigure)
    super.start
  }

  override def stop {
    detachAndStopAllAppenders
    super.stop
  }

  /** Log a completed request.
    * @param requestTime the time at which the request was received
    */
  def log(requestTime : Long = -1, request : RequestHeader, result : SimpleResult) {
    val ev = PlayAccessEvent(requestTime, request, result)
    if (getFilterChainDecision(ev) != FilterReply.DENY)
      aai.appendLoopOnAppenders(ev)
  }

  /** A Filter that can be used to automatically log all requests. */
  object filter extends Filter {
    def apply(next : RequestHeader => Future[SimpleResult])(req : RequestHeader) : Future[SimpleResult] = {
      val rt = System.currentTimeMillis
      val res = next(req)
      res.onSuccess { case res : SimpleResult =>
	log(rt, req, res)
      }
      res
    }
  }

  def detachAndStopAllAppenders = aai.detachAndStopAllAppenders
  def getAppender(a : String) = aai.getAppender(a)
  def isAttached(a : logback.Appender[IAccessEvent]) = aai.isAttached(a)
  def iteratorForAppenders = aai.iteratorForAppenders
  def addAppender(a : logback.Appender[IAccessEvent]) = aai.addAppender(a)
  def detachAppender(a : logback.Appender[IAccessEvent]) = aai.detachAppender(a)
  def detachAppender(a : String) = aai.detachAppender(a)

  def clearAllFilters = fai.clearAllFilters
  def getCopyOfAttachedFiltersList = fai.getCopyOfAttachedFiltersList
  def addFilter(f : logback.filter.Filter[IAccessEvent]) = fai.addFilter(f)
  def getFilterChainDecision(e : IAccessEvent) = fai.getFilterChainDecision(e)
}

final class LogbackAccessPlugin(app : play.api.Application) extends Plugin {
  private[this] lazy val executionContext = app.configuration.getString("logbackaccess.context")
    .fold(concurrent.Execution.defaultContext)(
      concurrent.Akka.system(app).dispatchers.lookup)
  private[this] lazy val configs =
    app.configuration.getString("logbackaccess.config.file").map(new File(_).toURI.toURL) ++
    app.configuration.getString("logbackaccess.config.resource").flatMap(app.resource(_)) ++
    app.configuration.getString("logbackaccess.config.url").map(new URL(_))

  lazy val api = new PlayLogbackAccess(configs)(executionContext)

  override def onStart = api.start
  override def onStop = api.stop
}
