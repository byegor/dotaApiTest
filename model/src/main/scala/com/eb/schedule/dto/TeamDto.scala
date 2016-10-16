package com.eb.schedule.dto

import com.eb.schedule.shared.bean.TeamBean

import scala.collection.JavaConversions._

/**
  * Created by Egor on 26.03.2016.
  */
class TeamDTO(val id: Int) {
  var name: String = ""
  var tag: String = ""
  var logo: Long = -1
  var players: List[PlayerDTO] = Nil
  var netWorth: Int = 0
  var towerStatus: Int = -1
  var barrackStatus: Int = -1
  var picks: List[HeroDTO] = Nil
  var bans: List[HeroDTO] = Nil

  def copy() = {
    val t: TeamDTO = new TeamDTO(id)
    t.name = name
    t.tag = tag
    t.logo = logo
    t
  }


  override def toString = s"TeamDTO($id, $name)"

  def toTeamBean: TeamBean = new TeamBean(id, name, tag, logo)

}
