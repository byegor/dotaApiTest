package com.eb.pulse.telegrambot.entity;

import com.eb.schedule.shared.bean.GameBean;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Egor on 24.09.2017.
 */
public class Data {

    Map<String, String> currentGames;
    Map<String, String> currentMatches;
    Map<String, String> matchesByGames;

    public Data() {
    }

    public List<GameBean> getCurrentGames() {
//        return currentGames.entrySet().stream().map(entry -> entry.getValue()).flatMap(List::stream).collect(Collectors.toList());
        return Collections.emptyList();
    }

    public Map<String, String> getCurrentMatches() {
        return currentMatches;
    }

    public void setCurrentMatches(Map<String, String> currentMatches) {
        this.currentMatches = currentMatches;
    }

    public Map<String, String> getMatchesByGames() {
        return matchesByGames;
    }

    public void setMatchesByGames(Map<String, String> matchesByGames) {
        this.matchesByGames = matchesByGames;
    }
}