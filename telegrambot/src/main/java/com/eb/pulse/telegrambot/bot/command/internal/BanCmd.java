package com.eb.pulse.telegrambot.bot.command.internal;

import com.eb.schedule.shared.bean.Match;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egor on 08.10.2017.
 */

public class BanCmd extends PickCmd {
    public BanCmd() {
        super("/mb", "");
    }


    protected String generateMessage(Match match) {
        StringBuilder sb = new StringBuilder();

        addPickInfo(sb, match.getRadiantTeam().getName(), match.getRadianBans());
        addPickInfo(sb, match.getDireTeam().getName(), match.getDireBans());
        return sb.toString();
    }

    @Override
    protected boolean validateCmdRequest(String[] arguments) {
        return arguments.length == 2;
    }

    protected InlineKeyboardMarkup createInlineKeyboard(Match match) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> inlineRow = new ArrayList<>();

        inlineRow.add(new InlineKeyboardButton().setText("« Back").setCallbackData("/mg " + match.getMatchId()));
        inlineRow.add(new InlineKeyboardButton().setText("Picks").setCallbackData("/mp " + match.getMatchId()));
        rows.add(inlineRow);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
