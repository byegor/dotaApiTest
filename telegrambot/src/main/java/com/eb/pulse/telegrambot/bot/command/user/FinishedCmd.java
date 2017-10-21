package com.eb.pulse.telegrambot.bot.command.user;

import com.eb.pulse.telegrambot.service.DataService;
import com.eb.schedule.shared.bean.GameBean;

import java.util.List;

/**
 * Created by Egor on 03.10.2017.
 */
public class FinishedCmd extends RecentCmd {
    public FinishedCmd() {
        super("/done", "get last 5 finished games, also available /done N");
    }

    @Override
    public List<GameBean> getGameBeanList(int count) {
        return DataService.INSTANCE.getFinishedGames(count);
    }

    @Override
    public String getText() {
        return getDescription();
    }

    @Override
    public String getTextForNoGames() {
        return "Couldn't find any finished game for the past few hours";
    }
}
