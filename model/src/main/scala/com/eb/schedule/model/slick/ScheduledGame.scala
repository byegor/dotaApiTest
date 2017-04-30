package com.eb.schedule.model.slick

import java.sql.Timestamp

import slick.jdbc.MySQLProfile.api._
import slick.lifted.{Rep, Tag}

/**
  * Created by Egor on 23.03.2016.
  */
case class ScheduledGame(id: Int, radiant: Int, dire: Int, leagueId: Int, seriesType: Byte, startDate: Timestamp = new Timestamp(System.currentTimeMillis()), status: Byte = 1)

object ScheduledGame {

  class ScheduledGameTable(_tableTag: Tag) extends Table[ScheduledGame](_tableTag, "scheduled_games") {
    def * = (id, radiant, dire, leagueId, seriesType, startDate, status) <>((ScheduledGame.apply _).tupled, ScheduledGame.unapply)

    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val radiant: Rep[Int] = column[Int]("radiant")
    val dire: Rep[Int] = column[Int]("dire")
    val leagueId: Rep[Int] = column[Int]("league_id")
    val seriesType: Rep[Byte] = column[Byte]("series_type")
    val startDate: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("start_date")
    val status: Rep[Byte] = column[Byte]("status", O.Default(0))
  }

  val table = TableQuery[ScheduledGameTable]
}