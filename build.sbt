resolvers += Resolver.jcenterRepo

organization := "org.databrary"

bintrayRepository := "com.cardamo.play-logback-access"

name := "play-logback-access"

description := "Play framework plugin to generate logback-access events for flexible access logging"

homepage := Some(url("http://github.com/cardamo/play-logback-access"))

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

version := "0.5.2"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.5.14",
  "ch.qos.logback" % "logback-access" % "1.2.3",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % Optional
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test
)

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature","-deprecation")

publishMavenStyle := true

publishArtifact in Test := false

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
