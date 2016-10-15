package com.eb.schedule.shared.bean

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

/**
  * Created by Egor on 15.10.2016.
  */

case class GameBean(
                     @BeanProperty @JsonProperty("st") var startTime: Long,
                     @BeanProperty @JsonProperty("r") var radiant: TeamBean,
                     @BeanProperty @JsonProperty("d") var dire: TeamBean,
                     @BeanProperty @JsonProperty("l") var league: LeagueBean,
                     @BeanProperty @JsonProperty("bo") var seriesType: String,
                     @BeanProperty @JsonProperty("rw") var radiantWin: Int,
                     @BeanProperty @JsonProperty("dw") var direWin: Int,
                     @BeanProperty @JsonProperty("gs") var gameStatus: Int
                   ) extends Serializable

case class TeamBean(
                     @BeanProperty var id: Int,
                     @BeanProperty @JsonProperty("n") var name: String,
                     @BeanProperty @JsonProperty("t") var tag: String,
                     @BeanProperty @JsonProperty("l") var logo: String,
                     @BeanProperty @JsonProperty("p") var players: List[Player]
                   ) extends Serializable


case class Player(
                   @BeanProperty @JsonProperty("id") var accountId: Int,
                   @BeanProperty @JsonProperty("n") var name: String,
                   @BeanProperty @JsonProperty("h") var hero: HeroBean,
                   @BeanProperty @JsonProperty("i") var items: List[Item],
                   @BeanProperty @JsonProperty("l") var level: Int,
                   @BeanProperty @JsonProperty("k") var kills: Int,
                   @BeanProperty @JsonProperty("d") var deaths: Int,
                   @BeanProperty @JsonProperty("a") var assists: Int,
                   @BeanProperty @JsonProperty("nw") var netWorth: Int
                 ) extends Serializable

case class HeroBean(
                     @BeanProperty var id: Int,
                     @BeanProperty @JsonProperty("n") var name: String
                   ) extends Serializable

case class Item(
                 @BeanProperty var id: Int,
                 @BeanProperty @JsonProperty("n") var name: String
               ) extends Serializable

case class LeagueBean(
                       @BeanProperty var id: Int,
                       @BeanProperty @JsonProperty("n") var name: String
                     ) extends Serializable


case class Match(
                  @BeanProperty @JsonProperty("id") var matchId: Long,
                  @BeanProperty @JsonProperty("st") var startTime: Long,
                  @BeanProperty @JsonProperty("d") var duration: String,
                  @BeanProperty @JsonProperty("mst") var matchStatus: Integer,
                  @BeanProperty @JsonProperty("rad") var radiantTeam: TeamBean,
                  @BeanProperty @JsonProperty("dire") var direTeam: TeamBean,
                  @BeanProperty @JsonProperty("msc") var matchScore: String,
                  @BeanProperty @JsonProperty("nw") var networth: List[Double],
                  @BeanProperty @JsonProperty("gn") var gameNumber: Int,
                  @BeanProperty @JsonProperty("rpic") var radianPicks: List[HeroBean],
                  @BeanProperty @JsonProperty("rban") var radianBans: List[HeroBean],
                  @BeanProperty @JsonProperty("dpic") var direPicks: List[HeroBean],
                  @BeanProperty @JsonProperty("dban") var direBans: List[HeroBean]
                ) extends Serializable