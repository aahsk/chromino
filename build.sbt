import Dependencies._

ThisBuild / organization := "com.aahsk.chromino"
ThisBuild / scalaVersion := "2.13.4"

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(scalaTest),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-Ymacro-annotations"
  )
)

lazy val domain = (project in file("domain"))
  .settings(commonSettings: _*)
  .settings(
    name := "domain"
  )

lazy val logic = (project in file("logic"))
  .settings(commonSettings: _*)
  .settings(
    name := "logic"
  )

lazy val boot = (project in file("boot"))
  .dependsOn(domain)
  .dependsOn(http)
  .settings(commonSettings: _*)
  .settings(
    name := "boot"
  )

lazy val http = (project in file("http"))
  .dependsOn(domain)
  .dependsOn(logic)
  .settings(commonSettings: _*)
  .settings(
    name := "http",
    libraryDependencies ++= Seq(
      Log.logBack,
      Cats.core,
      Cats.effect,
      Http4s.dsl,
      Http4s.blazeServer
    )
  )

lazy val protocol = (project in file("protocol"))
  .dependsOn(domain)
  .settings(commonSettings: _*)
  .settings(
    name := "protocol"
  )

lazy val persistance = (project in file("persistance"))
  .dependsOn(domain)
  .settings(commonSettings: _*)
  .settings(
    name := "persistance"
  )
