package com.eb.pulse.crawler.service

import com.eb.schedule.dao.HeroRepositoryImpl

/**
  * Created by Iegor.Bondarenko on 01.05.2017.
  */
class HeroService(repository: HeroRepositoryImpl) {

  def findAll() = {
    repository.findAll()
  }
}
