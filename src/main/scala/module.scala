package org.databrary

import java.io.File
import java.net.URL

import javax.inject.{Inject, Provider, Singleton}
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.inject.{ApplicationLifecycle, Binding, Module}
import play.api.mvc.{Filter, RequestHeader, Result}
import play.api.{Configuration, Environment}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

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
    defaultExecutionContext: ExecutionContext,
    apiProvider: Provider[PlayLogbackAccessApi]) extends PlayLogbackAccessFilter {

  private[this] implicit lazy val executionContext: ExecutionContext = configuration
    .getOptional[String]("logbackaccess.context")
    .fold(defaultExecutionContext)(actorSystem.dispatchers.lookup)

  private lazy val api = apiProvider.get

  def apply(next : RequestHeader => Future[Result])(req : RequestHeader) : Future[Result] = {
    val rt = System.currentTimeMillis
    val resp = next(req)
    resp.onComplete {
      case Success(res) => api.log(rt, req, res)
    }
    resp
  }
}

class PlayLogbackAccessApiImpl @Inject() (app: play.api.Application, lifecycle: ApplicationLifecycle) extends PlayLogbackAccessApi {

  private[this] lazy val configs =
    app.configuration.getOptional[String]("logbackaccess.config.file").map(new File(_).toURI.toURL) ++
      app.configuration.getOptional[String]("logbackaccess.config.resource").flatMap(app.environment.resource) ++
      app.configuration.getOptional[String]("logbackaccess.config.url").map(new URL(_))

  lazy val context = new PlayLogbackAccess(configs)

  override def log(requestTime: Long, request: RequestHeader, result: Result, user: Option[String]): Unit =
    context.log(requestTime, request, result, user)

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

class PlayLogbackAccessLazyInjectModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[PlayLogbackAccessApi].to[PlayLogbackAccessApiImpl],
    bind[PlayLogbackAccessFilter].to[PlayLogbackAccessFilterImpl]
  )
}
