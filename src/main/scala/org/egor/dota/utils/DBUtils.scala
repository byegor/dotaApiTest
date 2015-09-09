package org.egor.dota.utils

import java.sql._

import org.egor.dota.entity._

import scala.StringBuilder


/**
 * Created by Егор on 13.07.2015.
 */
object DBUtils {


  private val JDBC_DRIVER: String = "com.mysql.jdbc.Driver"
  private val DB_URL: String = "jdbc:mysql://localhost/dota"
  private val USER: String = "root"
  private val PASS: String = "q1w2e3r4"
  private val HERO_INSERT: String = "INSERT HERO (id, name) values(%s, '%s');"
  private val HERO_GET_ALL: String = "SELECT * FROM HERO;"
  private val TEAM_GET_BY_CODE: String = "SELECT * FROM TEAM WHERE CODE='%s';"
  private val TEAM_INSERT: String = "INSERT TEAM (code) values('%s');"
  private val TEAM_FIGHTS_INSERT: String = "INSERT team_fights (winner, looser, match_id) values(%s, %s, %s);"
  private val PROGRESS_INSERT: String = "INSERT PROGRESS (value) values(%s);"
  private val PROGRESS_GET_LAST: String = "SELECT * FROM PROGRESS ORDER BY id DESC LIMIT 1"

  private val TRASH_GET_MATCH_PROGRESS: String = "select t.match_id from team_fights t left join match_progress m on ( t.match_id = m.match_id ) where m.match_id is null limit 1;"
  private val GET_UNPROGRESS_MATCH: String = "SELECT u.match_id FROM UNPROCESSED_MATCH u where not exists (select 1 from match_progress p where p.match_id = u.match_id) LIMIT 1;"
  private val INSERT_MATCH_PROGRESS: String = "INSERT MATCH_PROGRESS (match_id) values(%s);"
  private val INSERT_UNPROCESSED_MATCH_PROGRESS: String = "INSERT unprocessed_match (match_id) values(%s);"
  private val DELETE_UNPROCESSED_MATCH_PROGRESS: String = "DELETE FROM unprocessed_match where match_id = %s;"

  private val GET_USER_FOR_PROCESS: String = "SELECT user_id FROM USER_PROGRESS ORDER BY last_match_processed LIMIT 1;"
  private val GET_USER_BY_IDS: String = "SELECT user_id FROM USER_PROGRESS WHERE user_id in (%s);"
  private val INSERT_USER_PROGRESS: String = "INSERT USER_PROGRESS (user_id) values(%s);"
  private val UPDATE_USER_PROGRESS: String = "UPDATE USER_PROGRESS set last_match_processed = %s where user_id = %s;"

  Class.forName(JDBC_DRIVER)


  @throws(classOf[SQLException])
  def getConnection: Connection = {
    DriverManager.getConnection(DB_URL, USER, PASS)
  }

  def insertHeroes(heroes: Seq[Hero]) {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      for (hero <- heroes) {
        statement.addBatch(HERO_INSERT.format(hero.id, hero.name))
      }
      statement.executeBatch
    } catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close()
        }
        if (statement != null) {
          statement.close()
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def insertMatchResults(results: Seq[MatchResult], lastMatchId: Long) {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      for (matchResult <- results) {
        val winner: Int = if (matchResult.radiantWin) matchResult.radiant.id else matchResult.dire.id
        val looser: Int = if (matchResult.radiantWin) matchResult.dire.id else matchResult.radiant.id
        statement.addBatch(TEAM_FIGHTS_INSERT.format(winner, looser, matchResult.id))
      }
      statement.addBatch(PROGRESS_INSERT.format(lastMatchId))
      statement.executeBatch
    }
    catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def getHeroes: List[Hero] = {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      var heroes: List[Hero] = Nil
      val resultSet: ResultSet = statement.executeQuery(HERO_GET_ALL)
      while (resultSet.next) {
        val id: Int = resultSet.getInt("id")
        val name: String = resultSet.getString("name")
        heroes ::= new Hero(id, name)
      }
      return heroes
    }
    catch {
      case e: SQLException => throw new RuntimeException(e)
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def getTeamIdByCode(code: String): Int = {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val resultSet: ResultSet = statement.executeQuery(String.format(TEAM_GET_BY_CODE, code))
      val teamId = if (!resultSet.next) -1 else resultSet.getInt("id")
      teamId
    }
    catch {
      case e: SQLException => throw new RuntimeException(e)
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def createTeam(code: String): Int = {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val teamId: Int = statement.executeUpdate(String.format(TEAM_INSERT, code), Statement.RETURN_GENERATED_KEYS)
      teamId
    }
    catch {
      case e: SQLException => {
        throw new RuntimeException(e)
      }
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def getLatRecordedMatchId(jobId: Int): Long = {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val resultSet: ResultSet = statement.executeQuery(PROGRESS_GET_LAST)
      if (!resultSet.next) {
        return -1
      }
      else {
        return resultSet.getLong("value")
      }
    }
    catch {
      case e: SQLException => {
        e.printStackTrace
        throw new RuntimeException(e)
      }
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {
        }
      }
    }
  }

  def getUnprocessedMatchId(): Option[Long] = {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val resultSet: ResultSet = statement.executeQuery(GET_UNPROGRESS_MATCH)
      if (!resultSet.next) {
        None
      }
      else {
        Some(resultSet.getLong("match_id"))
      }
    }
    catch {
      case e: SQLException => {
        throw new RuntimeException(e)
      }
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {
        }
      }
    }
  }

  def getUnsavedUsers(accounts: List[Int]): List[Int] = {
    var filteredUsers: Set[Int] = accounts.filter(x => x != -1).toSet
    val queryParameters: String = filteredUsers.mkString(",")
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val resultSet: ResultSet = statement.executeQuery(GET_USER_BY_IDS.format(queryParameters))
      while (resultSet.next()) {
        val userId: Int = resultSet.getInt("user_id")
        filteredUsers = filteredUsers.filter(x => x != userId)
      }
      filteredUsers.toList
    } catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def getUserForProcess(): Option[Int] = {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val resultSet: ResultSet = statement.executeQuery(GET_USER_FOR_PROCESS)
      if (resultSet.next()) {
        Some(resultSet.getInt("user_id"))
      } else {
        None
      }
    } catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def insertUsers(users: List[Int]) {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement

      val unsavedUsers: List[Int] = getUnsavedUsers(users)
      for (id <- unsavedUsers) {
        statement.addBatch(INSERT_USER_PROGRESS.format(id))
      }
      statement.executeBatch
    }
    catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def updateUserProgress(userId: Int, lastMatch: Long) {
    if (lastMatch != -1) {
      var connection: Connection = null
      var statement: Statement = null
      try {
        connection = getConnection
        statement = connection.createStatement
        statement.executeUpdate(UPDATE_USER_PROGRESS.format(lastMatch, userId))
      }
      catch {
        case e: SQLException => throw e
      } finally {
        try {
          if (connection != null) {
            connection.close
          }
          if (statement != null) {
            statement.close
          }
        }
        catch {
          case ignore: SQLException => {}
        }
      }
    }
  }

  def insertMatches(matches: List[Long]) {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement

      for (id <- matches) {
        statement.addBatch(INSERT_UNPROCESSED_MATCH_PROGRESS.format(id))
      }
      statement.executeBatch
    }
    catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def matchProcessed(matchDetails: MatchResult) {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val winner: Int = if (matchDetails.radiantWin) matchDetails.radiant.id else matchDetails.dire.id
      val looser: Int = if (matchDetails.radiantWin) matchDetails.dire.id else matchDetails.radiant.id

      statement.addBatch(TEAM_FIGHTS_INSERT.format(winner, looser, matchDetails.id))
      statement.addBatch(INSERT_MATCH_PROGRESS.format(matchDetails.id))
      statement.addBatch(DELETE_UNPROCESSED_MATCH_PROGRESS.format(matchDetails.id))
      statement.executeBatch
    }
    catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def getUnsavedMatches(matches: List[Int]): List[Int] = {
    val queryParameters: String = matches.map("'" + _ + "'").mkString(",")
    var filteredUsers: List[Int] = matches
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val resultSet: ResultSet = statement.executeQuery(GET_USER_BY_IDS.format(queryParameters))
      while (resultSet.next()) {
        val userId: Int = resultSet.getInt("user_id")
        filteredUsers = filteredUsers.filter(x => x != userId)
      }
      filteredUsers.filter(x => x != -1)
    } catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def matchIdProcessed(matchId: Long) {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      statement.addBatch(INSERT_MATCH_PROGRESS.format(matchId))
      statement.addBatch(DELETE_UNPROCESSED_MATCH_PROGRESS.format(matchId))
      statement.executeBatch()
    }
    catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def deleteUnprocessedMatch(matchId: Long) {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      statement.executeUpdate(DELETE_UNPROCESSED_MATCH_PROGRESS.format(matchId))
    }
    catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

  def getMaxTeamId(): Int = {
    var connection: Connection = null
    var statement: Statement = null
    try {
      connection = getConnection
      statement = connection.createStatement
      val rs: ResultSet = statement.executeQuery("SELECT max(team_id) FROM TEAMS")
      if (rs.next()) rs.getInt("team_id") else -1
    }
    catch {
      case e: SQLException => throw e
    } finally {
      try {
        if (connection != null) {
          connection.close
        }
        if (statement != null) {
          statement.close
        }
      }
      catch {
        case ignore: SQLException => {}
      }
    }
  }

}
