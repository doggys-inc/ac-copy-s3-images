Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / scalaVersion := "3.5.0-RC3"
ThisBuild / scalacOptions ++= Seq(
  "-Wunused:all",
  "-Wimplausible-patterns",
  "-WunstableInlineAccessors",
  "-Wshadow:all",
  "-Wsafe-init",
  "-Yexplicit-nulls",
  "-Ycheck-all-patmat",
  "-Ycheck-constraint-deps",
  "-deprecation",
  "-language:strictEquality",
  "-Vprofile"
)
ThisBuild / semanticdbEnabled := true
libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "2.0.13" % Runtime
libraryDependencies += "com.outr" %% "scribe" % "3.15.0"
libraryDependencies += "com.outr" %% "scribe-slf4j2" % "3.15.0" % Runtime
libraryDependencies += "io.circe" %% "circe-parser" % "0.14.9"
libraryDependencies += "io.scalaland" %% "chimney" % "1.3.0" % Runtime
libraryDependencies += "org.typelevel" %% "cats-core" % "2.12.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.4"
libraryDependencies += "org.typelevel" %% "munit-cats-effect" % "2.0.0-M5" % Test
libraryDependencies += "software.amazon.awssdk" % "aws-sdk-java" % "2.26.19"
libraryDependencies += "org.typelevel" %% "spire" % "0.18.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j-to-slf4j" % "2.23.1" % Runtime
libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"

Compile / run / fork := true
run / javaOptions ++= Seq(
  "-Xmx8G",
  "-XX:+UseG1GC",
  "-XX:+HeapDumpOnOutOfMemoryError",
  "-Dcats.effect.tracing.mode=full",
  "-Dcats.effect.tracing.buffer.size=1024"
)
outputStrategy := Some(StdoutOutput)
//javaOptions += "-XX:StartFlightRecording=settings=profile"
wartremoverWarnings ++= Seq(Wart.StringPlusAny, Wart.ToString)
