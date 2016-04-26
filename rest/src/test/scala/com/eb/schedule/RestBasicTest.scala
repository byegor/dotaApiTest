package com.eb.schedule

import com.eb.schedule.config.RestModule
import com.eb.schedule.configure.{CoreModule, H2Module}
import com.eb.schedule.live.LiveGameHelper
import com.eb.schedule.model.BasicTest
import com.google.inject.Guice

/**
  * Created by Egor on 10.04.2016.
  */
class RestBasicTest extends BasicTest{

  val restInjector = Guice.createInjector(new RestModule, new CoreModule, new H2Module)


  val liveGameHelper: LiveGameHelper = restInjector.getInstance(classOf[LiveGameHelper])

}
