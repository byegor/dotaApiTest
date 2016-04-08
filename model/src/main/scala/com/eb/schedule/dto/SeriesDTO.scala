package com.eb.schedule.dto

import com.eb.schedule.model.MatchStatus

/**
  * Created by Egor on 26.03.2016.
  */

class SeriesDTO(val gameId: Int, val matchId: Long, val gameNumber: Byte) {
  def this(gameId: Int, matchId: Long) = this(gameId, matchId, 0)
}
