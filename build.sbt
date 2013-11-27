name := "scala-ide-dashboard"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)     

play.Project.playScalaSettings
