package com.eb.schedule.model.slick

import java.sql.Timestamp

import slick.jdbc.MySQLProfile.api._
import slick.lifted.{Rep, Tag}

/**
  * Created by Egor on 23.03.2016.
  */
case class MatchSeries(scheduledGameId: Int, matchId: Long, gameNumber: Byte, radiantWin:Option[Boolean], finished:Boolean, radiantTeam:Int, startDate:Timestamp)

object MatchSeries {

  class MatchSeriesTable(_tableTag: Tag) extends Table[MatchSeries](_tableTag, "match_series") {
    def * = (scheduledGameId, matchId, gameNumber, radiantWin, finished, radiantTeam, startDate) <>((MatchSeries.apply _).tupled, MatchSeries.unapply)
    def ? = (Rep.Some(scheduledGameId), Rep.Some(matchId), Rep.Some(gameNumber), Rep.Some(radiantWin), Rep.Some(finished), Rep.Some(radiantTeam), Rep.Some(startDate)).shaped.<>({ r => import r._; _1.map(_ => MatchSeries.apply(_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    val scheduledGameId: Rep[Int] = column[Int]("scheduled_game_id")
    val matchId: Rep[Long] = column[Long]("match_id")
    val gameNumber: Rep[Byte] = column[Byte]("game_number")
    val radiantWin: Rep[Option[Boolean]] = column[Option[Boolean]]("radiant_win")
    val finished: Rep[Boolean] = column[Boolean]("finished")
    val radiantTeam: Rep[Int] = column[Int]("radiant_team")
    val startDate:Rep[Timestamp] = column[Timestamp]("start_date", O.Default(new Timestamp(System.currentTimeMillis())))

    lazy val scheduledGamesFk = foreignKey("FK_match_series_scheduled_games", scheduledGameId, ScheduledGame.table)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
    val index1 = index("match_id", matchId, unique = true)
  }

  val table = TableQuery[MatchSeriesTable]

}