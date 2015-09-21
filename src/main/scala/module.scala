package org.databrary

import java.io.File
import java.net.URL
import javax.inject.Inject
import play.api.inject.{ApplicationLifecycle, Binding, Module}
import play.api.libs.concurrent
import play.api.mvc.{Filter, Result, RequestHeader}
import play.api.{Configuration, Environment}
import scala.concurrent.Future

trait PlayLogbackAccessApi {
  val context: PlayLogbackAccess
  def log(requestTime : Long = -1, request : RequestHeader, result : Result, user : Option[String] = None)
  val filter: Filter
}

class PlayLogbackAccessApiImpl @Inject() (app: play.api.Application, lifecycle: ApplicationLifecycle) extends PlayLogbackAccessApi {

  private[this] lazy val executionContext = app.configuration.getString("logbackaccess.context")
    .fold(concurrent.Execution.defaultContext)(
      concurrent.Akka.system(app).dispatchers.lookup)

  private[this] lazy val configs =
    app.configuration.getString("logbackaccess.config.file").map(new File(_).toURI.toURL) ++
      app.configuration.getString("logbackaccess.config.resource").flatMap(app.resource(_)) ++
      app.configuration.getString("logbackaccess.config.url").map(new URL(_))

  lazy val context = new PlayLogbackAccess(configs)(executionContext)

  override def log(requestTime: Long, request: RequestHeader, result: Result, user: Option[String]): Unit =
    context.log _

  override val filter: Filter =
    context.filter


  context.start()

  lifecycle.addStopHook { () =>
    Future.successful(context.stop())
  }
}

class PlayLogbackAccessModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[PlayLogbackAccessApi].to[PlayLogbackAccessApiImpl].eagerly()
  )
}
