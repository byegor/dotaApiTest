name := "model"

version := "0.1"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "com.typesafe" % "config" % "1.3.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1" exclude("com.zaxxer", "HikariCP-java6"),

  "mysql" % "mysql-connector-java" % "5.1.36",
  "com.zaxxer" % "HikariCP" % "2.4.5",
  "com.h2database" % "h2" % "1.4.191",

  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "com.mashape.unirest" % "unirest-java" % "1.4.6"
)
