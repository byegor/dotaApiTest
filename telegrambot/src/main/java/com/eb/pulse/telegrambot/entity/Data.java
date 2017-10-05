package com.eb.pulse.telegrambot.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by Egor on 24.09.2017.
 */
public class Data {

    public Map<String, List<String>> currentGames;
    Map<String, String> currentMatches;
    Map<String, List<String>> matchesByGames;

    public Data() {
    }

    public Map<String, List<String>> getCurrentGames() {
        return currentGames;
    }

    public void setCurrentGames(Map<String, List<String>> currentGames) {
        this.currentGames = currentGames;
    }

    public Map<String, String> getCurrentMatches() {
        return currentMatches;
    }

    public void setCurrentMatches(Map<String, String> currentMatches) {
        this.currentMatches = currentMatches;
    }

    public Map<String, List<String>> getMatchesByGames() {
        return matchesByGames;
    }

    public void setMatchesByGames(Map<String, List<String>> matchesByGames) {
        this.matchesByGames = matchesByGames;
    }
}