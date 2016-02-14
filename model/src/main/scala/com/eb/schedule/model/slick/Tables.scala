package com.eb.schedule.model.slick

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

//  lazy val league = new TableQuery(tag => new Leagues(tag))


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


case class MatchDetails(matchId: Long, radiant: Int, dire: Int, leagueId: Int, `type`: Int, radiantWin: Byte, game: Byte)

class MatchDetailsTable(_tableTag: Tag) extends Table[MatchDetails](_tableTag, "match_details") {
  def * = (matchId, radiant, dire, leagueId, `type`, radiantWin, game) <>(MatchDetails.tupled, MatchDetails.unapply)

  def ? = (Rep.Some(matchId), Rep.Some(radiant), Rep.Some(dire), Rep.Some(leagueId), Rep.Some(`type`), Rep.Some(radiantWin), Rep.Some(game)).shaped.<>({ r => import r._; _1.map(_ => MatchDetails.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

  val matchId: Rep[Long] = column[Long]("match_id", O.PrimaryKey)
  val radiant: Rep[Int] = column[Int]("radiant")
  val dire: Rep[Int] = column[Int]("dire")
  val leagueId: Rep[Int] = column[Int]("league_id")
  val `type`: Rep[Int] = column[Int]("type")
  val radiantWin: Rep[Byte] = column[Byte]("radiant_win")
  val game: Rep[Byte] = column[Byte]("game")

  val team = new TableQuery(tag => new Teams(tag))
  val league = new TableQuery(tag => new Leagues(tag))

  lazy val leagueFk = foreignKey("FK3_league", leagueId, league)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  lazy val teamFk2 = foreignKey("FK1_team1", radiant, team)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  lazy val teamFk3 = foreignKey("FK2_team2", dire, team)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
}


case class Game(id: Int, radiant: Int, dire: Int, leagueId: Int, matchId: Option[Long] = None, startDate: Option[java.sql.Timestamp] = None, status: MatchStatus)

class Games(tag: Tag) extends Table[Game](tag, "games") {
  def * = (id, radiant, dire, leagueId, matchId, startDate, status) <>(Game.tupled, Game.unapply)

  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val radiant: Rep[Int] = column[Int]("radiant")
  val dire: Rep[Int] = column[Int]("dire")
  val leagueId: Rep[Int] = column[Int]("league_id")
  val matchId: Rep[Option[Long]] = column[Option[Long]]("match_id", O.Default(None))
  val startDate: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("start_date", O.Default(None))
  val status: Rep[MatchStatus] = column[MatchStatus]("status", O.Default(MatchStatus.SCHEDULED))

  val team = new TableQuery(tag => new Teams(tag))
  val league = new TableQuery(tag => new Leagues(tag))
  val matchDetails = new TableQuery(tag => new MatchDetailsTable(tag))

  lazy val leagueFk = foreignKey("FK3_league_sched", leagueId, league)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  lazy val matchDetailsFk = foreignKey("FK4_match_sched", matchId, matchDetails)(r => Rep.Some(r.matchId), onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  lazy val teamFk3 = foreignKey("FK1_radiant_sched", radiant, team)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  lazy val teamFk4 = foreignKey("FK2_dire_sched", dire, team)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)

  implicit val MatchStatusMapper = MappedColumnType.base[MatchStatus, Int](_.status, MatchStatus.fromValue(_))
}


