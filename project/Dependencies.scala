import sbt._

//  Author's note:
//  I would love for the circe & test dependencies to also live here, but I couldn't fix this error
//  [error] /home/kshaa/Code/scala/chromino/project/Dependencies.scala:17:37: `value` can only be used within a task or setting macro, such as :=, +=, ++=, Def.task, or Def.setting.
//  [error]     def enumeratum = "com.beachape" %%% "enumeratum-circe" % "1.6.1"
object Dependencies {
  val scalaTestVersion = "3.2.3"
//  val scalaTest        = "org.scalatest" %%% "scalatest" % scalaTestVersion % Test

  val kindProjectorVersion = "0.11.2"
  val kindProjector =
    "org.typelevel" %% "kind-projector" % kindProjectorVersion cross CrossVersion.full

  object Circe {
    val circeVersion = "0.13.0"
//    val core          = "io.circe" %%% "circe-core"           % circeVersion
//    val generic       = "io.circe" %%% "circe-generic"        % circeVersion
//    val genericExtras = "io.circe" %%% "circe-generic-extras" % circeVersion
//    val parser        = "io.circe" %%% "circe-parser"         % circeVersion

    val enumeratumVersion = "1.6.1"
//    val enumeratum        = "com.beachape" %%% "enumeratum-circe" % enumeratumVersion
  }

  object Cats {
    val coreVersion = "2.3.0"
    val core        = "org.typelevel" %% "cats-core" % coreVersion

    val effectVersion = "2.3.0"
    val effect        = "org.typelevel" %% "cats-effect" % effectVersion
  }

  object Log {
    val logBackVersion = "1.2.3"
    val logBack        = "ch.qos.logback" % "logback-classic" % logBackVersion

    val slf4jVersion = "1.7.30"
    val slf4j        = "org.slf4j" % "slf4j-nop" % slf4jVersion
  }

  object FS2 {
    val coreVersion = "2.4.6"
    val core        = "co.fs2" %% "fs2-core" % coreVersion
  }

  object Http4s {
    val http4sVersion = "0.21.13"
    val dsl           = "org.http4s" %% "http4s-dsl"          % http4sVersion
    val blazeServer   = "org.http4s" %% "http4s-blaze-server" % http4sVersion
    val circe         = "org.http4s" %% "http4s-circe"        % http4sVersion
  }
}
