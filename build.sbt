name := "Task"
version := "0.1.0"
scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.2",
  "com.github.pureconfig" %% "pureconfig" % "0.17.2",
  "co.fs2" %% "fs2-io" % "3.6.1",
  "co.fs2" %% "fs2-core" % "3.6.1",
  "com.github.fd4s" %% "fs2-kafka" % "2.5.0",
  "org.gnieh" %% "fs2-data-csv" % "1.7.0",
  "org.gnieh" %% "fs2-data-csv-generic" % "1.7.0",
  "org.http4s" %% "http4s-ember-server" % "0.23.18",
  "org.http4s" %% "http4s-dsl" % "0.23.18",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.2.9",
  "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-docs" % "1.2.12",
  "com.softwaremill.sttp.apispec" %% "asyncapi-circe-yaml" % "0.3.2",
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % "1.2.12",
  "org.scalatest" %% "scalatest" % "3.2.15"
)

parallelExecution in Test := false
