package com.eb.pulse.telegrambot.service;

import com.eb.pulse.telegrambot.entity.Data;
import com.eb.pulse.telegrambot.util.TransformerUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Egor on 24.09.2017.
 */

public enum DataService {
    INSTANCE;

    private Data data;


    public void setData(Data data) {
        this.data = data;
    }


    public List<String> getAllGames() {
        return data.getGames().stream().map(TransformerUtil::transform).collect(Collectors.toList());
    }

    public List<String> getLiveGames() {
        return data.getGames().stream().filter(gameBean -> gameBean.getGameStatus() == 1).map(TransformerUtil::transform).collect(Collectors.toList());
    }

    public List<String> getFinishedGames() {
        return data.getGames().stream().filter(gameBean -> gameBean.getGameStatus() == 0).map(TransformerUtil::transform).collect(Collectors.toList());
    }
}
