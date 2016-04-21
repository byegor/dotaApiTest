package com.eb.schedule.live

import com.eb.schedule.cache.{HeroCache, ItemCache, LeagueCache, TeamCache}
import com.eb.schedule.dto.{CurrentGameDTO, NetWorthDTO, PlayerDTO, TeamDTO}
import com.eb.schedule.model.SeriesType
import com.eb.schedule.services.NetWorthService
import com.google.inject.Inject
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 10.04.2016.
  */
class LiveGameHelper @Inject()(val heroCache: HeroCache, val itemCache: ItemCache, val leagueCache: LeagueCache, val teamCache: TeamCache, val netWorthService: NetWorthService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  def transformToDTO(game: JSONObject): Option[CurrentGameDTO] = {
    try {
      val matchId: Long = game.getLong("match_id")
      val currentGame: CurrentGameDTO = new CurrentGameDTO(matchId)
      fillGameWithTeams(game, currentGame)
      fillGameWithOther(game, currentGame)
      fillGameWithNetWorth(currentGame)
      Some(currentGame)
    }catch {
      case e => {
        log.error("couldn't parse live game: " + game, e)
        None
      }
    }
  }

  def fillGameWithTeams(game: JSONObject, currentGame: CurrentGameDTO): Unit = {
    val leagueId: Int = game.getInt("league_id")
    currentGame.basicInfo.league = leagueCache.getLeague(leagueId)
    currentGame.basicInfo.radiantWin = game.getInt("radiant_series_wins").toByte
    currentGame.basicInfo.direWin = game.getInt("dire_series_wins").toByte
    currentGame.basicInfo.radiantTeam = parseTeam(game.getJSONObject("radiant_team"))
    currentGame.basicInfo.direTeam = parseTeam(game.getJSONObject("dire_team"))
    currentGame.basicInfo.seriesType = SeriesType.fromCode(game.getInt("series_type").toByte)
    currentGame.radiantTeam = currentGame.basicInfo.radiantTeam.copy()
    currentGame.direTeam = currentGame.basicInfo.direTeam.copy()
  }

  def fillGameWithOther(game: JSONObject, currentGame: CurrentGameDTO): Unit = {
    val basicPlayerInfo: JSONArray = game.getJSONArray("players")
    val playerInfo: (mutable.Map[Int, PlayerDTO], mutable.Map[Int, PlayerDTO]) = parseBasicPlayerInfo(basicPlayerInfo)

    val scoreBoard: JSONObject = game.getJSONObject("scoreboard")
    currentGame.basicInfo.duration = scoreBoard.getDouble("duration")

    val radiantScoreBoard: JSONObject = scoreBoard.getJSONObject("radiant")
    currentGame.radiantTeam.players = parseDeepPlayerInfo(radiantScoreBoard.getJSONArray("players"), playerInfo._1)
    val direScoreBoard: JSONObject = scoreBoard.getJSONObject("dire")
    currentGame.direTeam.players = parseDeepPlayerInfo(direScoreBoard.getJSONArray("players"), playerInfo._2)
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

  def parseDeepPlayerInfo(playerInfo: JSONArray, basicPlayerInfo: mutable.Map[Int, PlayerDTO]): List[PlayerDTO] = {
    for (i <- 0 until playerInfo.length()) {
      val player: JSONObject = playerInfo.getJSONObject(i)
      val accId: Int = player.getInt("account_id")
      val playerDTO: PlayerDTO = basicPlayerInfo.get(accId).get
      playerDTO.kills = player.getInt("kills")
      playerDTO.deaths = player.getInt("death")
      playerDTO.assists = player.getInt("assists")
      playerDTO.level = player.getInt("level")
      playerDTO.netWorth = player.getInt("net_worth")

      playerDTO.hero = heroCache.getHero(player.getInt("hero_id"))

      fillPlayerDTOWithItem(playerDTO, player.getInt("item0"))
      fillPlayerDTOWithItem(playerDTO, player.getInt("item1"))
      fillPlayerDTOWithItem(playerDTO, player.getInt("item2"))
      fillPlayerDTOWithItem(playerDTO, player.getInt("item3"))
      fillPlayerDTOWithItem(playerDTO, player.getInt("item4"))
      fillPlayerDTOWithItem(playerDTO, player.getInt("item5"))
    }
    basicPlayerInfo.values.toList
  }

  def fillPlayerDTOWithItem(playerDTO: PlayerDTO, itemId: Int): Unit = {
    if (itemId != 0) {
      playerDTO.items ::= itemCache.getItem(itemId)
    }
  }

  def parseBasicPlayerInfo(basicPlayerInfo: JSONArray): (scala.collection.mutable.Map[Int, PlayerDTO], scala.collection.mutable.Map[Int, PlayerDTO]) = {
    val radiantPlayers: scala.collection.mutable.Map[Int, PlayerDTO] = new scala.collection.mutable.HashMap[Int, PlayerDTO]
    val direPlayers: scala.collection.mutable.Map[Int, PlayerDTO] = new scala.collection.mutable.HashMap[Int, PlayerDTO]
    for (i <- 0 until basicPlayerInfo.length()) {
      val player: JSONObject = basicPlayerInfo.getJSONObject(i)
      val team: Int = player.getInt("team")
      if (team < 2) {
        val playerDTO: PlayerDTO = new PlayerDTO(player.getInt("account_id"))
        playerDTO.name = player.getString("name")
        if (team == 0) {
          radiantPlayers.put(playerDTO.accountId, playerDTO)
        } else {
          direPlayers.put(playerDTO.accountId, playerDTO)
        }
      }
    }

    (radiantPlayers, direPlayers)
  }

  private def parseTeam(json: JSONObject): TeamDTO = {
    val teamId: Int = json.getInt("team_id")
    val teamDto: TeamDTO = teamCache.getTeam(teamId)
    if (teamDto != teamCache.unknownTeam) {
      teamDto
    } else {
      val team: TeamDTO = new TeamDTO(teamId)
      team.name = json.getString("team_name")
      team.logo = json.getLong("team_logo")
      team
    }
  }


}
