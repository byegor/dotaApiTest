package com.eb.pulse.telegrambot.bot;

import com.eb.pulse.telegrambot.service.DataService;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by Egor on 24.09.2017.
 */
public class DotaStatisticsBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().isCommand()) {
            String commandRequest = update.getMessage().getText();
            String responceText = processCommand(commandRequest);

            long chat_id = update.getMessage().getChatId();
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(responceText);
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
                    .setText(message_text);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String processCommand(String commandRequest) {
        String[] split = commandRequest.split(" ");
        String command = split[0];
        switch (command.toLowerCase()) {
            case "/all":

                return DataService.INSTANCE.getAllGames().stream().reduce((o1, o2) -> o1 + "\r\n" + o2).orElse("");
            case "/live":
                return "get only live games";
            case "/finished":
                return "only finished";
            default:
                return "have no idea what you want, ltmgtfy";


        }
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
