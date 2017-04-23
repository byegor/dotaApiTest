name := "rest"

version := "0.2"

lazy val scalatraVersion = "2.3.1"

libraryDependencies ++= Seq(
  "org.scalatra"      %% "scalatra"                       % scalatraVersion,
  "org.scalatra"      %% "scalatra-scalate"               % scalatraVersion,
  "org.scalatra"      %% "scalatra-specs2"                % scalatraVersion       % "test",
  "ch.qos.logback"    %  "logback-classic"                % "1.1.3"               % "runtime",
  "org.eclipse.jetty" %  "jetty-webapp"                   % "9.1.0.v20131115"     % "container;compile",
  "javax.servlet"     %  "javax.servlet-api"              % "3.1.0"               % "provided",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.3"
)

//jetty()

javaOptions ++= Seq(
  "-Xdebug",
  "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
)

mainClass in assembly := Some("com.eb.schedule.rest.JettyRunner")

unmanagedResourceDirectories in Compile += { baseDirectory.value / "src/main/webapp" }