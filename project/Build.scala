import sbt._
import Keys._

object ApplicationBuild extends Build {

  val appName         = "starterkit"
  val appVersion      = "0.1-SNAPSHOT"
  val appScalaVersion = "2.10.2"
  val appScalaBinaryVersion = "2.10"
  val appScalaCrossVersions = Seq("2.10.2")

  lazy val baseSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := appScalaVersion,
    scalaBinaryVersion := appScalaBinaryVersion,
    crossScalaVersions := appScalaCrossVersions,
    parallelExecution in Test := false
  )

  lazy val root = Project("root", base = file("."))
    .settings(baseSettings: _*)
    .settings(
      publishLocal := {},
      publish := {}
    ).aggregate(
      StarterKit
    )

  lazy val StarterKit = Project(appName, base = file("starterkit"))
    .settings(baseSettings: _*)
    .settings(
      resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      resolvers += "ReactiveCouchbase" at "https://raw.github.com/ReactiveCouchbase/repository/master/snapshots",
      libraryDependencies += "org.reactivecouchbase" %% "reactivecouchbase-core" % "0.1-SNAPSHOT",
      libraryDependencies += "org.reactivecouchbase" %% "reactivecouchbase-es" % "0.1-SNAPSHOT",
      libraryDependencies += "org.specs2" %% "specs2" % "2.2.1" % "test",
      organization := "foo.bar",
      version := appVersion,
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false }
    )
}
