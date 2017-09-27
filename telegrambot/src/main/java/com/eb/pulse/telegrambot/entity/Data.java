package com.eb.pulse.telegrambot.entity;

import com.eb.schedule.shared.bean.GameBean;
import com.eb.schedule.shared.bean.Match;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Egor on 24.09.2017.
 */
public class Data {

    Map<String, List<GameBean>> currentGames;
    Map<String, Match> currentMatches;
    Map<String, Match> matchesByGames;

    public Data() {
    }

    public List<GameBean> getCurrentGames() {
        return currentGames.entrySet().stream().map(entry -> entry.getValue()).flatMap(List::stream).collect(Collectors.toList());
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

    public Map<String, Match> getMatchesByGames() {
        return matchesByGames;
    }

    public void setMatchesByGames(Map<String, Match> matchesByGames) {
        this.matchesByGames = matchesByGames;
    }
}
