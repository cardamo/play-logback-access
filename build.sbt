organization := "play.logback.access"

description := "Play framework plugin to generate logback-access events for flexible access logging"

version := "0.1"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.2.2",
  "ch.qos.logback" % "logback-access" % "1.1.2",
  "javax.servlet" % "servlet-api" % "2.5"
)

scalaVersion := "2.10.4"

scalacOptions := Seq("-feature","-deprecation")

publishTo := Some(Resolver.file("play", new File("/usr/local/src/play/repository/local/")))
