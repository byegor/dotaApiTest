package com.eb.schedule.services

import java.util.concurrent.ConcurrentHashMap

import com.eb.schedule.cache.{CacheHelper, CachedTeam}
import com.eb.schedule.data.DataStorage
import com.eb.schedule.dto.{LeagueDTO, ScheduledGameDTO, SeriesDTO}
import com.eb.schedule.live.GameContainer
import com.eb.schedule.model.services.ScheduledGameService
import com.eb.schedule.shared.bean.{GameBean, LeagueBean, TeamBean}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.inject.Inject
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import org.slf4j.LoggerFactory

import scala.collection.immutable.{HashMap, ListMap}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by Egor on 01.04.2017.
  */
class DataProcessor @Inject()(scheduledGameService: ScheduledGameService, seriesService: SeriesService, cacheHelper: CacheHelper) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val formatter = DateTimeFormat.forPattern("EEEE, d MMM")

  //todo live
  def processCurrentGames(): Unit = {

    val gamesBetweenDate: Future[Map[ScheduledGameDTO, Seq[SeriesDTO]]] = scheduledGameService.getRecentGamesRethink(System.currentTimeMillis())
    gamesBetweenDate.onComplete {
      case Success(gamesPair) => {
        val currentGames = processGames(gamesPair)

        val currentGamesJson = mapper.writeValueAsString(currentGames)
        val currentMatchesJson = processMatch(gamesPair.values.flatten)

        val matchesByGames = new ConcurrentHashMap[String, String]()
        for((game, mathces) <- gamesPair){

          var matchesByGameNumber = HashMap[String, String]()
          for(m <- mathces.sortBy(_.startDate.getTime)){
            val jsonMatch = currentMatchesJson.get(m.matchId.toString)
            matchesByGameNumber = matchesByGameNumber + (m.gameNumber.toString -> jsonMatch)
          }
          matchesByGames.put(game.id.toString, mapper.writeValueAsString(matchesByGameNumber))
        }

        DataStorage.setCurrentGames(currentGamesJson, currentMatchesJson, matchesByGames)
      }
      case Failure(ex) => log.error("Couldn't get current games", ex)

    }
  }


  def processGames(gamesPair: Map[ScheduledGameDTO, Seq[SeriesDTO]]): Map[String, List[GameBean]] = {
    var currentGames: List[GameBean] = Nil
    for ((game, matches) <- gamesPair) {
      val finishedMatches = matches.filter(_.radiantWin.isDefined)
      val gameBean = new GameBean()
      fillTeamAndLeague(gameBean, game)
      fillWithScore(gameBean, finishedMatches)
      gameBean.setId(game.id)
      gameBean.setSeriesType(game.seriesType.name())
      gameBean.setGameStatus(game.matchStatus.status)
      gameBean.setNumberOfGames(matches.maxBy(_.gameNumber).gameNumber)
      gameBean.setStartTime(game.startDate.getTime)

      currentGames = gameBean :: currentGames
    }
    val currentGamesByMillis: Map[Long, List[GameBean]] = currentGames.groupBy(g => getMillisInUTC(g.startTime))
    val sortedCurrentGameByMillis: ListMap[Long, List[GameBean]] = ListMap(currentGamesByMillis.toSeq.sortWith(_._1 > _._1): _*)
    sortedCurrentGameByMillis.map { case (k, v) => formatter.print(k) -> v.sortBy(g => (g.gameStatus, -g.startTime)) }

  }

  def processMatch(matches: Iterable[SeriesDTO]) = {
    val allMatchesById: ConcurrentHashMap[String, String] = new ConcurrentHashMap[String, String]
    for (m <- matches) {
      if (GameContainer.exists(m.matchId)) {
        val liveMatch = mapper.writeValueAsString(GameContainer.getLiveGame(m.matchId).get.toMatch())
        allMatchesById.put(m.matchId.toString, liveMatch)
      } else {
        cacheHelper.getMatch(m.matchId) match {
          case Some(x) => allMatchesById.put(m.matchId.toString, x)
          case None => None
        }
      }
    }
    allMatchesById
  }

  def fillTeamAndLeague(gameBean: GameBean, game: ScheduledGameDTO): Unit = {
    val radiantTeam: CachedTeam = cacheHelper.getTeam(game.radiantTeam.id)
    val direTeam: CachedTeam = cacheHelper.getTeam(game.direTeam.id)
    val league: LeagueDTO = cacheHelper.getLeague(game.league.leagueId)
    gameBean.setRadiant(new TeamBean(radiantTeam.id, radiantTeam.name, radiantTeam.tag, radiantTeam.logo))
    gameBean.setDire(new TeamBean(direTeam.id, direTeam.name, direTeam.tag, direTeam.logo))
    gameBean.setLeague(new LeagueBean(league.leagueId, league.leagueName))
  }


  def fillWithScore(gameBean: GameBean, matches: Seq[SeriesDTO]) = {
    matches.foreach(matches =>
      if (matches.radiantWin.get) gameBean.setRadiantWin(gameBean.radiantWin + 1) else gameBean.setDireWin(gameBean.direWin + 1))
  }

  def getMillisInUTC(mil: Long): Long  = {
    new DateTime().withMillis(mil).withZone(DateTimeZone.UTC).withTimeAtStartOfDay().getMillis
  }
}
