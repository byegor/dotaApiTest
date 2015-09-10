name := "model"

version := "0.1"

libraryDependencies ++= {
  import Deps._
  Seq(
    scalaTest,
    scalaCheck
  )
}