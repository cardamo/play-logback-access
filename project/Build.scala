import sbt._
import sbt.Keys._

object PluginBuild extends Build {
  val playLogbackAccess = Project("play-logback-access", file("src")).settings(
    organization := "play.logback.access",
    description := "Play framework plugin to generate logback-access events for flexible access logging",
    version := "0.1",
    scalaVersion := "2.10.4",
    scalacOptions := Seq("-feature","-deprecation"),
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % "2.2.2",
      "ch.qos.logback" % "logback-access" % "1.1.2",
      "javax.servlet" % "servlet-api" % "2.5"
    )
  )
}
