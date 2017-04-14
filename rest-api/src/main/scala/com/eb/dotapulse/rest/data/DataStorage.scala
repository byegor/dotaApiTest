package com.eb.dotapulse.rest.data

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._

/**
  * Created by Egor on 14.04.2017.
  */
object DataStorage {

  private var currentGames: String = ""
  private var currentMatches: ConcurrentHashMap[Long, String] = new ConcurrentHashMap[Long, String]()
  private var matchesByGames: ConcurrentHashMap[Int, String] = new ConcurrentHashMap[Int, String]()

  def setData(data: Data): Unit = {
    this.currentGames = data.currentGames
    this.currentMatches = new ConcurrentHashMap[Long, String](mapAsJavaMap(data.currentMatches))
    this.matchesByGames = new ConcurrentHashMap[Int, String](mapAsJavaMap(data.matchesByGames))
  }

  def getCurrentGames: String = currentGames

  def getMatchById(matchId: Long): String = currentMatches.get(matchId, "")

  def getMatchesByGameId(gameId: Int): String =  matchesByGames.getOrDefault(gameId, "")
}
