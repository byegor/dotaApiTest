
scalaVersion in ThisBuild := "2.11.7"

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
  "org.mockito" % "mockito-all" % "1.9.5"  % "test",
  "mysql" % "mysql-connector-java" % "5.1.36",
  "com.google.inject" % "guice" % "4.0",

 "ch.qos.logback" % "logback-core" % "1.1.3",
 "ch.qos.logback" % "logback-classic" % "1.1.3"
)

lazy val root = project.in(file(".")) aggregate(model, ui, crawler)

lazy val model = project.in(file("model"))

lazy val harvester = project.in(file("harvester")) dependsOn model

lazy val ui = project.in(file("ui"))

lazy val crawler = project.in(file("crawler"))dependsOn(model)
