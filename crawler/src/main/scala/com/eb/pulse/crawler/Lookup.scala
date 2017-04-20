package com.eb.pulse.crawler

import com.eb.pulse.crawler.service.NetworthService
import com.eb.schedule.dao.NetWorthRepositoryImpl
import com.eb.schedule.utils.HttpUtils

/**
  * Created by Egor on 20.04.2017.
  */
trait Lookup {

  private val netWorthRepository = new NetWorthRepositoryImpl
  val netWorthService = new NetworthService(netWorthRepository)

  val httpUtils = new HttpUtils

}
