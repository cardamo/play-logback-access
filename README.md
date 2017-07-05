# Logback-access plugin for Play 2

This plugin allows you to use [Logback](http://logback.qos.ch)'s very flexible [access](http://logback.qos.ch/access.html) framework within [Play](http://www.playframework.com).

## Installation

Add the following dependency for Play 2.6.x:

    libraryDependencies += "org.databrary" %% "play-logback-access" % "0.6"

### Play 2.5.x

Add the following dependency for Play 2.4.x:

    libraryDependencies += "org.databrary" %% "play-logback-access" % "0.5.1"

### Play 2.4.x

Add the following dependency for Play 2.4.x:

    libraryDependencies += "org.databrary" %% "play-logback-access" % "0.4"

### Play 2.3.x

For Play 2.3.x on Scala 2.11, use version 0.3:

    libraryDependencies += "org.databrary" %% "play-logback-access" % "0.3"

[Usage info](https://github.com/databrary/play-logback-access/releases/tag/0.3)

### Play 2.2.x

For Play 2.2.x on Scala 2.10, use version 0.1:

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
- `log(requestTime: Long, request: RequestHeader, result: Result, user: Option[String])` - Manually log to the Access logger

Inject `PlayLogbackAccessFilter` to access a Filter (in 0.4 and earlier this was available as `filter` on the above)

### Example: Filter

#### 0.5 and later:

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
