name := "model"

version := "0.1"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "com.zaxxer" % "HikariCP" % "2.3.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.0",

  "com.typesafe" % "config" % "1.3.0",

  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.1",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.3",

  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "com.mashape.unirest" % "unirest-java" % "1.4.6",

  "com.h2database" % "h2" % "1.4.191"
)
