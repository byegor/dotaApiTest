package com.eb.pulse.telegrambot.bot.command;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Created by Egor on 03.10.2017.
 */
public abstract class BotCommand {

    private String command;
    private String description;

    public BotCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public abstract SendMessage processCommand(Message message, String... arguments);

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }
}
