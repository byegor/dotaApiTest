package com.eb.pulse.crawler.utils

import java.io.File
import java.net.URL

import com.eb.pulse.crawler.Lookup
import com.eb.pulse.crawler.service.HeroService
import com.eb.schedule.model.slick.Hero

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.sys.process._

/**
  * Created by Egor on 24.06.2016.
  */
object HeroImageGrabber extends App {

  private val heroServiceImpl: HeroService = Lookup.heroService
  private val heroes: Seq[Hero] = Await.result(heroServiceImpl.findAll(), Duration.Inf).filter(hero => hero.id != 0)
  for (hero <- heroes) {
    val escapedName = hero.name.replaceAll(" ", "_")
    fileDownloader("http://cdn.dota2.com/apps/dota2/images/heroes/" + escapedName + "_full.png", escapedName + ".png")
  }


  def fileDownloader(url: String, filename: String) = {
    new URL(url) #> new File(filename) !!
  }
}
