
scalaVersion := "2.11.5"

javaOptions in run ++= List(
  "-Xmx1G",
  "-Xdebug",
  "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9999"
)

crossScalaVersions in ThisBuild := Seq("2.10.4", "2.11.5")

scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-optimize"
)

lazy val root = Project("dota-stats", file(".")) aggregate(model, harvester)

lazy val model = project.in(file("model"))

lazy val harvester = Project("harvester", file("harvester")) dependsOn (model)