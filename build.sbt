organization := "org.databrary"

name := "play-logback-access"

description := "Play framework plugin to generate logback-access events for flexible access logging"

homepage := Some(url("http://github.com/cardamo/play-logback-access"))

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

version := "0.9.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.playframework" %% "play" % "3.0.1",
  "ch.qos.logback" % "logback-access" % "1.4.14",
  "jakarta.servlet" % "jakarta.servlet-api" % "6.0.0" % Optional,
  //https://github.com/playframework/playframework/releases/tag/2.8.15
  "com.google.inject"            % "guice"                % "6.0.0",
  "com.google.inject.extensions" % "guice-assistedinject" % "6.0.0"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
  "org.playframework" %% "play-pekko-http-server" % "3.0.2" % Test
)

scalaVersion := "2.13.13"
crossScalaVersions := Seq("3.4.1", scalaVersion.value)

scalacOptions ++= Seq("-feature","-deprecation")

publishMavenStyle := true

Test / publishArtifact  := false
Test / javaOptions ++= Seq(
  "--add-exports=java.base/sun.security.x509=ALL-UNNAMED",
  "--add-opens=java.base/sun.security.ssl=ALL-UNNAMED"
)
// Test / fork := true // This is the default anyway, just a reminder in case you changed it to false before

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:cardamo/play-logback-access.git</url>
    <connection>scm:git:git@github.com:cardamo/play-logback-access.git</connection>
  </scm>
  <developers>
    <developer>
      <id>dylex</id>
      <name>Dylan Simon</name>
    </developer>
    <developer>
      <id>cardamo</id>
      <name>Artem Sinicyn</name>
    </developer>
  </developers>)
