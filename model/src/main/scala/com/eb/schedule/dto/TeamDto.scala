package com.eb.schedule.dto

/**
  * Created by Egor on 13.03.2016.
  */
case class TeamDTO(val id: Int, val name: String, logo:Long, val tag: String = "") {
  var score: Int = 0
  var picks:List[PickDTO] = Nil
  var bans:List[PickDTO] = Nil
}
