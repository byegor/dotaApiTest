package com.eb.pulse.telegrambot.bot.command;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Created by Egor on 03.10.2017.
 */
public class LiveCmd extends BotCommand {
    public LiveCmd() {
        super("/live", "get all live games");
    }

    @Override
    public SendMessage processCommand(Message message, String... arguments) {
        return null;

    }


}
