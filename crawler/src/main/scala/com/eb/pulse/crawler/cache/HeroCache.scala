package com.eb.pulse.crawler.cache

import com.eb.pulse.crawler.service.HeroService
import com.eb.schedule.model.slick.Hero
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 26.03.2016.
  */
//todo download hero from api and ivalidate cache
class HeroCache (val heroService: HeroService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val cache: mutable.Map[Int, Hero] = {
    val heroes: Seq[Hero] = Await.result(heroService.findAll(), Duration.Inf)
    val heroMap: mutable.Map[Int, Hero] = new mutable.HashMap[Int, Hero]()
    heroes.foreach(h => heroMap.put(h.id, h))
    heroMap
  }

  def getHero(id: Int): Hero = {
    val option: Option[Hero] = cache.get(id)
    if (option.isDefined) {
      option.get
    } else {
      log.error("ALRAMAA### Couldn't find a hero: " + id)
      new Hero(id, "")
    }
  }


}
