package com.eb.schedule.dto

/**
  * Created by Egor on 26.03.2016.
  */
class TeamDTO(val id: Int) {
  var name: String = ""
  var tag: String = ""
  var logo:Long = -1
  var players: List[PlayerDTO] = Nil
  var netWorth: Int = 0

  def copy() = {
    val t: TeamDTO = new TeamDTO(id)
    t.name = name
    t.tag = tag
    t.logo = logo
    t
  }
}
