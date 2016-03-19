package com.eb.schedule.model.services

import com.eb.schedule.dto.{PickDTO, TeamDTO}
import com.eb.schedule.model.BasicTest
import com.eb.schedule.model.dao.PickRepository
import com.eb.schedule.model.slick.{Pick, Team}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 20.02.2016.
  */
class PickServiceTest extends BasicTest {


  test("test picks") {
    val pickRepository: PickRepository = injector.getInstance(classOf[PickRepository])
    val pick: Pick = new Pick(1, 1, true, true)
    pickRepository.insert(List(pick, new Pick(1, 2, true, true)))
    assert(2 == Await.result(pickRepository.findByMatchId(1), Duration.Inf).size)

    assert(pick == Await.result(pickRepository.findById(pick), Duration.Inf))

    val t: TeamDTO = new TeamDTO(1, "")
    t.picks = List(new PickDTO(1), new PickDTO(2), new PickDTO(3))
    pickService.insertIfNotExists(1, t, true)
    Thread.sleep(1500)
    assert(3 == Await.result(pickRepository.findByMatchId(1), Duration.Inf).size)
  }

}
