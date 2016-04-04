package com.eb.schedule

import com.eb.schedule.model.BasicTest
import org.json.JSONObject
import org.mockito.Mockito._

import scala.io.BufferedSource

/**
  * Created by Egor on 23.03.2016.
  */
class LiveGameProcessorTest extends BasicTest{

  test("getLiveLeagueGames"){
    val processor:LiveGameProcessor = org.mockito.Mockito.spy(new LiveGameProcessor)
    when(processor.executeGet()).thenReturn(getLiveMatchResponse)

    val games: List[JSONObject] = processor.getLiveLeagueGames()
    assert(1 == games.size)
  }


  def getLiveMatchResponse: JSONObject = {
    val source: BufferedSource = io.Source.fromURL(getClass.getResource("/live.json"))
    val lines = try source.mkString finally source.close()
    val liveMatchResult: JSONObject = new JSONObject(lines)
    liveMatchResult
  }
}
