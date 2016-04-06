package com.eb.schedule.dto

/**
  * Created by Egor on 13.03.2016.
  */
case class NetWorthDTO(matchId: Long, var netWorth: List[Int]) {
  def this(matchId: Long) = this(matchId, Nil)


}
