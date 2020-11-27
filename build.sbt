import Dependencies._

ThisBuild / organization := "com.aahsk.chromino"
ThisBuild / scalaVersion := "2.12.10"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"

lazy val domain = (project in file("domain"))
  .settings(
    name := "domain",
    libraryDependencies ++= Seq(scalaTest)
  )

lazy val logic = (project in file("logic"))
  .settings(
    name := "logic"
  )

lazy val boot = (project in file("boot"))
  .dependsOn(domain)
  .dependsOn(http)
  .settings(
    name := "boot"
  )

lazy val http = (project in file("http"))
  .dependsOn(domain)
  .dependsOn(logic)
  .settings(
    name := "http"
  )

lazy val protocol = (project in file("protocol"))
  .dependsOn(domain)
  .settings(
    name := "protocol"
  )

lazy val persistance = (project in file("persistance"))
  .dependsOn(domain)
  .settings(
    name := "persistance"
  )
