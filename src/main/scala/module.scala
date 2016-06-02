package org.databrary

import java.io.File
import java.net.URL
import javax.inject.{Inject, Provider, Singleton}

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.inject.{ApplicationLifecycle, Binding, Module}
import play.api.libs.concurrent
import play.api.mvc.{Filter, RequestHeader, Result}
import play.api.{Application, Configuration, Environment}

import scala.concurrent.Future

trait PlayLogbackAccessApi {
  val context: PlayLogbackAccess
  def log(requestTime : Long = -1, request : RequestHeader, result : Result, user : Option[String] = None)
}

trait PlayLogbackAccessFilter extends Filter

/** A Filter that can be used to automatically log all requests. */
@Singleton
class PlayLogbackAccessFilterImpl @Inject()(
    val mat: Materializer,
    configuration: Configuration,
    actorSystem: ActorSystem,
    apiProvider: Provider[PlayLogbackAccessApi]) extends PlayLogbackAccessFilter {

  private[this] implicit lazy val executionContext = configuration.getString("logbackaccess.context")
    .fold(concurrent.Execution.defaultContext)(
      actorSystem.dispatchers.lookup)

  private lazy val provider = apiProvider.get

  def apply(next : RequestHeader => Future[Result])(req : RequestHeader) : Future[Result] = {
    val rt = System.currentTimeMillis
    val res = next(req)
    res.onSuccess { case res : Result =>
      provider.log(rt, req, res)
    }
    res
  }
}

class PlayLogbackAccessApiImpl @Inject() (app: play.api.Application, lifecycle: ApplicationLifecycle) extends PlayLogbackAccessApi {

  private[this] lazy val configs =
    app.configuration.getString("logbackaccess.config.file").map(new File(_).toURI.toURL) ++
      app.configuration.getString("logbackaccess.config.resource").flatMap(app.resource) ++
      app.configuration.getString("logbackaccess.config.url").map(new URL(_))

  lazy val context = new PlayLogbackAccess(configs)

  override def log(requestTime: Long, request: RequestHeader, result: Result, user: Option[String]): Unit =
    context.log _

  context.start()

  lifecycle.addStopHook { () =>
    Future.successful(context.stop())
  }
}

class PlayLogbackAccessModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[PlayLogbackAccessApi].to[PlayLogbackAccessApiImpl].eagerly(),
    bind[PlayLogbackAccessFilter].to[PlayLogbackAccessFilterImpl]
  )
}
