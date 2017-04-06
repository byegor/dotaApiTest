package com.eb.schedule.dto

import java.sql.Timestamp

/**
  * Created by Egor on 26.03.2016.
  */

case class SeriesDTO(gameId: Int, matchId: Long, gameNumber: Byte, var radiantWin:Option[Boolean], var finished:Boolean, radiantTeamId:Int, startDate:Timestamp = new Timestamp(System.currentTimeMillis()))
