package com.eb.dotapulse.bot.data

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._

/**
  * Created by Egor on 14.04.2017.
  */
object DataStorage {

  private var currentGames: String = ""
  private var currentMatches: java.util.Map[String, String] = new ConcurrentHashMap[String, String]()
  private var matchesByGames: java.util.Map[String, String] = new ConcurrentHashMap[String, String]()

  def setData(data: Data): Unit = {
    this.currentGames = data.currentGames
    this.currentMatches = new ConcurrentHashMap[String, String](mapAsJavaMap(data.currentMatches))
    this.matchesByGames = new ConcurrentHashMap[String, String](mapAsJavaMap(data.matchesByGames))
  }

  def getCurrentGames: String = currentGames

  def getMatchById(matchId: String): String = currentMatches.getOrDefault(matchId, "")

  def getMatchesByGameId(gameId: String): String =  matchesByGames.getOrDefault(gameId, "")
}
