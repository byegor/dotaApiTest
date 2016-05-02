package com.eb.schedule

import com.eb.schedule.cache.{LeagueCache, PlayerCache, TeamCache}
import com.eb.schedule.dto.{CurrentGameDTO, TeamDTO}
import com.google.gson.JsonObject


/**
  * Created by Egor on 02.05.2016.
  */
class MatchParser(teamCache: TeamCache, leagueCache: LeagueCache, playerCache: PlayerCache) {

  private class MatchBuilder(matchId:Long, json:JsonObject){
    val matchDetails:CurrentGameDTO = new CurrentGameDTO(matchId)

    def parseTeams()={
      matchDetails.radiantTeam = parseTeam("radiant_team_id", "radiant_name", "radiant_logo")
      matchDetails.radiantTeam = parseTeam("dire_team_id", "dire_name", "dire_logo")
      this
    }

    def parseTeam(id:String, teamName:String, teamLogo:String) ={
      val teamId: Int = json.get(id).getAsInt
      val teamDto: TeamDTO = teamCache.getTeam(teamId)
      if (teamDto != teamCache.unknownTeam) {
        teamDto
      } else {
        val team: TeamDTO = new TeamDTO(teamId)
        team.name = json.get(teamName).getAsString
        team.logo = json.get(teamLogo).getAsLong
        team
      }
    }
  }



}
