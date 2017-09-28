name := "crawler"

version := "0.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka"               %% "akka-actor"                % "2.4.17",
  "com.typesafe.akka"               %% "akka-http"                 % "10.0.5",
  "com.typesafe.akka"               %% "akka-http-spray-json"      % "10.0.5",

  "com.typesafe.akka"               %% "akka-slf4j"                 % "2.4.17",
  "commons-dbutils"                 % "commons-dbutils"             % "1.6",
  "org.apache.httpcomponents"       % "httpclient"                  % "4.5",
  "com.mashape.unirest"             % "unirest-java"                % "1.4.6",
  "com.fasterxml.jackson.module"    %% "jackson-module-scala"       % "2.8.8",
  "com.fasterxml.jackson.core"      % "jackson-databind"            % "2.8.8",

  "org.jsoup"                       % "jsoup"                       % "1.8.3",

  "com.eb.schedule"                 % "shared"                      % "0.2",

  "com.typesafe.akka"               %% "akka-http-testkit"          % "10.0.5"  % "test"

)


mainClass in assembly := Some("com.eb.pulse.crawler.CrawlerStarter")

assemblyMergeStrategy in assembly := {
  case PathList("application.conf", xs @ _*) => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}