package com.eb.pulse.telegrambot.bot.command.user;

import com.eb.pulse.telegrambot.bot.command.BotCommand;
import com.eb.pulse.telegrambot.bot.command.UserCommand;
import com.eb.pulse.telegrambot.service.DataService;
import com.eb.pulse.telegrambot.util.TransformerUtil;
import com.eb.schedule.shared.bean.GameBean;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egor on 03.10.2017.
 */
public class RecentCmd extends BotCommand implements UserCommand {
    public RecentCmd() {
        super("/recent", "get recent 5 games, want more: type /recent 10");
    }

    protected RecentCmd(String s, String s1) {
        super(s, s1);
    }

    @Override
    public SendMessage executeCmd(Message message, String... arguments) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<GameBean> games = null;
        if (arguments.length == 1) {
            games = getGameBeanList(5);
        } else {
            int count = 5;
            try {
                count = Integer.parseInt(arguments[1]);
            } catch (NumberFormatException ignore) {

            }
            games = getGameBeanList(count);
        }

        for (GameBean g : games) {
            List<InlineKeyboardButton> inlineRow = new ArrayList<>();
            inlineRow.add(new InlineKeyboardButton().setText(TransformerUtil.transform(g)).setCallbackData("/gd " + g.id));
            rows.add(inlineRow);
        }

        keyboardMarkup.setKeyboard(rows);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(keyboardMarkup);
        sendMessage.setText(getText());
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    @Override
    protected boolean validateCmdRequest(String[] arguments) {
        return arguments.length >= 1;
    }

    public List<GameBean> getGameBeanList(int count) {
        return DataService.INSTANCE.getRecentGames(count);
    }

    public String getText() {
        return "Here the list of games for the last few hours";
    }

    @Override
    public String getHelpDescription() {
        return getCommand() + " - " + getDescription();
    }
}
