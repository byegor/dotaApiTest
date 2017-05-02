organization := "com.eb.dotapulse"

scalaVersion in ThisBuild := "2.12.1"

scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-optimize"
)

resolvers += Resolver.mavenLocal

libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest"       %% "scalatest"                    % "3.0.1"   % "test",
  "org.scalacheck"      %% "scalacheck"                   % "1.13.5"  % "test",
  "org.mockito"         % "mockito-all"                   % "1.9.5"   % "test",
  "com.google.code.gson" % "gson" % "2.6.2",
  "com.google.guava" % "guava" % "19.0",

  "ch.qos.logback" % "logback-classic" % "1.1.3",

  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.8"

)

lazy val root = project.in(file(".")) aggregate(model, restapi, crawler)

lazy val model = project.in(file("model"))

//lazy val rest = project.in(file("rest"))

lazy val crawler = project.in(file("crawler")) dependsOn (model % "test->test;compile->compile")

lazy val restapi = project.in(file("rest-api"))

parallelExecution in ThisBuild := false