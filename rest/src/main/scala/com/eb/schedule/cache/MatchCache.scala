package com.eb.schedule.cache

import java.util.concurrent.TimeUnit

import com.eb.schedule.{MatchDTO, MatchProcessor}
import com.eb.schedule.dto.LeagueDTO
import com.eb.schedule.exception.CacheItemNotFound
import com.eb.schedule.model.services.{LeagueService, UpdateTaskService}
import com.eb.schedule.model.slick.{League, UpdateTask}
import com.eb.schedule.shared.bean.Match
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 26.03.2016.
  */
class MatchCache @Inject()(matchProcessor: MatchProcessor) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val cache: LoadingCache[Long, Match] = CacheBuilder.newBuilder()
    .expireAfterAccess(4, TimeUnit.HOURS)
    .maximumSize(100)
    .build(new CacheLoader[Long, Match]() {
      def load(mathcId: Long): Match = {
        val matchDto: Option[MatchDTO] = matchProcessor.findMatch(mathcId)
        if (matchDto.isDefined) {
          matchDto.get.toMatch()
        } else {
          throw new CacheItemNotFound
        }
      }
    }).asInstanceOf[LoadingCache[Long, Match]]

  def getMatch(id: Long): Option[Match] = {
    try {
      Some(cache.get(id))
    } catch {
      case e: Exception =>
        if (e.getCause.isInstanceOf[CacheItemNotFound]) {

        } else {
          log.error("couldn't get match from cache: ", e)
        }
        None
    }
  }
}
