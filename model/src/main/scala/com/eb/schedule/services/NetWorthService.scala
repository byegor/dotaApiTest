package com.eb.schedule.services

import com.eb.schedule.dao.NetWorthRepository
import com.eb.schedule.dto.{DTOUtils, NetWorthDTO}
import com.google.inject.Inject

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
  def findByMatchId(id: Long): Future[Option[NetWorthDTO]] = {
    netWorthRepository.findByMatchId(id).map(DTOUtils.crateNetWorthDTO)
  }

  def insertOrUpdate(nw: NetWorthDTO): Unit = {
    netWorthRepository.insertOrUpdate(DTOUtils.transformNetWorthFromDTO(nw))
  }


}
