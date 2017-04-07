package com.eb.schedule.services

import java.sql.Timestamp

import com.eb.schedule.RestBasicTest
import com.eb.schedule.dto.{LeagueDTO, ScheduledGameDTO, SeriesDTO, TeamDTO}
import com.eb.schedule.model.{MatchStatus, SeriesType}
import com.eb.schedule.shared.bean.{GameBean, LeagueBean, TeamBean}
import org.joda.time.{DateTime, DateTimeZone}

/**
  * Created by Egor on 05.04.2017.
  */
//todo test rehost
class DataProcessorTest extends RestBasicTest {

  test("Get time in mills") {
    val millis = new DateTime(2017, 1, 1, 0, 0, DateTimeZone.UTC).getMillis
    assert(millis == dataProcessor.getMillisInUTC(millis))
  }

  test("Fill with score game bean") {
    var bean = new GameBean()
    dataProcessor.fillWithScore(bean, List(SeriesDTO(1, 2, 1, Some(true), finished = false, 10)))
    assert(bean.getRadiantWin == 1 && bean.getDireWin == 0, "radiant win  - should be 1:0 and not " + bean.getRadiantWin + " " + bean.getDireWin)

    bean = new GameBean()
    dataProcessor.fillWithScore(bean, List(SeriesDTO(1, 2, 1, Some(true), finished = false, 10), SeriesDTO(1, 2, 1, Some(true), finished = false, 10), SeriesDTO(1, 2, 1, Some(false), finished = false, 10)))
    assert(bean.getRadiantWin == 2 && bean.getDireWin == 1, "should be 2:1 and not " + bean.getRadiantWin + " " + bean.getDireWin)
  }

  test("fill team and league") {
    val bean = new GameBean()
    val gameDTO: ScheduledGameDTO = ScheduledGameDTO(1, new TeamDTO(36), new TeamDTO(35), LeagueDTO(1), SeriesType.BO3, new Timestamp(System.currentTimeMillis()), MatchStatus.LIVE)

    dataProcessor.fillTeamAndLeague(bean, gameDTO)
    assert(bean.radiant.isInstanceOf[TeamBean])
    assert(36 == bean.radiant.getId)
    assert(35 == bean.dire.getId)
    assert(bean.league.isInstanceOf[LeagueBean])
    assert(1 == bean.league.getId)
  }

  test("processGames check fill properties") {
    val gameDTO: ScheduledGameDTO = ScheduledGameDTO(1, new TeamDTO(36), new TeamDTO(35), LeagueDTO(1), SeriesType.BO5, new Timestamp(System.currentTimeMillis()), MatchStatus.LIVE)
    val matches = List(
      SeriesDTO(1, 2, 1, Some(true), finished = false, 10), SeriesDTO(1, 21, 2, Some(true), finished = false, 10),
      SeriesDTO(1, 31, 3, Some(false), finished = false, 10), SeriesDTO(1, 41, 4, None, finished = false, 10)
    )

    val gamePair = Map(gameDTO -> matches)
    val processedGames = dataProcessor.processGames(gamePair)
    assert(1 == processedGames.size, "key should be only one, for current game")
    val currentGames = processedGames.values.head
    assert(1 == currentGames.size, "only one game was set")
    val game = currentGames.head
    assert(2 == game.getRadiantWin)
    assert(1 == game.getDireWin)
    assert(4 == game.getNumberOfGames)
  }

  test("processGames: sorting") {
    val game1: ScheduledGameDTO = ScheduledGameDTO(1, new TeamDTO(36), new TeamDTO(35), LeagueDTO(1), SeriesType.BO1, new Timestamp(System.currentTimeMillis()), MatchStatus.LIVE)
    val game2: ScheduledGameDTO = ScheduledGameDTO(2, new TeamDTO(36), new TeamDTO(35), LeagueDTO(1), SeriesType.BO1, new Timestamp(System.currentTimeMillis() + 10000), MatchStatus.LIVE)
    val game3: ScheduledGameDTO = ScheduledGameDTO(3, new TeamDTO(36), new TeamDTO(35), LeagueDTO(1), SeriesType.BO1, new Timestamp(System.currentTimeMillis() + 5000), MatchStatus.FINISHED)
    val matchDto = List(SeriesDTO(1, 2, 1, Some(true), finished = false, 10))
    val gamePair = Map(game1 -> matchDto, game2 -> matchDto, game3 -> matchDto)
    val processedGames = dataProcessor.processGames(gamePair)
    val todayGames = processedGames.values.head

    assert(List(game2, game1, game3).map(_.id) == todayGames.map(_.id))
  }


}
