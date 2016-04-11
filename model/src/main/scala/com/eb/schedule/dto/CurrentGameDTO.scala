package com.eb.schedule.dto

/**
  * Created by Egor on 13.03.2016.
  */
class CurrentGameDTO(val matchId: Long) {
  var basicInfo: BasicGameInfoDTO = new BasicGameInfoDTO(matchId)
  var direTeam:TeamDTO = _
  var radiantTeam:TeamDTO = _
  var netWorth:NetWorthDTO = _

}





