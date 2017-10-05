package com.eb.pulse.telegrambot.bot.command;

import com.eb.pulse.telegrambot.service.DataService;
import com.eb.schedule.shared.bean.GameBean;

import java.util.List;

/**
 * Created by Egor on 03.10.2017.
 */
public class LiveCmd extends AllCmd {
    public LiveCmd() {
        super("/live", "get all live games");
    }

    @Override
    public List<GameBean> getGameBeanList() {
        return DataService.INSTANCE.getLiveGames();
    }

    @Override
    public String getText() {
        return getDescription();
    }
}
