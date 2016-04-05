package com.eb.schedule.dto

/**
  * Created by Egor on 13.03.2016.
  */
class CurrentGameDTO(val matchId: Long) {
  var basicInfo: BasicGameInfoDTO = new BasicGameInfoDTO(matchId)
  var direTeam:TeamDTO = ???
  var radiantTeam:TeamDTO = ???
  var netWorth:NetWorthDTO = ???

}





