
scalaVersion in ThisBuild := "2.11.6"

crossScalaVersions in ThisBuild := Seq("2.10.4", "2.11.5")

scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-optimize"
)

libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
  "mysql" % "mysql-connector-java" % "5.1.36"

)

lazy val root = project.in(file(".")) aggregate(model, ui, harvester)

lazy val model = project.in(file("model"))

lazy val harvester = project.in(file("harvester")) dependsOn model

lazy val ui = project.in(file("ui"))