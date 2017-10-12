package com.eb.pulse.telegrambot.bot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Arrays;

/**
 * Created by Egor on 03.10.2017.
 */
public abstract class BotCommand<T extends BotApiMethod> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private String command;
    private String description;

    public BotCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public T execute(Message message, String... arguments) {
        if (validateCmdRequest(arguments)) {
            return executeCmd(message, arguments);
        } else {
            log.debug("Error processing command: " + Arrays.toString(arguments) + " message: " + message);
            return null;
        }

    }


    protected abstract T executeCmd(Message message, String... arguments);

    protected abstract boolean validateCmdRequest(String[] arguments);

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }
}
