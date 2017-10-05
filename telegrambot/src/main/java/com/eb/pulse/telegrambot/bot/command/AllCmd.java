package com.eb.pulse.telegrambot.bot.command;

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
//todo the same code for done and live games
public class AllCmd extends BotCommand {
    public AllCmd() {
        super("/all", "get all games during last few hours");
    }

    protected AllCmd(String s, String s1) {
        super(s, s1);
    }

    @Override
    public SendMessage processCommand(Message message, String... arguments) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> inlineRow = null;
        List<GameBean> games = getGameBeanList();
        for (int i = 0; i < games.size(); i++) {
            if (i % 2 == 0) {
                if (inlineRow != null) {
                    rows.add(inlineRow);
                }
                inlineRow = new ArrayList<>();
            }
            GameBean g = games.get(i);
            inlineRow.add(new InlineKeyboardButton().setText(TransformerUtil.transform(g)).setCallbackData("/gd " + g.id));
        }
        if (!inlineRow.isEmpty()) {
            rows.add(inlineRow);
        }


        keyboardMarkup.setKeyboard(rows);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(keyboardMarkup);
        sendMessage.setText(getText());
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    public List<GameBean> getGameBeanList(){
        return DataService.INSTANCE.getCurrentGames();
    }

    public String getText(){
        return "Here the list of games for the last few hours";
    }
}
