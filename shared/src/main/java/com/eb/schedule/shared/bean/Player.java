package com.eb.schedule.shared.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Egor on 06.07.2016.
 */
public class Player implements Serializable{
    @JsonProperty("id")
    int accountId;
    @JsonProperty("n")
    String name;
    @JsonProperty("h")
    HeroBean hero;
    @JsonProperty("i")
    List<Item> items;
    @JsonProperty("l")
    int level;
    @JsonProperty("k")
    int kills;
    @JsonProperty("d")
    int deaths;
    @JsonProperty("a")
    int assists;
    @JsonProperty("nw")
    int netWorth;


    public Player() {
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HeroBean getHero() {
        return hero;
    }

    public void setHero(HeroBean hero) {
        this.hero = hero;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(int netWorth) {
        this.netWorth = netWorth;
    }
}

