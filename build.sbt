organization := "com.milestone"
scalaVersion := "2.11.12"
name := "Milestone-Project"
scalacOptions += "-Ypartial-unification"
testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "sequential")

libraryDependencies ++= {

  val http4sVersion = "0.18.12"
  val circeVersion = "0.9.3"
  val specs2Version = "4.2.0"
  val doobieVersion = "0.5.3"

  Seq(
    "org.specs2"     %% "specs2-core"         % specs2Version % "test",

    "org.http4s"     %% "http4s-blaze-server" % http4sVersion,
    "org.http4s"     %% "http4s-blaze-client" % http4sVersion,
    "org.http4s"     %% "http4s-circe"        % http4sVersion,
    "org.http4s"     %% "http4s-dsl"          % http4sVersion,

    "io.circe"       %% "circe-generic"       % circeVersion,

    "org.tpolecat"   %% "doobie-core"         % doobieVersion,
    "org.tpolecat"   %% "doobie-postgres"     % doobieVersion,

    "ch.qos.logback" %  "logback-classic"     % "1.1.7"
  )
}
