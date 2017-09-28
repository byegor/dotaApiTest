package com.eb.pulse.crawler.data.service

import com.eb.schedule.dao.HeroRepositoryImpl
import com.eb.schedule.model.slick.Hero

/**
  * Created by Iegor.Bondarenko on 01.05.2017.
  */
class HeroService(repository: HeroRepositoryImpl) {

  def findAll() = {
    repository.findAll()
  }

  def insertHero(hero:Hero)={
    repository.insert(hero)
  }
}
