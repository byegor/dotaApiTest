package com.eb.pulse.telegrambot.bot.command;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Created by Egor on 03.10.2017.
 */
public class FinishedCmd extends BotCommand {
    public FinishedCmd() {
        super("/done", "get finished games during last 8 hours");
    }

    @Override
    public SendMessage processCommand(Message message, String... arguments) {
        return null;

    }
}
