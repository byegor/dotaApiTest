name := "crawler"

version := "0.1"

libraryDependencies ++= Seq(
  "commons-dbutils" % "commons-dbutils" % "1.6",

  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "com.mashape.unirest" % "unirest-java" % "1.4.6",
  "org.jsoup" % "jsoup" % "1.8.3"
)


mainClass in assembly := Some("com.eb.schedule.CrawlerStarter")