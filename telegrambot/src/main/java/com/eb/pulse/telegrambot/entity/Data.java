package com.eb.pulse.telegrambot.entity;

import com.eb.schedule.shared.bean.GameBean;
import com.eb.schedule.shared.bean.Match;

import java.util.List;
import java.util.Map;

/**
 * Created by Egor on 24.09.2017.
 */
public class Data {

    List<GameBean> games;
    Map<String, Match> matchById;
    Map<String, List<Match>> matchesByGames;

    public Data() {
    }

    public Data(List<GameBean> games, Map<String, Match> matchById, Map<String, List<Match>> matchesByGames) {
        this.games = games;
        this.matchById = matchById;
        this.matchesByGames = matchesByGames;
    }

    public List<GameBean> getGames() {
        return games;
    }

    public void setGames(List<GameBean> games) {
        this.games = games;
    }

    public Map<String, Match> getMatchById() {
        return matchById;
    }

    public void setMatchById(Map<String, Match> matchById) {
        this.matchById = matchById;
    }

    public Map<String, List<Match>> getMatchesByGames() {
        return matchesByGames;
    }

    public void setMatchesByGames(Map<String, List<Match>> matchesByGames) {
        this.matchesByGames = matchesByGames;
    }
}
