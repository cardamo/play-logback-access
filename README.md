# Logback-access plugin for Play 2

This plugin allows you to use [Logback](http://logback.qos.ch)'s very flexible [access](http://logback.qos.ch/access.html) framework within [Play](http://www.playframework.com).

## Installation

Add the following dependency for Play 2.4.x:

    libraryDependencies += "org.databrary" %% "play-logback-access" % "1.0-SNAPSHOT"


### Play 2.3.x

For Play 2.3.x on Scala 2.11, use version 0.2:

    libraryDependencies += "org.databrary" %% "play-logback-access" % "0.2"

[Usage info](https://github.com/databrary/play-logback-access/releases/tag/0.2)

### Play 2.2.x
For Play 2.2.x on Scala 2.11, use version 0.1:

    libraryDependencies += "org.databrary" %% "play-logback-access" % "0.1"


## Configuration

Add a configuration file to your `conf/application.conf` with something like:

    logbackaccess.config.resource=logback-access.xml

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

The library will automatically initialize itself as a [Play Module](https://www.playframework.com/documentation/2.4.x/Modules).

Inject `PlayLogbackAccessApi` into any class to gain access to the API. This exposes:
- `filter` - [Play Filter](https://www.playframework.com/documentation/2.4.x/ScalaHttpFilters) to log requests automatically
- `log(requestTime: Long, request: RequestHeader, result: Result, user: Option[String])` - Manually log to the Access logger

### Example: Filter

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
