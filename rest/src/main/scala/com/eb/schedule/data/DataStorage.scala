package com.eb.schedule.data


import java.util.concurrent.ConcurrentHashMap

/**
  * Created by Egor on 01.04.2017.
  */
object DataStorage {

  private var currentGames: String = ""
  private var currentMatches: ConcurrentHashMap[String, String] = new ConcurrentHashMap[String, String]()
  private var matchesByGames: ConcurrentHashMap[String, String] = new ConcurrentHashMap[String, String]()




  def setCurrentGames(currentGames: String, currentMatches: ConcurrentHashMap[String, String], matchesByGames: ConcurrentHashMap[String, String]): Unit = {
    this.currentMatches = currentMatches
    this.currentGames = currentGames
    this.matchesByGames = matchesByGames
  }

  def getCurrentGames = currentGames

  def getMatchById(matchId: String) = currentMatches.get(matchId)

  def getMatchesByGameId(gameId: String) = matchesByGames.get(gameId)


}

