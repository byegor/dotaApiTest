name := "harvester"

version := "0.1"


libraryDependencies ++= {
  import Deps._
  Seq(
    json,
    httpClient,
    unirest,
    mysql

  )
}