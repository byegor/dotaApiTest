package com.eb.schedule.services

import com.eb.schedule.dao.NetWorthRepository
import com.eb.schedule.dto.{DTOUtils, NetWorthDTO}
import com.eb.schedule.model.slick.NetWorth
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait NetWorthService {
  def findByMatchId(id: Long): Future[Option[NetWorthDTO]]

  def insertOrUpdate(nw: NetWorthDTO)
}

class NetWorthServiceImpl @Inject()(netWorthRepository: NetWorthRepository) extends NetWorthService {

  private val log = LoggerFactory.getLogger(this.getClass)

  def findByMatchId(id: Long): Future[Option[NetWorthDTO]] = {
    netWorthRepository.findByMatchId(id).map(DTOUtils.crateNetWorthDTO)
  }

  def insertOrUpdate(nw: NetWorthDTO): Unit = {
    val worth: NetWorth = DTOUtils.transformNetWorthFromDTO(nw)
    val future: Future[Boolean] = netWorthRepository.exists(worth.matchId)
    future.onSuccess {
      case exists => if (exists) netWorthRepository.update(worth) else netWorthRepository.insert(worth)
    }
    future.onFailure {
      case e => log.error("Couldn't update netWorth by id" + nw.matchId, e)
    }
  }


}
