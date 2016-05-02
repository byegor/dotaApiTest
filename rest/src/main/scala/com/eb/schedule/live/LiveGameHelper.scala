package com.eb.schedule.live

import com.eb.schedule.cache._
import com.eb.schedule.dto.{CurrentGameDTO, NetWorthDTO, PlayerDTO, TeamDTO}
import com.eb.schedule.model.SeriesType
import com.eb.schedule.services.NetWorthService
import com.google.gson.{JsonArray, JsonObject}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 10.04.2016.
  */
class LiveGameHelper @Inject()(val heroCache: HeroCache, val itemCache: ItemCache, val leagueCache: LeagueCache, val teamCache: TeamCache, playerCache:PlayerCache, val netWorthService: NetWorthService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  def transformToDTO(game: JsonObject): Option[CurrentGameDTO] = {
    try {
      if (game.has("scoreboard")) {
        val matchId: Long = game.get("match_id").getAsLong
        val currentGame: CurrentGameDTO = new CurrentGameDTO(matchId)
        fillGameWithTeams(game, currentGame)
        fillGameWithOther(game, currentGame)
        fillGameWithNetWorth(currentGame)
        Some(currentGame)
      } else {
        None
      }
    } catch {
      case e: Throwable => {
        log.error("couldn't parse live game: " + game, e)
        None
      }
    }
  }

  def fillGameWithTeams(game: JsonObject, currentGame: CurrentGameDTO): Unit = {
    val leagueId: Int = game.get("league_id").getAsInt
    currentGame.basicInfo.league = leagueCache.getLeague(leagueId)
    currentGame.basicInfo.radiantWin = game.get("radiant_series_wins").getAsByte
    currentGame.basicInfo.direWin = game.get("dire_series_wins").getAsByte
    currentGame.basicInfo.radiantTeam = parseTeam(game.getAsJsonObject("radiant_team"))
    currentGame.basicInfo.direTeam = parseTeam(game.getAsJsonObject("dire_team"))
    currentGame.basicInfo.seriesType = SeriesType.fromCode(game.get("series_type").getAsByte)
    currentGame.radiantTeam = currentGame.basicInfo.radiantTeam.copy()
    currentGame.direTeam = currentGame.basicInfo.direTeam.copy()
  }

  def fillGameWithOther(game: JsonObject, currentGame: CurrentGameDTO): Unit = {
    val basicPlayerInfo: JsonArray = game.get("players").getAsJsonArray
    val playerInfo: (mutable.Map[Int, PlayerDTO], mutable.Map[Int, PlayerDTO]) = parseBasicPlayerInfo(basicPlayerInfo)

    val scoreBoard: JsonObject = game.get("scoreboard").getAsJsonObject
    currentGame.basicInfo.duration = scoreBoard.get("duration").getAsDouble

    val radiantScoreBoard: JsonObject = scoreBoard.get("radiant").getAsJsonObject
    currentGame.radiantTeam.players = parseDeepPlayerInfo(radiantScoreBoard.get("players").getAsJsonArray, playerInfo._1)
    val direScoreBoard: JsonObject = scoreBoard.get("dire").getAsJsonObject
    currentGame.direTeam.players = parseDeepPlayerInfo(direScoreBoard.get("players").getAsJsonArray, playerInfo._2)
  }

  def fillGameWithNetWorth(currentGame: CurrentGameDTO): Unit = {
    val storedNetWorth: Future[Option[NetWorthDTO]] = netWorthService.findByMatchId(currentGame.matchId)
    currentGame.radiantTeam.netWorth = currentGame.radiantTeam.players.foldLeft(0)((res, player) => res + player.netWorth)
    currentGame.direTeam.netWorth = currentGame.direTeam.players.foldLeft(0)((res, player) => res + player.netWorth)
    val currentNetWorth = currentGame.radiantTeam.netWorth - currentGame.direTeam.netWorth
    val netWorth: Option[NetWorthDTO] = Await.result(storedNetWorth, Duration.Inf)
    val newNetWorth: NetWorthDTO = netWorth match {
      case Some(nw) => nw.netWorth ::= currentNetWorth; nw
      case None => new NetWorthDTO(currentGame.matchId, List(0, currentNetWorth))
    }
    currentGame.netWorth = newNetWorth
  }

  def parseDeepPlayerInfo(playerInfo: JsonArray, basicPlayerInfo: mutable.Map[Int, PlayerDTO]): List[PlayerDTO] = {
    for (i <- 0 until playerInfo.size()) {
      val player: JsonObject = playerInfo.get(i).getAsJsonObject
      val accId: Int = player.get("account_id").getAsInt
      val playerDTO: PlayerDTO = basicPlayerInfo.get(accId).get
      playerDTO.kills = player.get("kills").getAsInt
      playerDTO.deaths = player.get("death").getAsInt
      playerDTO.assists = player.get("assists").getAsInt
      playerDTO.level = player.get("level").getAsInt
      playerDTO.netWorth = player.get("net_worth").getAsInt

      playerDTO.hero = heroCache.getHero(player.get("hero_id").getAsInt)

      fillPlayerDTOWithItem(playerDTO, player.get("item0").getAsInt)
      fillPlayerDTOWithItem(playerDTO, player.get("item1").getAsInt)
      fillPlayerDTOWithItem(playerDTO, player.get("item2").getAsInt)
      fillPlayerDTOWithItem(playerDTO, player.get("item3").getAsInt)
      fillPlayerDTOWithItem(playerDTO, player.get("item4").getAsInt)
      fillPlayerDTOWithItem(playerDTO, player.get("item5").getAsInt)
    }
    basicPlayerInfo.values.toList
  }

  def fillPlayerDTOWithItem(playerDTO: PlayerDTO, itemId: Int): Unit = {
    if (itemId != 0) {
      playerDTO.items ::= itemCache.getItem(itemId)
    }
  }

  def parseBasicPlayerInfo(basicPlayerInfo: JsonArray): (scala.collection.mutable.Map[Int, PlayerDTO], scala.collection.mutable.Map[Int, PlayerDTO]) = {
    val radiantPlayers: scala.collection.mutable.Map[Int, PlayerDTO] = new scala.collection.mutable.HashMap[Int, PlayerDTO]
    val direPlayers: scala.collection.mutable.Map[Int, PlayerDTO] = new scala.collection.mutable.HashMap[Int, PlayerDTO]
    for (i <- 0 until basicPlayerInfo.size()) {
      val player: JsonObject = basicPlayerInfo.get(i).getAsJsonObject
      val team: Int = player.get("team").getAsInt
      if (team < 2) {
        val accountId: Int = player.get("account_id").getAsInt
        val name: String = player.get("name").getAsString
        playerCache.put(accountId, name)
        val playerDTO: PlayerDTO = new PlayerDTO(accountId)
        playerDTO.name = name
        if (team == 0) {
          radiantPlayers.put(playerDTO.accountId, playerDTO)
        } else {
          direPlayers.put(playerDTO.accountId, playerDTO)
        }
      }
    }

    (radiantPlayers, direPlayers)
  }

  private def parseTeam(json: JsonObject): TeamDTO = {
    val teamId: Int = json.get("team_id").getAsInt
    val teamDto: TeamDTO = teamCache.getTeam(teamId)
    if (teamDto != teamCache.unknownTeam) {
      teamDto
    } else {
      val team: TeamDTO = new TeamDTO(teamId)
      team.name = json.get("team_name").getAsString
      team.logo = json.get("team_logo").getAsLong
      team
    }
  }


}
