name := "crawler"

version := "0.3"

javaOptions in Test += "-Dconfig.file=application-test.conf"

libraryDependencies ++= Seq(
  "commons-dbutils" % "commons-dbutils" % "1.6",

  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "com.mashape.unirest" % "unirest-java" % "1.4.6",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.3",
  "org.jsoup" % "jsoup" % "1.8.3",

  "com.eb.schedule"             % "shared"            % "0.3"

)


mainClass in assembly := Some("com.eb.schedule.CrawlerStarter")