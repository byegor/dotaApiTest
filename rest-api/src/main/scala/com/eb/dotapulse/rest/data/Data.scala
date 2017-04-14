package com.eb.dotapulse.rest.data

/**
  * Created by Egor on 14.04.2017.
  */
case class Data(currentGames: String, currentMatches: Map[Long, String], matchesByGames: Map[Int, String])
