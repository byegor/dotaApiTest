package com.eb.pulse.telegrambot.bot;

import com.eb.pulse.telegrambot.bot.command.BotCommand;
import com.eb.pulse.telegrambot.bot.command.internal.*;
import com.eb.pulse.telegrambot.bot.command.user.FinishedCmd;
import com.eb.pulse.telegrambot.bot.command.user.HelpCmd;
import com.eb.pulse.telegrambot.bot.command.user.LiveCmd;
import com.eb.pulse.telegrambot.bot.command.user.RecentCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egor on 24.09.2017.
 */
public class DotaStatisticsBot extends TelegramLongPollingBot {

    private static Logger log = LoggerFactory.getLogger(DotaStatisticsBot.class);

    private List<BotCommand> botCommands = new ArrayList<>();
    private SendMessage errorMessage;


    public DotaStatisticsBot() {
        botCommands.add(new RecentCmd());
        botCommands.add(new LiveCmd());
        botCommands.add(new FinishedCmd());
        botCommands.add(new GameDetailsCmd());
        botCommands.add(new GeneralMatchInfoCmd());
        botCommands.add(new PickCmd());
        botCommands.add(new BanCmd());
        botCommands.add(new GeneralMatchHeroStatsCmd());
        botCommands.add(new HeroStatsCmd());
        botCommands.add(new HelpCmd(this));
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotApiMethod message = null;
        Long chatId = null;
        if (update.hasMessage() && update.getMessage().isCommand()) {
            Message incomingMessage = update.getMessage();
            String commandRequest = incomingMessage.getText();
            chatId = incomingMessage.getChatId();
            message = processCommand(commandRequest, incomingMessage);

        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            Message callbackMessage = update.getCallbackQuery().getMessage();
            chatId = callbackMessage.getChatId();
            message = processCommand(data, callbackMessage);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            message = new SendMessage()
                    .setChatId(chatId)
                    .setText(message_text)
                    .setParseMode(ParseMode.MARKDOWN);
        }
        if (message == null) {
            log.error("Couldn't handle command:" + update + " ");
            message = getErrorMessage(chatId);
        }

        try {
            execute(message);
        } catch (Exception e) {
            log.error("Issue with sending message ", e);
        }
    }

    public BotApiMethod processCommand(String commandRequest, Message message) {
        String[] split = commandRequest.split(" ");
        String command = split[0].toLowerCase();
        for (BotCommand botCommand : botCommands) {
            if (botCommand.getCommand().equals(command)) {
                return botCommand.execute(message, split);
            }
        }
        return null;
    }

    private SendMessage getErrorMessage(Long chatId) {
        errorMessage = new SendMessage();
        errorMessage.setText("Opps and sorry! We can't handle your request now :( ");
        errorMessage.setChatId(chatId);
        return errorMessage;
    }

    public List<BotCommand> getBotCommands() {
        return botCommands;
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
