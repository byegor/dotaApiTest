package org.egor.dota.utils

import java.sql._

import org.egor.dota.entity._


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
  private val PROGRESS_INSERT: String = "INSERT PROGRESS (value, job_id) values(%s, %s);"
  private val PROGRESS_GET_LAST: String = "SELECT * FROM PROGRESS WHERE job_id = %s ORDER BY id DESC LIMIT 1"

  Class.forName(JDBC_DRIVER)


  @throws(classOf[SQLException])
  private def getConnection: Connection = {
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

  def insertMatchResults(results: Seq[MatchResult], lastMatchId: Long, jobId: Int) {
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
      statement.addBatch(PROGRESS_INSERT.format(lastMatchId, jobId))
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
      val resultSet: ResultSet = statement.executeQuery(PROGRESS_GET_LAST.format(jobId))
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

}
