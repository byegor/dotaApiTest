package com.eb.pulse.telegrambot.service;

import com.eb.pulse.telegrambot.entity.Data;
import com.eb.schedule.shared.bean.GameBean;
import com.eb.schedule.shared.bean.Match;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Egor on 24.09.2017.
 */

public enum DataService {
    INSTANCE;

    Data data;
    List<GameBean> currentGames;

    public void setData(Data data) {
        this.data = data;
        currentGames = data.getCurrentGames().values().stream().flatMap(List::stream).collect(Collectors.toList());
    }


    public List<GameBean> getCurrentGames() {
        return currentGames;
    }

    public List<GameBean> getLiveGames() {
        if (data != null) {
            return currentGames.stream().filter(gameBean -> gameBean.getGameStatus() == 1).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public List<GameBean> getFinishedGames() {
        if (data != null) {
            return currentGames.stream().filter(gameBean -> gameBean.getGameStatus() == 2).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> getMatchesIdByGameId(String gameId) {
        return data.getMatchesByGames().getOrDefault(gameId, Collections.emptyList());
    }

    public Match getMatchById(String matchId) {
        return data.getCurrentMatches().get(matchId);
    }
}
