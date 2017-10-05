package com.eb.pulse.telegrambot.service;

import com.eb.pulse.telegrambot.util.TransformerUtil;
import com.eb.schedule.shared.bean.GameBean;
import com.eb.schedule.shared.bean.Match;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Egor on 24.09.2017.
 */

public enum DataService {
    INSTANCE;

    List<GameBean> currentGames = Collections.emptyList();
    Map<String, Match> currentMatches = Collections.emptyMap();
    Map<String, List<String>> matchesByGames = Collections.emptyMap();


    public void setCurrentGames(List<GameBean> currentGames) {
        this.currentGames = currentGames;
    }

    public void setCurrentMatches(Map<String, Match> currentMatches) {
        this.currentMatches = currentMatches;
    }

    public void setMatchesByGames(Map<String, List<String>> matchesByGames) {
        this.matchesByGames = matchesByGames;
    }
//todo text can't be empty


    public List<GameBean> getCurrentGames() {
        return currentGames;
    }

    public List<String> getLiveGames() {
        return currentGames.stream().filter(gameBean -> gameBean.getGameStatus() == 1).map(TransformerUtil::transform).collect(Collectors.toList());
    }

    public List<String> getFinishedGames() {
        return currentGames.stream().filter(gameBean -> gameBean.getGameStatus() == 2).map(TransformerUtil::transform).collect(Collectors.toList());
    }
}
