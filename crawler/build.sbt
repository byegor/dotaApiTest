name := "crawler"

version := "0.1"

libraryDependencies ++= Seq(
  "commons-dbutils" % "commons-dbutils" % "1.6",

  "org.json" % "json" % "20140107",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.1",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.3",

  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "com.mashape.unirest" % "unirest-java" % "1.4.6",
  "org.jsoup" % "jsoup" % "1.8.3"
)


assemblyJarName in assembly := "crawler.jar"

//mainClass in assembly := Some("egor.dota.harvester.HarvesterStarter")