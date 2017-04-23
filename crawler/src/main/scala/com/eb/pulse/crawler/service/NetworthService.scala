package com.eb.pulse.crawler.service

import com.eb.schedule.dao.NetWorthRepository
import com.eb.schedule.model.slick.NetWorth
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 20.04.2017.
  */
class NetworthService(netWorthRepository: NetWorthRepository) extends Service {

  private val log = LoggerFactory.getLogger(this.getClass)

  def findByMatchId(id: Long): NetWorth = {
    val gold = Await.result(netWorthRepository.findByMatchId(id), timeout)
    gold match {
      case Some(nw) => nw
      case None => NetWorth(id, "")
    }
  }

  def insertOrUpdate(nw: NetWorth): Future[Option[NetWorth]] = {
    val future: Future[Option[NetWorth]] = netWorthRepository.findByMatchId(nw.matchId)
    future.onSuccess {
      case option => if (option.isDefined) {
        val netWorth = option.get
        val newNetWorth = new NetWorth(nw.matchId, netWorth.netWorth + "," + nw.netWorth)
        netWorthRepository.update(newNetWorth)
      } else netWorthRepository.insert(nw)
    }
    future.onFailure {
      case e => log.error("Couldn't update netWorth by id: " + nw, e)
    }
    future
  }
}
