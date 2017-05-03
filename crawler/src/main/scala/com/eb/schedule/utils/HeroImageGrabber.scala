package com.eb.schedule.utils

import java.io.File
import java.net.URL

import scala.sys.process._

/**
  * Created by Egor on 24.06.2016.
  */
object HeroImageGrabber extends App {

/*
todo
  val injector = Guice.createInjector(new MysqlModule, new CoreModule)
  private val heroServiceImpl: HeroServiceImpl = injector.getInstance(classOf[HeroServiceImpl])
  private val heroes: Seq[HeroDTO] = Await.result(heroServiceImpl.findAll(), Duration.Inf).filter(_.heroId != 0)
  for (hero <- heroes) {
    val escapedName = hero.name.replaceAll(" ", "_")
    fileDownloader("http://cdn.dota2.com/apps/dota2/images/heroes/" + escapedName + "_full.png", escapedName + ".png")
  }
*/

  def fileDownloader(url: String, filename: String) = {
    new URL(url) #> new File(filename) !!
  }
}
