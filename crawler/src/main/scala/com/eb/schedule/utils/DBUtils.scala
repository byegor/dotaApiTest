package com.eb.schedule.utils

import java.sql._

import com.eb.schedule.model.TaskResult
import egor.dota.model.entity._
import org.apache.commons.dbutils.DbUtils
import org.slf4j.LoggerFactory


object DBUtils {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val JDBC_DRIVER: String = "com.mysql.jdbc.Driver"
  private val DB_URL: String = "jdbc:mysql://localhost/schedule"
  private val USER: String = "root"
  private val PASS: String = "q1w2e3r4"


  private val INSERT_UPDATE_TASK = "INSERT update_task (id, classname, result) values (?,?,?);"
  private val UPDATE_RESULT_IN_UPDATE_TASK = "UPDATE update_task set result = ? where id = ? and classname=?;"
  private val GET_UPDATE_TASK_BY_TYPE = "select * from update_task where classname = '%s' and result = 0;"

  private val GET_TEAM_BY_ID = "select * from team where id = %s;"
  private val INSERT_TEAM = "INSERT team (id, name, tag) values (?,?,?);"
  private val UPDATE_TEAM = "UPDATE team set name = ?, tag = ?) where id = ?;"

  private val GET_LEAGUE_BY_ID = "select * from league where id = %s;"
  private val INSERT_LEAGUE = "INSERT league (id, name, description, url) values (?, ?, ?, ?);"

  private val INSERT_GAME = "insert games (radiant, dire, league_id, match_id, status, start_date) values (?, ?, ?, ?, ?, ?);"
  private val GET_NOT_FINISHED_GAME = "select * from games where (status <> 2) and ((match_id = ?) or (radiant = ? and dire = ?) or (radiant = ? and dire = ?)) order by start_date desc"
  private val UPDATE_STATUS_GAME = "update games set match_id = ?, status = 1 where id = ?"

  private val INSERT_MATCH = "insert match_details (match_id, radiant, dire, league_id, type, radiant_win, game) values (?, ?, ?, ?, ?, ?, ?);"


  Class.forName(JDBC_DRIVER)


  @throws(classOf[SQLException])
  def getConnection: Connection = {
    DriverManager.getConnection(DB_URL, USER, PASS)
  }


  def createUpdateTask(id: Int, className: String): Unit = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    try {
      connection = getConnection
      statement = connection.prepareStatement(INSERT_UPDATE_TASK)
      statement.setInt(1, id)
      statement.setString(2, className)
      statement.setInt(3, 0)
      statement.executeUpdate()
    } catch {
      case e: SQLException => log.error("Couldn't create update task for id: " + id + " classname: " + className, e)
    } finally {
      DbUtils.close(statement)
      DbUtils.close(connection)

    }
  }

  def updateResultInTask(id: Int, className: String, result: TaskResult): Unit = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    try {
      connection = getConnection
      statement = connection.prepareStatement(UPDATE_RESULT_IN_UPDATE_TASK)
      statement.setInt(1, result.status)
      statement.setInt(2, id)
      statement.setString(3, className)
      statement.executeUpdate()
    } catch {
      case e: SQLException => log.error("Couldn't  update result for task id: " + id + " classname: " + className, e)
    } finally {
      DbUtils.close(statement)
      DbUtils.close(connection)

    }
  }

  def getPendingUpdateTasks(className: String): List[(Int, String)] = {
    var connection: Connection = null
    var statement: Statement = null
    var tasks: List[(Int, String)] = Nil
    try {
      connection = getConnection
      statement = connection.createStatement
      val resultSet: ResultSet = statement.executeQuery(GET_UPDATE_TASK_BY_TYPE.format(className))
      while (resultSet.next) {
        val id: Int = resultSet.getInt("id")
        val name: String = resultSet.getString("classname")
        tasks ::=(id, className)
      }
    }
    catch {
      case e: SQLException => {
        log.error("Couldn't get update task for " + className, e)
      }
    } finally {
      DbUtils.close(statement)
      DbUtils.close(connection)
    }
    tasks
  }

  def getTeamById(teamId: Int): Option[TeamInfo] = {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val rs: ResultSet = statement.executeQuery(GET_TEAM_BY_ID.format(teamId))
      if (rs.next()) {
        Some(new TeamInfo(rs.getInt("id"), rs.getString("name"), rs.getString("tag")))
      } else {
        None
      }
    } catch {
      case e: SQLException => {
        log.error("Couldn't  get team " + teamId, e)
        None
      }
    } finally {
      DbUtils.close(statement)
      DbUtils.close(connection)
    }
  }

  def getLeagueById(leagueId: Int): Option[League] = {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val rs: ResultSet = statement.executeQuery(GET_LEAGUE_BY_ID.format(leagueId))
      if (rs.next()) {
        Some(new League(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getString("url")))
      } else {
        None
      }
    } catch {
      case e: SQLException => {
        log.error("Couldn't  get league " + leagueId, e)
        None
      }
    } finally {
      DbUtils.close(statement)
      DbUtils.close(connection)
    }
  }

  def saveLeague(league: League) {
    var connection: Connection = null
    var statement: PreparedStatement = null
    try {
      connection = getConnection
      statement = connection.prepareStatement(INSERT_LEAGUE)
      statement.setInt(1, league.id)
      statement.setString(2, league.name)
      statement.setString(3, league.dscr)
      statement.setString(4, league.url)
      statement.execute()
    } catch {
      case e: SQLException => {
        log.error("Couldn't save league " + league, e)
      }
    } finally {
      DbUtils.close(statement)
      DbUtils.close(connection)
    }
  }

  def updateOrCreateTeamInfo(temInfo: TeamInfo): Unit = {
    var connection: Connection = null
    var statement: Statement = null
    var ps: PreparedStatement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val resultSet: ResultSet = statement.executeQuery(GET_TEAM_BY_ID.format(temInfo.id))

      if (resultSet.next()) {
        ps = connection.prepareStatement(UPDATE_TEAM)
        ps.setString(1, temInfo.name)
        ps.setString(2, temInfo.tag)
        ps.setInt(3, temInfo.id)
      } else {
        ps = connection.prepareStatement(INSERT_TEAM)
        ps.setInt(1, temInfo.id)
        ps.setString(2, temInfo.name)
        ps.setString(3, temInfo.tag)
      }
      ps.executeUpdate()
    }
    catch {
      case e: SQLException => {
        log.error("Couldn't  update team " + temInfo, e)
      }
    } finally {
      DbUtils.close(statement)
      DbUtils.close(ps)
      DbUtils.close(connection)
    }
  }

  def getNotFinishedMatchByCriteria(matchId: Long, radiantId: Int, direId: Int): Option[Game] = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    try {
      connection = getConnection
      statement = connection.prepareStatement(GET_NOT_FINISHED_GAME)
      statement.setLong(1, matchId)
      statement.setInt(2, radiantId)
      statement.setInt(3, direId)
      statement.setInt(4, direId)
      statement.setInt(5, radiantId)
      val resultSet: ResultSet = statement.executeQuery()
      while (resultSet.next) {
        val date: Date = resultSet.getDate("start_date")
        val leagueId: Int = resultSet.getInt("league_id")
        val status: Int = resultSet.getInt("status")
        val radiant: Int = resultSet.getInt("radiant")
        val dire: Int = resultSet.getInt("dire")
        val id: Int = resultSet.getInt("id")
        return Some(new Game(id, radiant, dire, matchId, leagueId, date, MatchStatus.fromValue(status)))
      }
    }
    catch {
      case e: SQLException => {
        log.error("Couldn't get game for match_id" + matchId, e)
      }
    } finally {
      DbUtils.close(statement)
      DbUtils.close(connection)
    }
    None
  }

  def updateGameStatus(game: Game): Unit = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    try {
      connection = getConnection
      statement = connection.prepareStatement(UPDATE_STATUS_GAME)
      statement.setLong(1, game.matchId)
      statement.setInt(2, game.id)
      statement.executeUpdate()
    } catch {
      case e: SQLException => {
        log.error("Couldn't  update game status" + game, e)
      }
    } finally {
      DbUtils.close(statement)
      DbUtils.close(connection)
    }
  }

  def saveLiveGame(matchDetails: Match): Unit = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    try {
      connection = getConnection
      statement = connection.prepareStatement(INSERT_GAME)
      statement.setInt(1, matchDetails.radiantId)
      statement.setInt(2, matchDetails.direId)
      statement.setInt(3, matchDetails.leagueid)
      statement.setLong(4, matchDetails.matchId)
      statement.setInt(5, MatchStatus.LIVE.status)
      statement.setDate(6, new Date(System.currentTimeMillis()))
      statement.executeUpdate()
    } catch {
      case e: SQLException => {
        log.error("Couldn't  save live game " + matchDetails, e)
      }
    } finally {
      DbUtils.close(statement)
      DbUtils.close(connection)
    }
  }

  def saveMatchDetails(mtch: Match) {
    var connection: Connection = null
    var statement: PreparedStatement = null
    try {
      connection = getConnection
      statement = connection.prepareStatement(INSERT_MATCH)
      statement.setLong(1, mtch.matchId)
      statement.setInt(2, mtch.radiantId)
      statement.setInt(3, mtch.direId)
      statement.setInt(4, mtch.leagueid)
      statement.setInt(5, mtch.seriesType)
      statement.setInt(6, mtch.radiantWin)
      statement.setInt(7, mtch.gameNumber)
      statement.execute()
    } catch {
      case e: SQLException => {
        log.error("Couldn't save league " + mtch, e)
      }
    } finally {
      DbUtils.close(statement)
      DbUtils.close(connection)
    }
  }
}
