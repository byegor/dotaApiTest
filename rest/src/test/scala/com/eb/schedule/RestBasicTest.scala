package com.eb.schedule


import com.eb.schedule.config.RestModule
import com.eb.schedule.configure.{CoreModule, H2Module}
import com.eb.schedule.live.LiveGameHelper
import com.eb.schedule.model.BasicTest
import com.eb.schedule.services.{DataProcessor, ScheduleRestService, ScheduledRestServiceImpl}
import com.google.inject.Guice

/**
  * Created by Egor on 10.04.2016.
  */
class RestBasicTest extends BasicTest{

  val restInjector = Guice.createInjector(new RestModule, new CoreModule, new H2Module)


  val liveGameHelper: LiveGameHelper = restInjector.getInstance(classOf[LiveGameHelper])
  val scheduledService:ScheduleRestService = restInjector.getInstance(classOf[ScheduledRestServiceImpl])
  val dataProcessor:DataProcessor = restInjector.getInstance(classOf[DataProcessor])

}
