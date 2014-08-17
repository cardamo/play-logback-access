organization := "org.databrary"

name := "play-logback-access"

description := "Play framework plugin to generate logback-access events for flexible access logging"

homepage := Some(url("http://github.com/databrary/play-logback-access"))

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

version := "0.3-SNAPSHOT"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.3.3",
  "ch.qos.logback" % "logback-access" % "1.1.2",
  "javax.servlet" % "servlet-api" % "2.5"
)

scalaVersion := "2.11.2"

crossScalaVersions ++= Seq("2.10.4")

scalacOptions ++= Seq("-feature","-deprecation")

scalaSource in Compile := baseDirectory.value / "src"

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
