package com.eb.schedule.services

import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

import com.eb.schedule.RestBasicTest
import com.eb.schedule.dto.{LeagueDTO, ScheduledGameDTO, SeriesDTO, TeamDTO}
import com.eb.schedule.model.{MatchStatus, SeriesType}
import com.eb.schedule.shared.bean.GameBean
import org.scalatest.Ignore

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 04.06.2016.
  */
class ScheduledGameServiceImplTest extends RestBasicTest {


  test("testGetGameByDate") {
    val now: Long = System.currentTimeMillis()
    val gameDTO: ScheduledGameDTO = ScheduledGameDTO(1, new TeamDTO(36), new TeamDTO(35), LeagueDTO(1), SeriesType.BO3, new Timestamp(now), MatchStatus.SCHEDULED)
    val precondition = Future {
      scheduledGameService.insert(gameDTO)
      val tomorrow: Instant = Instant.ofEpochMilli(now).plus(1, ChronoUnit.DAYS)
      val game2: ScheduledGameDTO = new ScheduledGameDTO(2, new TeamDTO(36), new TeamDTO(35), new LeagueDTO(1), SeriesType.BO3, new Timestamp(tomorrow.toEpochMilli), MatchStatus.LIVE)
      scheduledGameService.insert(game2)
    }
    whenReady(precondition) { result =>
      val games: Map[Long, Seq[GameBean]] = scheduledService.getGameByDate(now)
      assert(1 == games.size)
      assert(1 == games.head._2.head.getId)
      assert(0 == games.head._2.head.radiantWin)
      assert(0 == games.head._2.head.direWin)
    }

    val startGame = Future {
      scheduledGameService.updateStatus(1, MatchStatus.LIVE)
      seriesService.insert(new SeriesDTO(1, 100, 1, None, false, 36))
    }
    whenReady(startGame) { result =>
      val games: Map[Long, Seq[GameBean]] = scheduledService.getGameByDate(now)
      val seqGames: Seq[GameBean] = games.head._2
      assert(0 == seqGames.head.radiantWin)
      assert(0 == seqGames.head.direWin)
    }

    whenReady(seriesService.insert(new SeriesDTO(1, 200, 1, Some(true), true, 36))) { result =>
      val games: Map[Long, Seq[GameBean]] = scheduledService.getGameByDate(now)
      val seqGames: Seq[GameBean] = games.head._2
      assert(1 == seqGames.head.radiantWin)
      assert(0 == seqGames.head.direWin)
    }

    whenReady(seriesService.insert(new SeriesDTO(1, 300, 1, Some(false), true, 36))) { result =>
      val games: Map[Long, Seq[GameBean]] = scheduledService.getGameByDate(now)
      val seqGames: Seq[GameBean] = games.head._2
      assert(1 == seqGames.head.radiantWin)
      assert(1 == seqGames.head.direWin)
    }

    val finished = Future {
      scheduledGameService.updateStatus(1, MatchStatus.FINISHED)
      seriesService.insert(new SeriesDTO(1, 400, 1, Some(true), true, 36))
    }

    whenReady(finished) { result =>
      val games: Map[Long, Seq[GameBean]] = scheduledService.getGameByDate(now)
      val seqGames: Seq[GameBean] = games.head._2
      assert(2 == seqGames.head.radiantWin)
      assert(1 == seqGames.head.direWin)
    }
  }

}
