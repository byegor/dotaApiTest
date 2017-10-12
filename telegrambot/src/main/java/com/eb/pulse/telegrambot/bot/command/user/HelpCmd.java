package com.eb.pulse.telegrambot.bot.command.user;

import com.eb.pulse.telegrambot.bot.DotaStatisticsBot;
import com.eb.pulse.telegrambot.bot.command.BotCommand;
import com.eb.pulse.telegrambot.bot.command.UserCommand;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.List;

/**
 * Created by Egor on 03.10.2017.
 */
public class HelpCmd extends BotCommand implements UserCommand {

    private final DotaStatisticsBot bot;
    private final String helpMessage;

    public HelpCmd(DotaStatisticsBot bot) {
        super("/help", "print all available commands");
        this.bot = bot;
        this.helpMessage = generateHelpMessage();
    }

    private String generateHelpMessage() {
        StringBuilder sb = new StringBuilder();
        List<BotCommand> botCommands = bot.getBotCommands();
        for (BotCommand botCommand : botCommands) {
            if (botCommand instanceof UserCommand) {
                sb.append(((UserCommand) botCommand).getHelpDescription()).append("\r\n");
            }
        }
        return sb.toString();
    }


    @Override
    public SendMessage executeCmd(Message message, String... arguments) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(helpMessage);
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    @Override
    protected boolean validateCmdRequest(String[] arguments) {
        return true;
    }

    @Override
    public String getHelpDescription() {
        return getCommand() + " - " + getDescription();
    }

}
