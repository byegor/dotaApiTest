package com.eb.schedule.services

import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

import com.eb.schedule.RestBasicTest
import com.eb.schedule.dto.{LeagueDTO, ScheduledGameDTO, SeriesDTO, TeamDTO}
import com.eb.schedule.model.{MatchStatus, SeriesType}
import com.eb.schedule.shared.bean.GameBean
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
      val games: Map[String, Seq[GameBean]] = scheduledService.getGameByDate(now)
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
      val games: Map[String, Seq[GameBean]] = scheduledService.getGameByDate(now)
      val seqGames: Seq[GameBean] = games.head._2
      assert(0 == seqGames.head.radiantWin)
      assert(0 == seqGames.head.direWin)
    }

    whenReady(seriesService.insert(new SeriesDTO(1, 200, 1, Some(true), true, 36))) { result =>
      val games: Map[String, Seq[GameBean]] = scheduledService.getGameByDate(now)
      val seqGames: Seq[GameBean] = games.head._2
      assert(1 == seqGames.head.radiantWin)
      assert(0 == seqGames.head.direWin)
    }

    whenReady(seriesService.insert(new SeriesDTO(1, 300, 1, Some(false), true, 36))) { result =>
      val games: Map[String, Seq[GameBean]] = scheduledService.getGameByDate(now)
      val seqGames: Seq[GameBean] = games.head._2
      assert(1 == seqGames.head.radiantWin)
      assert(1 == seqGames.head.direWin)
    }

    val finished = Future {
      scheduledGameService.updateStatus(1, MatchStatus.FINISHED)
      seriesService.insert(new SeriesDTO(1, 400, 1, Some(true), true, 36))
    }

    whenReady(finished) { result =>
      val games: Map[String, Seq[GameBean]] = scheduledService.getGameByDate(now)
      val seqGames: Seq[GameBean] = games.head._2
      assert(2 == seqGames.head.radiantWin)
      assert(1 == seqGames.head.direWin)
    }
  }

  test("get game order by day") {
    val yesterday: DateTime = new DateTime().minusDays(1)
    val gameDTO: ScheduledGameDTO = ScheduledGameDTO(11, new TeamDTO(11), new TeamDTO(35), LeagueDTO(1), SeriesType.BO3, new Timestamp(yesterday.getMillis), MatchStatus.SCHEDULED)
    var newGameId: Int = 0
    var oldGameId: Int = 0
    val precondition = Future {
      newGameId = scheduledGameService.insertAndGet(gameDTO)
      val beforeYesterday: DateTime = new DateTime().minusDays(2).plusHours(1)
      val game2: ScheduledGameDTO = new ScheduledGameDTO(22, new TeamDTO(22), new TeamDTO(35), new LeagueDTO(1), SeriesType.BO3, new Timestamp(beforeYesterday.getMillis), MatchStatus.LIVE)
      oldGameId = scheduledGameService.insertAndGet(game2)
    }

    whenReady(precondition) { result =>
      val games: Map[String, Seq[GameBean]] = scheduledService.getGameByDate(yesterday.getMillis)
      assert(2 == games.size, "Expecting games for today and yesterday:" + games)
      assert(newGameId == games.head._2.head.getId, "First we should show last started game: " + games.head)
      assert(oldGameId == games.last._2.head.getId, "Game started earlier should be the last in the list" + games.head)

    }
  }

  test("get game order within the day") {
    val now: DateTime = new DateTime()
    val game1: ScheduledGameDTO = ScheduledGameDTO(11, new TeamDTO(11), new TeamDTO(35), LeagueDTO(1), SeriesType.BO3, new Timestamp(now.getMillis), MatchStatus.FINISHED)
    val game2: ScheduledGameDTO = ScheduledGameDTO(22, new TeamDTO(22), new TeamDTO(35), LeagueDTO(1), SeriesType.BO3, new Timestamp(now.minusHours(1).getMillis), MatchStatus.LIVE)
    val game3: ScheduledGameDTO = ScheduledGameDTO(33, new TeamDTO(33), new TeamDTO(35), LeagueDTO(1), SeriesType.BO3, new Timestamp(now.minusHours(2).getMillis), MatchStatus.LIVE)
    var id1: Int = 0
    var id2: Int = 0
    var id3: Int = 0

    val precondition = Future {
      id1 = scheduledGameService.insertAndGet(game1)
      id2 = scheduledGameService.insertAndGet(game2)
      id3 = scheduledGameService.insertAndGet(game3)
    }

    whenReady(precondition) { result =>
      val games: Map[String, Seq[GameBean]] = scheduledService.getGameByDate(now.getMillis)
      assert(1 == games.size, "Expecting games for one day today:" + games)
      val gameSeq: Seq[GameBean] = games.head._2
      assert(id2 == gameSeq.head.getId, "Expecting first game - Live which started last: " + gameSeq)
      assert(id1 == gameSeq.last.getId, "Expecting last game - finished game: " + gameSeq)
    }
  }

}
