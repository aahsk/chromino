import sbt._

object Dependencies {
  val scalaTest =
    "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2" % Test

  object Circe {
    private val circeVersion = "0.13.0"
    val core = "io.circe" %% "circe-core" % circeVersion
    val generic = "io.circe" %% "circe-generic" % circeVersion
    val genericExtras = "io.circe" %% "circe-generic-extras" % circeVersion
    val optics = "io.circe" %% "circe-optics" % circeVersion
    val parser = "io.circe" %% "circe-parser" % circeVersion
  }

  object Cats {
    private val catsVersion = "2.2.0"
    val core = "org.typelevel" %% "cats-core" % catsVersion
    val effect = "org.typelevel" %% "cats-effect" % catsVersion

    private val taglessVersion = "0.11"
    val taglessCore = "org.typelevel" %% "cats-tagless-core" % taglessVersion
    val taglessMacros =
      "org.typelevel" %% "cats-tagless-macros" % taglessVersion
  }

  object Log {
    val logBack = "ch.qos.logback" % "logback-classic" % "1.2.3"
    val slf4j = "org.slf4j" % "slf4j-nop" % "1.6.4"
    val catsCore = "io.chrisdavenport" %% "log4cats-core" % "1.1.1"
    val catsSlf4j = "io.chrisdavenport" %% "log4cats-slf4j" % "1.1.1"
  }

  object Http4s {
    private val http4sVersion = "0.21.7"
    val dsl = "org.http4s" %% "http4s-dsl" % http4sVersion
    val blazeServer = "org.http4s" %% "http4s-blaze-server" % http4sVersion
    val circe = "org.http4s" %% "http4s-circe" % http4sVersion
  }
}
