package com.eb.pulse.telegrambot.bot.command;

import com.eb.pulse.telegrambot.service.DataService;
import com.eb.schedule.shared.bean.GameBean;

import java.util.List;

/**
 * Created by Egor on 03.10.2017.
 */
public class FinishedCmd extends AllCmd {
    public FinishedCmd() {
        super("/done", "get finished games during last 8 hours");
    }

    @Override
    public List<GameBean> getGameBeanList() {
        return DataService.INSTANCE.getFinishedGames();
    }

    @Override
    public String getText() {
        return getDescription();
    }
}
