package com.eb.schedule

import com.eb.schedule.cache._
import com.eb.schedule.dto.{NetWorthDTO, PlayerDTO, TeamDTO}
import com.eb.schedule.services.NetWorthService
import com.google.gson.{JsonArray, JsonObject}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}


/**
  * Created by Egor on 02.05.2016.
  */
class MatchParser @Inject()(teamCache: TeamCache, leagueCache: LeagueCache, playerCache: PlayerCache, heroCache: HeroCache, itemCache: ItemCache, netWorthService: NetWorthService) {
  private val log = LoggerFactory.getLogger(this.getClass)


  def parseMatch(jsonObject: JsonObject): MatchDTO = {
    new MatchBuilder(jsonObject).parseTeams().addMatchPlayers().addBasicInfo().addBuildingStatus().addMatchPicks().buildMatch()
  }


  private class MatchBuilder(json: JsonObject) {
    val matchDetails = new MatchDTO()
    var playerFuture: List[Future[Unit]] = Nil

    def buildMatch(): MatchDTO = {
      Await.result(Future.sequence(playerFuture), 10 second)
      matchDetails
    }

    def parseTeams() = {
      matchDetails.radiantTeam = parseTeam("radiant_team_id", "radiant_name", "radiant_logo")
      matchDetails.direTeam = parseTeam("dire_team_id", "dire_name", "dire_logo")
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
          playerFuture ::= Future {
            val player: JsonObject = playersList.get(i).getAsJsonObject
            val accountId: Int = player.get("account_id").getAsInt
            val playerDTO = new PlayerDTO(accountId)
            playerDTO.name = playerCache.getPlayerName(accountId)

            val heroId: Int = player.get("hero_id").getAsInt
            playerDTO.hero = heroCache.getHero(heroId)

            parseKDA(player, playerDTO)
            parseItem(player, playerDTO)

            val isRadiant = player.get("player_slot").getAsInt < 5
            if (isRadiant) matchDetails.radiantTeam.players ::= playerDTO else matchDetails.direTeam.players ::= playerDTO
          }
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
      matchDetails.duration = json.get("duration").getAsInt
      matchDetails.radiantWin = json.get("radiant_win").getAsBoolean
      matchDetails.startTime = json.get("start_time").getAsLong
      matchDetails.radiantScore = json.get("radiant_score").getAsByte
      matchDetails.direScore = json.get("dire_score").getAsByte

      matchDetails.league = leagueCache.getLeague(json.get("leagueid").getAsInt)
      val netWorthFuture: Future[Option[NetWorthDTO]] = netWorthService.findByMatchId(matchDetails.matchId)
      val result: Option[NetWorthDTO] = Await.result(netWorthFuture, 5 second)
      if (result.isDefined) {
        matchDetails.netWorth = result.get
      }
      this
    }

    def parseKDA(playerJson: JsonObject, playerDTO: PlayerDTO): Unit = {
      playerDTO.kills = playerJson.get("kills").getAsInt
      playerDTO.deaths = playerJson.get("deaths").getAsInt
      playerDTO.assists = playerJson.get("assists").getAsInt
      playerDTO.level = playerJson.get("level").getAsInt
    }

    def parseItem(playerJson: JsonObject, playerDTO: PlayerDTO): Unit = {
      for (i <- 0 until 6) {
        val itemId: Int = playerJson.get("item_" + i).getAsInt
        if (itemId != 0) {
          playerDTO.items ::= itemCache.getItem(itemId)
        }
      }

    }

    def parseTeam(id: String, teamName: String, teamLogo: String) = {
      try{
      val teamId: Int = json.get(id).getAsInt
      val cachedTeam: CachedTeam = teamCache.getTeam(teamId)
      if (cachedTeam.name != "") {
        val teamDTO: TeamDTO = new TeamDTO(cachedTeam.id)
        teamDTO.name = cachedTeam.name
        teamDTO.logo = cachedTeam.logo
        teamDTO.tag = cachedTeam.tag
        teamDTO
      } else {
        val team: TeamDTO = new TeamDTO(teamId)
        team.name = json.get(teamName).getAsString
        team.logo = json.get(teamLogo).getAsLong
        team
      }
    }catch {
        case e: Throwable => log.error("Couldn't parse team, e")
          new TeamDTO(-1)
      }
    }
  }


}
