package com.eb.pulse.telegrambot.bot;

import com.eb.pulse.telegrambot.bot.command.*;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egor on 24.09.2017.
 */
public class DotaStatisticsBot extends TelegramLongPollingBot {

    List<BotCommand> botCommands = new ArrayList<>();

    public DotaStatisticsBot() {
        botCommands.add(new AllCmd());
        botCommands.add(new LiveCmd());
        botCommands.add(new FinishedCmd());
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().isCommand()) {
            String commandRequest = update.getMessage().getText();
            SendMessage message = processCommand(commandRequest, update);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();


            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(message_text)
                    .setParseMode(ParseMode.MARKDOWN);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            String[] split = data.split(" ");
            SendMessage message = null;
            switch (split[0]) {
                case "/gd":
                    message = new GameDetailsCmd().processCommand(update.getCallbackQuery().getMessage(), split);
            }
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public SendMessage processCommand(String commandRequest, Update update) {
        String[] split = commandRequest.split(" ");
        String command = split[0].toLowerCase();
        for (BotCommand botCommand : botCommands) {
            if (botCommand.getCommand().equals(command)) {
                return botCommand.processCommand(update.getMessage(), split);
            }
        }
        //todo
        return null;
    }

    @Override
    public String getBotUsername() {
        return "DotaStatisticsBot";
    }

    @Override
    public String getBotToken() {
        //todo
        return System.getProperty("bot.token", "349821242:AAHEVMnE6cjsrk5HSBnn3il7vhs2YbTT0qU");
    }
}
