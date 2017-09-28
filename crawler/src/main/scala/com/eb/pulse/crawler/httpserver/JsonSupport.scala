package com.eb.pulse.crawler.httpserver

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.eb.pulse.crawler.data.model.Data
import com.eb.schedule.shared.bean.{GameBean, Match}
import com.fasterxml.jackson.databind.ObjectMapper
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

/**
  * Created by Egor on 14.04.2017.
  */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val dataFormat = jsonFormat3(Data)

  implicit object GameBeanJsonFormat extends JsonFormat[GameBean] {
    val mapper: ObjectMapper = new ObjectMapper()

    override def write(obj: GameBean): JsValue =
      JsString(mapper.writeValueAsString(obj))

    override def read(json: JsValue): GameBean = new GameBean()
  }

  implicit object MatchBeanJsonFormat extends JsonFormat[Match] {
    val mapper: ObjectMapper = new ObjectMapper()

    override def write(obj: Match): JsValue =
      JsString(mapper.writeValueAsString(obj))

    override def read(json: JsValue): Match = new Match()
  }

}
