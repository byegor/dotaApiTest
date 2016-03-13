package com.eb.schedule.model.slick

import java.sql.Timestamp

import com.eb.schedule.model.MatchStatus
import slick.lifted.{Rep, Tag}
import slick.driver.MySQLDriver.api._

/**
  * Created by Egor on 13.02.2016.
  */

case class League(id: Int, name: String, description: Option[String] = None, url: Option[String] = None)

class Leagues(tag: Tag) extends Table[League](tag, "league") {
  def * = (id, name, description, url) <>(League.tupled, League.unapply)

  def ? = (Rep.Some(id), Rep.Some(name), description, url).shaped.<>({ r => import r._; _1.map(_ => League.tupled((_1.get, _2.get, _3, _4))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

  val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
  val name: Rep[String] = column[String]("name", O.Length(250, varying = true))
  val description: Rep[Option[String]] = column[Option[String]]("description", O.Length(400, varying = true), O.Default(None))
  val url: Rep[Option[String]] = column[Option[String]]("url", O.Length(100, varying = true), O.Default(None))
}


case class Team(id: Int, name: String, tag: String)

class Teams(_tableTag: Tag) extends Table[Team](_tableTag, "team") {
  def * = (id, name, tag) <>(Team.tupled, Team.unapply)

  def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(tag)).shaped.<>({ r => import r._; _1.map(_ => Team.tupled((_1.get, _2.get, _3.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

  val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
  val name: Rep[String] = column[String]("name", O.Length(50, varying = true))
  val tag: Rep[String] = column[String]("tag", O.Length(20, varying = true))
}


case class UpdateTask(id: Long, classname: String, result: Byte)

class UpdateTasks(_tableTag: Tag) extends Table[UpdateTask](_tableTag, "update_task") {
  def * = (id, classname, result) <>(UpdateTask.tupled, UpdateTask.unapply)

  val id: Rep[Long] = column[Long]("id")
  val classname: Rep[String] = column[String]("classname", O.Length(150, varying = true))
  val result: Rep[Byte] = column[Byte]("result", O.Default(0))

  val index1 = index("id_classname_select", (id, classname))
}


case class LiveGame(matchId: Long, radiant: Int, dire: Int, leagueId: Int, seriesType: Byte, var startDate: java.sql.Timestamp, radiantWin: Byte, game: Byte)

class LiveGames(_tableTag: Tag) extends Table[LiveGame](_tableTag, "live_games") {
  def * = (matchId, radiant, dire, leagueId, seriesType, startDate, radiantWin, game) <> (LiveGame.tupled, LiveGame.unapply)

  val matchId: Rep[Long] = column[Long]("match_id", O.PrimaryKey)
  val radiant: Rep[Int] = column[Int]("radiant")
  val dire: Rep[Int] = column[Int]("dire")
  val leagueId: Rep[Int] = column[Int]("league_id")
  val seriesType: Rep[Byte] = column[Byte]("series_type")
  val startDate: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("start_date")
  val radiantWin: Rep[Byte] = column[Byte]("radiant_win")
  val game: Rep[Byte] = column[Byte]("game")

  val team = new TableQuery(tag => new Teams(tag))
  val league = new TableQuery(tag => new Leagues(tag))

  lazy val leagueFk = foreignKey("FK3_league", leagueId, league)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  lazy val teamFk2 = foreignKey("FK1_team1", radiant, team)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  lazy val teamFk3 = foreignKey("FK2_team2", dire, team)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
}


case class MatchSeries(matchId: Long, radiant: Int, dire: Int, leagueId: Int, radiantWin: Boolean, parent: Option[Long] = None, seriesType: Byte, radiantScore: Byte, direScore: Byte)

class MatchSeriesTable(_tableTag: Tag) extends Table[MatchSeries](_tableTag, "match_series") {
  def * = (matchId, radiant, dire, leagueId, radiantWin, parent, seriesType, radiantScore, direScore) <> (MatchSeries.tupled, MatchSeries.unapply)

  val matchId: Rep[Long] = column[Long]("match_id", O.PrimaryKey)
  val radiant: Rep[Int] = column[Int]("radiant")
  val dire: Rep[Int] = column[Int]("dire")
  val leagueId: Rep[Int] = column[Int]("league_id")
  val radiantWin: Rep[Boolean] = column[Boolean]("radiant_win")
  val parent: Rep[Option[Long]] = column[Option[Long]]("parent", O.Default(None))
  val seriesType: Rep[Byte] = column[Byte]("series_type")
  val radiantScore: Rep[Byte] = column[Byte]("radiant_score")
  val direScore: Rep[Byte] = column[Byte]("dire_score")

//  val matchSeries = new TableQuery(tag => new MatchSeries(tag))
  val team = new TableQuery(tag => new Teams(tag))
  val league = new TableQuery(tag => new Leagues(tag))


  lazy val leagueFk = foreignKey("FK_league_match", leagueId, league)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  lazy val teamFk2 = foreignKey("FK_dire_team_match", dire, team)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  lazy val teamFk3 = foreignKey("FK_radiant_team_match", radiant, team)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
}

case class Pick(matchId: Long, heroId: Int, radiant: Boolean, pick: Boolean)

class Picks(_tableTag: Tag) extends Table[Pick](_tableTag, "picks") {
  def * = (matchId, heroId, radiant, pick) <>(Pick.tupled, Pick.unapply)

  val matchId: Rep[Long] = column[Long]("match_id")
  val heroId: Rep[Int] = column[Int]("hero_id")
  val radiant: Rep[Boolean] = column[Boolean]("radiant")
  val pick: Rep[Boolean] = column[Boolean]("pick")
}

case class ScheduledGame(id:Int, matchId: Option[Long] = None, radiant: Int, dire: Int, leagueId: Int, startDate:Timestamp = new Timestamp(System.currentTimeMillis()), status: Byte = 0, radiantScore: Byte = 0, direScore: Byte = 0)

class ScheduledGames(_tableTag: Tag) extends Table[ScheduledGame](_tableTag, "scheduled_games") {
  def * = (id, matchId, radiant, dire, leagueId, startDate, status, radiantScore, direScore) <> (ScheduledGame.tupled, ScheduledGame.unapply)

  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val matchId: Rep[Option[Long]] = column[Option[Long]]("match_id", O.Default(None))
  val radiant: Rep[Int] = column[Int]("radiant")
  val dire: Rep[Int] = column[Int]("dire")
  val leagueId: Rep[Int] = column[Int]("league_id")
  val startDate: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("start_date")
  val status: Rep[Byte] = column[Byte]("status", O.Default(0))
  val radiantScore: Rep[Byte] = column[Byte]("radiant_score", O.Default(0))
  val direScore: Rep[Byte] = column[Byte]("dire_score", O.Default(0))

  val team = new TableQuery(tag => new Teams(tag))
  val league = new TableQuery(tag => new Leagues(tag))

  lazy val leagueFk = foreignKey("FK3_league_sched", leagueId, league)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  lazy val teamFk2 = foreignKey("FK1_radiant_sched", radiant, team)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  lazy val teamFk3 = foreignKey("FK2_dire_sched", dire, team)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
}




