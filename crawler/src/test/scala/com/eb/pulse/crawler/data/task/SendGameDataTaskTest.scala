package com.eb.pulse.crawler.data.task

import java.sql.Timestamp

import com.eb.pulse.crawler.TestLookup
import com.eb.schedule.model.BasicWordSuiteTest
import com.eb.schedule.model.slick.MatchSeries
import com.eb.schedule.shared.bean.GameBean

/**
  * Created by Egor on 13.05.2017.
  */
class SendGameDataTaskTest extends BasicWordSuiteTest {

  "fill game bean with score" should {
    "evaluate normal score" in {
      val task = new SendGameDataTask(Nil, TestLookup.gameService, TestLookup.matchService, TestLookup.httpUtils, TestLookup.cacheHelper);
      val bean = new GameBean()
      val matches = List(
        MatchSeries(1, 1, gameNumber = 1, Some(true), true, 35, new Timestamp(1)),
        MatchSeries(1, 2, gameNumber = 2, Some(false), true, 35, new Timestamp(2)),
        MatchSeries(1, 3, gameNumber = 3, Some(true), true, 35, new Timestamp(3))
      )
      task.fillWithScore(bean, matches)
      bean.radiantWin shouldEqual 2
      bean.direWin shouldEqual 1
    }

    "evaluate rehost score" in {
      val task = new SendGameDataTask(Nil, TestLookup.gameService, TestLookup.matchService, TestLookup.httpUtils, TestLookup.cacheHelper);
      val bean = new GameBean()
      val matches = List(
        MatchSeries(1, 1, gameNumber = 1, Some(true), true, 35, new Timestamp(1)),
        MatchSeries(1, 2, gameNumber = 2, Some(false), true, 35, new Timestamp(2)),
        MatchSeries(1, 3, gameNumber = 2, Some(false), true, 35, new Timestamp(3)),
        MatchSeries(1, 4, gameNumber = 3, Some(true), true, 35, new Timestamp(4))
      )
      task.fillWithScore(bean, matches)
      bean.radiantWin shouldEqual 2
      bean.direWin shouldEqual 1
    }
  }

}
