package com.eb.pulse.telegrambot.entity;

import com.eb.schedule.shared.bean.GameBean;
import com.eb.schedule.shared.bean.Match;

import java.util.List;
import java.util.Map;

/**
 * Created by Egor on 24.09.2017.
 */
public class Data {

    Map<String, List<GameBean>> currentGames;
    Map<String, Match> currentMatches;
    Map<String, List<String>> matchesByGames;

    public Data() {
    }

    public Map<String, List<GameBean>> getCurrentGames() {
        return currentGames;
    }

    public void setCurrentGames(Map<String, List<GameBean>> currentGames) {
        this.currentGames = currentGames;
    }

    public Map<String, Match> getCurrentMatches() {
        return currentMatches;
    }

    public void setCurrentMatches(Map<String, Match> currentMatches) {
        this.currentMatches = currentMatches;
    }

    public Map<String, List<String>> getMatchesByGames() {
        return matchesByGames;
    }

    public void setMatchesByGames(Map<String, List<String>> matchesByGames) {
        this.matchesByGames = matchesByGames;
    }
}