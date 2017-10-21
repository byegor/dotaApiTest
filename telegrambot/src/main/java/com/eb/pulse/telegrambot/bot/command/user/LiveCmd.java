package com.eb.pulse.telegrambot.bot.command.user;

import com.eb.pulse.telegrambot.service.DataService;
import com.eb.schedule.shared.bean.GameBean;

import java.util.List;

/**
 * Created by Egor on 03.10.2017.
 */
public class LiveCmd extends RecentCmd {
    public LiveCmd() {
        super("/live", "get all live games");
    }

    @Override
    public List<GameBean> getGameBeanList(int count) {
        return DataService.INSTANCE.getLiveGames();
    }

    @Override
    public String getText() {
        return getDescription();
    }

    @Override
    public String getTextForNoGames() {
        return "there are no live games currently running";
    }
}
