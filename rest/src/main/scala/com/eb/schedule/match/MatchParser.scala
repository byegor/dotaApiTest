package com.eb.schedule

import com.eb.schedule.cache._
import com.eb.schedule.dto.{PlayerDTO, TeamDTO}
import com.google.gson.{JsonArray, JsonObject}


/**
  * Created by Egor on 02.05.2016.
  */
class MatchParser(teamCache: TeamCache, leagueCache: LeagueCache, playerCache: PlayerCache, heroCache: HeroCache, itemCache: ItemCache) {

  def parseMatch(jsonObject: JsonObject):MatchDTO={
    new MatchBuilder(jsonObject).addBasicInfo().parseTeams().addBuildingStatus().addMatchPicks().addMatchPlayers().buildMatch()
  }


  private class MatchBuilder(json: JsonObject) {
    val matchDetails = new MatchDTO()


    def buildMatch(): MatchDTO = {
      matchDetails
    }

    def parseTeams() = {
      matchDetails.radiantTeam = parseTeam("radiant_team_id", "radiant_name", "radiant_logo")
      matchDetails.radiantTeam = parseTeam("dire_team_id", "dire_name", "dire_logo")
      this
    }

    def addMatchPicks() = {
      val pickAndBans: JsonArray = json.get("picks_bans").getAsJsonArray
      for (i <- 0 until pickAndBans.size()) {
        val pick: JsonObject = pickAndBans.get(i).getAsJsonObject
        val heroId: Int = pick.get("hero_id").getAsInt
        val isRadiant: Boolean = pick.get("team").getAsInt == 0
        val isPick: Boolean = pick.get("is_pick").getAsBoolean
        val team = if (isRadiant) matchDetails.radiantTeam else matchDetails.direTeam
        if (isPick) team.picks ::= heroCache.getHero(heroId) else team.bans ::= heroCache.getHero(heroId)
      }
      this
    }


    def addMatchPlayers() = {
      val playersList: JsonArray = json.get("players").getAsJsonArray
      for (i <- 0 until playersList.size()) {
        val player: JsonObject = playersList.get(i).getAsJsonObject
        val accountId: Int = player.get("account_id").getAsInt
        val playerDTO = new PlayerDTO(accountId)
        playerDTO.name = playerCache.getPlayerName(accountId)

        val heroId: Int = player.get("hero_id").getAsInt
        playerDTO.hero = heroCache.getHero(heroId)

        parseKDA(player, playerDTO)
        parseItem(player, playerDTO)

        val isRadiant = player.get("player_slot").getAsInt == 0
        if (isRadiant) matchDetails.radiantTeam.players ::= playerDTO else matchDetails.direTeam.players ::= playerDTO
      }
      this
    }

    def addBuildingStatus() = {
      matchDetails.direTeam.barrackStatus = json.get("barracks_status_dire").getAsInt
      matchDetails.direTeam.towerStatus = json.get("tower_status_dire").getAsInt
      matchDetails.radiantTeam.barrackStatus = json.get("barracks_status_radiant").getAsInt
      matchDetails.radiantTeam.towerStatus = json.get("tower_status_radiant").getAsInt
      this
    }

    def addBasicInfo() = {
      matchDetails.matchId = json.get("match_id").getAsLong
      matchDetails.duration = json.get("duration").getAsDouble
      matchDetails.radiantWin = json.get("radiant_win").getAsBoolean
      matchDetails.startTime = json.get("start_time").getAsLong
      matchDetails.league = leagueCache.getLeague(json.get("leagueid").getAsInt)
      this
    }

    def parseKDA(playerJson: JsonObject, playerDTO: PlayerDTO): Unit = {
      playerDTO.kills = playerJson.get("kills").getAsInt
      playerDTO.deaths = playerJson.get("death").getAsInt
      playerDTO.assists = playerJson.get("assists").getAsInt
      playerDTO.level = playerJson.get("level").getAsInt
    }

    def parseItem(playerJson: JsonObject, playerDTO: PlayerDTO): Unit = {
      for (i <- 0 until 6) {
        val itemId: Int = playerJson.get("item" + i).getAsInt
        if (itemId != 0) {
          playerDTO.items ::= itemCache.getItem(itemId)
        }
      }

    }

    def parseTeam(id: String, teamName: String, teamLogo: String) = {
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
