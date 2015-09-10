import sbt._

object Deps {

  object V {

    val scalaTest = "2.2.4"
    val scalaCheck = "1.12.2"

    val httpClient = "4.5"
    val unirest = "1.4.6"
    val json = "20141113"

    val mysql = "5.1.36"
  }

  val scalaTest      = "org.scalatest"                 %% "scalatest"             % V.scalaTest      % "test"
  val scalaCheck     = "org.scalacheck"                %% "scalacheck"            % V.scalaCheck     % "test"

  val httpClient     = "org.apache.httpcomponents"     %% "httpclient"            % V.httpClient
  val unirest        = "com.mashape.unirest"           %% "unirest-java"          % V.unirest
  val json           = "org.json"                      %% "json"                  % V.json

  val mysql          = "mysql"                         %% "mysql-connector-java"  % V.mysql
}

