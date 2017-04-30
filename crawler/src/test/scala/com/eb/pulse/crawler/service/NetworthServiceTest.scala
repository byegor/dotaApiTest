package com.eb.pulse.crawler.service

import com.eb.pulse.crawler.{Lookup, TestLookup}
import com.eb.schedule.model.BasicFunSuiteTest
import com.eb.schedule.model.slick.NetWorth

/**
  * Created by Egor on 20.04.2017.
  */
class NetworthServiceTest extends BasicFunSuiteTest {

  val worthService = new NetworthService(TestLookup.netWorthRepository)

  private def insertNetWorth(matchId: Long, net: String) = {
    worthService.insertOrUpdate(NetWorth(matchId, net))
  }

  test("find net worth") {
    whenReady(insertNetWorth(1, "10,20")) { result =>
      assert("10,20" == worthService.findByMatchId(1).netWorth)
    }
  }

  test("update net worth") {
    whenReady(insertNetWorth(1, "10,20")) { result =>
      val update = worthService.insertOrUpdate( NetWorth(1, "15"))
      whenReady(update){res =>
        assert("10,20,15" == worthService.findByMatchId(1).netWorth)
      }
    }
  }

}
