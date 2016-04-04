package com.eb.schedule.model.slick

import java.sql.Timestamp

import slick.driver.MySQLDriver.api._
import slick.lifted.{Rep, Tag}

/**
  * Created by Egor on 23.03.2016.
  */
case class ScheduledGame(id: Int, matchId: Option[Long] = None, radiant: Int, dire: Int, leagueId: Int, startDate: Timestamp, status: Byte = 0)

object ScheduledGame {

  class ScheduledGameTable(_tableTag: Tag) extends Table[ScheduledGame](_tableTag, "scheduled_games") {
    def * = (id, matchId, radiant, dire, leagueId, startDate, status) <>((ScheduledGame.apply _).tupled, ScheduledGame.unapply)

    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val matchId: Rep[Option[Long]] = column[Option[Long]]("match_id", O.Default(None))
    val radiant: Rep[Int] = column[Int]("radiant")
    val dire: Rep[Int] = column[Int]("dire")
    val leagueId: Rep[Int] = column[Int]("league_id")
    val startDate: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("start_date")
    val status: Rep[Byte] = column[Byte]("status", O.Default(0))

    lazy val leagueFk = foreignKey("FK3_league_sched", leagueId, League.table)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
    lazy val teamFk2 = foreignKey("FK1_radiant_sched", radiant, Team.table)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
    lazy val teamFk3 = foreignKey("FK2_dire_sched", dire, Team.table)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  }

  val table = TableQuery[ScheduledGameTable]
}