package com.eb.schedule.dto

/**
  * Created by Egor on 26.03.2016.
  */

case class SeriesDTO(gameId: Int, matchId: Long, gameNumber: Byte, var radiantWin:Option[Boolean], var finished:Boolean, radiantTeamId:Int) {
  def this(gameId: Int, matchId: Long, radiantTeam:Int) = this(gameId, matchId, 0, None, false, radiantTeam)
}
