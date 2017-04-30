package com.eb.schedule.model.slick

import slick.jdbc.MySQLProfile.api._
import slick.lifted.{Rep, Tag}

/**
  * Created by Egor on 23.03.2016.
  */
case class NetWorth(matchId: Long, netWorth: String)

object NetWorth {

  class NetWorthTable(_tableTag: Tag) extends Table[NetWorth](_tableTag, "net_worth") {
    def * = (matchId, netWorth) <>((NetWorth.apply _).tupled, NetWorth.unapply)

    def ? = (Rep.Some(matchId), Rep.Some(netWorth)).shaped.<>({ r => import r._; _1.map(_ => NetWorth.apply(_1.get, _2.get)) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    val matchId: Rep[Long] = column[Long]("match_id")
    val netWorth: Rep[String] = column[String]("net_worth", O.Length(2000, varying = true))
  }

  val table = TableQuery[NetWorthTable]
}
