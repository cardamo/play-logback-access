# Logback-access plugin for Play 2

This plugin allows you to use [Logback](http://logback.qos.ch)'s very flexible [access](http://logback.qos.ch/access.html) framework within [Play](http://www.playframework.com).

## Distribution

Binaries starting from version 0.5.2 are published to jcenter.

    resolvers += Resolver.jcenterRepo

## Installation

Add the following dependency for Play 2.6.x:

    libraryDependencies += "org.databrary" %% "play-logback-access" % "0.6.0"

For older versions of Play use:

| play version | lib version |
|--------------|-------------|
| 2.5.14+      | 0.5.2       |
| 2.5.0+       | 0.5.1       |
| 2.4.x        | 0.4         |
| 2.3.x        | 0.3         |
| 2.2.x        | 0.1         |

## Configuration

Add a configuration file to your `conf/application.conf` with something like:

    logbackaccess.config.resource=logback-access.xml
    play.modules.enabled += "org.databrary.PlayLogbackAccessModule"

Then in `conf/logback-access.xml`:

```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%t %D %s %h %b %A "%r" "%i{Referer}" "%i{User-Agent}"</pattern>
    </encoder>
  </appender>
  <appender-ref ref="STDOUT" />
</configuration>
```

(You can alternatively use `logbackaccess.config.file` or `logbackaccess.config.url`.)

You can read more about the supported [patterns](http://logback.qos.ch/manual/layouts.html#logback-access).
Note that patterns requiring access to the full request body or response body (including content-length) do not currently provide useful data.

There is also a `logbackaccess.context` setting if you want it to use an [execution context other than the default one](http://www.playframework.com/documentation/2.2.x/ThreadPools).


## Usage

The library is a [Play Module](https://www.playframework.com/documentation/2.5.x/Modules) which
you need to enable by adding this to application's `.conf` file:

    play.modules.enabled += "org.databrary.PlayLogbackAccessModule"

(if you experience any issues with dependency injection (esp. during tests), please try using `PlayLogbackAccessLazyInjectModule` which does the lazy dependency injection)

Inject `PlayLogbackAccessApi` into any class to gain access to the API. This exposes:
- `log(requestTime: Long, request: RequestHeader, result: Result, user: Option[String])` - Manually log to the Access logger

Inject `PlayLogbackAccessFilter` to access a Filter (in 0.4 and earlier this was available as `filter` on the above)

### Example: Filter

#### 0.6:
In file `conf/application.conf`

    play.filters.enabled += org.databrary.PlayLogbackAccessFilter

#### 0.5:

In file: `app/Filters.scala`
```scala
import javax.inject.Inject
import org.databrary.PlayLogbackAccessFilter
import play.api.http.HttpFilters

class Filters @Inject() (accessLogger: PlayLogbackAccessFilter) extends HttpFilters {
  val filters = Seq(accessLogger)
}
```

#### Before 0.5:

In file: `app/Filters.scala`
```scala
import javax.inject.Inject
import org.databrary.PlayLogbackAccessApi
import play.api.http.HttpFilters

class Filters @Inject() (accessLogger: PlayLogbackAccessApi) extends HttpFilters {
  val filters = Seq(accessLogger.filter)
}
```

Then, in file: `conf/application.conf`
```
play.http.filters=Filters
```
