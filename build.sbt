organization := "org.databrary"

name := "play-logback-access"

description := "Play framework plugin to generate logback-access events for flexible access logging"

homepage := Some(url("http://github.com/databrary/play-logback-access"))

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

version := "0.5"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.5.0",
  "ch.qos.logback" % "logback-access" % "1.1.4",
  "javax.servlet" % "servlet-api" % "2.5" % Optional
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test
)

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature","-deprecation")

publishMavenStyle := true

publishArtifact in Test := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:databrary/play-logback-access.git</url>
    <connection>scm:git:git@github.com:databrary/play-logback-access.git</connection>
  </scm>
  <developers>
    <developer>
      <id>dylex</id>
      <name>Dylan Simon</name>
    </developer>
  </developers>)
