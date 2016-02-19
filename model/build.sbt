name := "model"

version := "0.1"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "com.zaxxer" % "HikariCP" % "2.3.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.0",

  "com.typesafe" % "config" % "1.3.0"
)
