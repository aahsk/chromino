import Dependencies._

ThisBuild / organization := "com.aahsk.chromino"
ThisBuild / scalaVersion := "2.13.4"

lazy val commonSettings = Seq(
 scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
  libraryDependencies ++= Seq(
    "org.scalatest" %%% "scalatest" % scalaTestVersion % "test"
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-Ymacro-annotations",
    "-Ywarn-unused"
  )
)

lazy val root = (project in file("."))
  .aggregate(boot, domain, protocol)

lazy val domain = (project in file("domain"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "domain",
    libraryDependencies ++= Seq(
      "com.beachape" %%% "enumeratum-circe" % Circe.enumeratumVersion
    )
  )

lazy val logic = (project in file("logic"))
  .dependsOn(domain)
  .dependsOn(protocol)
  .settings(commonSettings: _*)
  .settings(
    name := "logic",
    libraryDependencies ++= Seq(
      Cats.core,
      Cats.effect,
      FS2.core
    )
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
  .dependsOn(protocol)
  .settings(commonSettings: _*)
  .settings(
    addCompilerPlugin(kindProjector),
    name := "http",
    libraryDependencies ++= Seq(
      Log.logBack,
      Cats.core,
      Cats.effect,
      Http4s.dsl,
      Http4s.blazeServer,
      "io.circe" %%% "circe-core"           % Circe.circeVersion,
      "io.circe" %%% "circe-generic"        % Circe.circeVersion,
      "io.circe" %%% "circe-generic-extras" % Circe.circeVersion,
      "io.circe" %%% "circe-parser"         % Circe.circeVersion
    )
  )

lazy val protocol = (project in file("protocol"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(domain)
  .settings(commonSettings: _*)
  .settings(
    name := "protocol",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core"           % Circe.circeVersion,
      "io.circe" %%% "circe-generic"        % Circe.circeVersion,
      "io.circe" %%% "circe-generic-extras" % Circe.circeVersion,
      "io.circe" %%% "circe-parser"         % Circe.circeVersion
    )
  )
