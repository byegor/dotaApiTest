package com.eb.dotapulse.bot.data

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by Egor on 14.04.2017.
  */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val itemFormat = jsonFormat3(Data)

}
