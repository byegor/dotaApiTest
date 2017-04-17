package com.eb.schedule.live

import com.eb.schedule.cache._
import com.google.gson.{JsonArray, JsonObject}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 10.04.2016.
  */
class LiveGameHelper @Inject()(val heroCache: HeroCache, val itemCache: ItemCache, val leagueCache: LeagueCache, val teamCache: TeamCache, playerCache: PlayerCache, val netWorthService: NetWorthService, val teamService: TeamService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  def transformToDTO(game: JsonObject): Option[CurrentGameDTO] = {
    try {
      if (game.has("scoreboard")) {
        val matchId: Long = game.get("match_id").getAsLong
        val currentGame: CurrentGameDTO = new CurrentGameDTO(matchId)

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


  def fillGameWithOther(game: JsonObject, currentGame: CurrentGameDTO): Unit = {
    val basicPlayerInfo: JsonArray = game.get("players").getAsJsonArray
    val playerInfo: (mutable.Map[Int, PlayerDTO], mutable.Map[Int, PlayerDTO]) = parseBasicPlayerInfo(basicPlayerInfo)

    val scoreBoard: JsonObject = game.get("scoreboard").getAsJsonObject
    currentGame.basicInfo.duration = scoreBoard.get("duration").getAsDouble

    val radiantScoreBoard: JsonObject = scoreBoard.get("radiant").getAsJsonObject
    val radiantScore: Byte = radiantScoreBoard.get("score").getAsByte
    currentGame.basicInfo.radiantScore = radiantScore
    currentGame.radiantTeam.players = parseDeepPlayerInfo(radiantScoreBoard.get("players").getAsJsonArray, playerInfo._1)
    fillWithPicks(radiantScoreBoard, currentGame.radiantTeam)
    val direScoreBoard: JsonObject = scoreBoard.get("dire").getAsJsonObject
    val direScore: Byte = direScoreBoard.get("score").getAsByte
    currentGame.basicInfo.direScore = direScore
    currentGame.direTeam.players = parseDeepPlayerInfo(direScoreBoard.get("players").getAsJsonArray, playerInfo._2)
    fillWithPicks(direScoreBoard, currentGame.direTeam)
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

  private def fillWithPicks(scoreboard: JsonObject, team: TeamDTO): Unit = {
    val picks: JsonArray = scoreboard.getAsJsonArray("picks")
    if (picks != null) {
      for (i <- 0 until picks.size()) {
        val pick: JsonObject = picks.get(i).getAsJsonObject
        val heroId: Int = pick.get("hero_id").getAsInt
        team.picks ::= heroCache.getHero(heroId)
      }
      val bans: JsonArray = scoreboard.getAsJsonArray("bans")
      if (bans != null) {
        for (i <- 0 until bans.size()) {
          val ban: JsonObject = bans.get(i).getAsJsonObject
          val heroId: Int = ban.get("hero_id").getAsInt
          team.bans ::= heroCache.getHero(heroId)
        }
      }
    }
  }

  def parseTeam(json: JsonObject): TeamDTO = {
    if (json == null) {
      new TeamDTO(0)
    } else {
      val teamId: Int = json.get("team_id").getAsInt
      val team: TeamDTO = new TeamDTO(teamId)
      team.name = json.get("team_name").getAsString
      team.logo = json.get("team_logo").getAsLong
      teamService.upsert(team).onSuccess { case res => teamCache.invalidateTeam(teamId) }
      team
    }
  }


}
