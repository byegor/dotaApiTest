package com.eb.pulse.telegrambot.bot.command.internal;

import com.eb.pulse.telegrambot.bot.command.BotCommand;
import com.eb.pulse.telegrambot.service.DataService;
import com.eb.schedule.shared.bean.HeroBean;
import com.eb.schedule.shared.bean.Match;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Egor on 08.10.2017.
 */
public class PickCmd extends BotCommand {
    public PickCmd() {
        super("/mp", "");
    }

    @Override
    public BotApiMethod executeCmd(Message message, String... arguments) {
        String matchId = arguments[1];

        Match match = DataService.INSTANCE.getMatchById(matchId);
        List<HeroBean> radianPicks = match.getRadianPicks();
        List<HeroBean> direPicks = match.getDirePicks();
        Iterator<HeroBean> direIterator = direPicks.iterator();
        StringBuilder sb = new StringBuilder();
        for (HeroBean radianPick : radianPicks) {
            HeroBean direPick = direIterator.next();
            sb.append(String.format("%-15s", radianPick.getName())).append(direPick.getName()).append("\r\n");
        }

        EditMessageText editMessage = new EditMessageText();
        editMessage.setText(sb.toString());
        editMessage.enableMarkdown(true);
        editMessage.setChatId(message.getChatId());
        editMessage.setMessageId(message.getMessageId());
        editMessage.setReplyMarkup(createInlineKeyboard(match));
        return editMessage;
    }

    @Override
    protected boolean validateCmdRequest(String[] arguments) {
        return arguments.length == 2;
    }

    private InlineKeyboardMarkup createInlineKeyboard(Match match) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> inlineRow = new ArrayList<>();

        inlineRow.add(new InlineKeyboardButton().setText("« Back").setCallbackData("/mg " + match.getMatchId()));
        inlineRow.add(new InlineKeyboardButton().setText("Bans").setCallbackData("/mb " + match.getMatchId()));
        rows.add(inlineRow);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
