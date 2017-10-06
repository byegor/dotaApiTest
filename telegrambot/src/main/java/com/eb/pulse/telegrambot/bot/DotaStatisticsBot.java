package com.eb.pulse.telegrambot.bot;

import com.eb.pulse.telegrambot.bot.command.*;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
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
        botCommands.add(new GameDetailsCmd());
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = null;
        if (update.hasMessage() && update.getMessage().isCommand()) {
            Message incomingMessage = update.getMessage();
            String commandRequest = incomingMessage.getText();
            message = processCommand(commandRequest, incomingMessage);

        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();

            message = processCommand(data, update.getCallbackQuery().getMessage());
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(message_text)
                    .setParseMode(ParseMode.MARKDOWN);
        }
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();//todo
            }
        }
    }

    public SendMessage processCommand(String commandRequest, Message message) {
        String[] split = commandRequest.split(" ");
        String command = split[0].toLowerCase();
        for (BotCommand botCommand : botCommands) {
            if (botCommand.getCommand().equals(command)) {
                return botCommand.processCommand(message, split);
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
