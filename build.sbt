name := "Task"
version := "0.1.0"
scalaVersion := "2.13.10"

libraryDependencies += "com.typesafe" % "config" % "1.4.2"
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.17.2"
libraryDependencies += "co.fs2" %% "fs2-io" % "3.6.1"
libraryDependencies += "co.fs2" %% "fs2-core" % "3.6.1"
libraryDependencies += "com.github.fd4s" %% "fs2-kafka" % "2.5.0"
libraryDependencies += "org.gnieh" %% "fs2-data-csv" % "1.7.0"
libraryDependencies += "org.gnieh" %% "fs2-data-csv-generic" % "1.7.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15"

parallelExecution in Test := false
