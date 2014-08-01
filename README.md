# Logback-access plugin for Play 2

This plugin allows you to use [Logback](http://logback.qos.ch)'s very flexible [access](http://logback.qos.ch/access.html) framework within [Play](http://www.playframework.com).

## Installation

To use it, add the dependency:

> "play.logback.access" %% "play-logback-access" % "0.2"

(You may need to download and `sbt publish-local` it first, for now.)

## Integration

Add `play.logback.access.LogbackAccessPlugin` to your `conf/play.plugins`, and load the plugin somewhere with:

```scala
val accessLogger = play.api.Play.current.plugin[play.logback.access.LogbackAccessPlugin].map(_.api)
```

## Configuration

Add a configuration file to your `conf/application.conf` with something like:

> logbackaccess.config.resource=logback-access.xml

`conf/logback-access.xml`:

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

## Use

Finally, either use `accessLogger.filter` in your [GlobalSettings](http://www.playframework.com/documentation/2.2.x/ScalaHttpFilters) object or selectively call `accessLogger.log`.

