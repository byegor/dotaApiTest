package com.eb.pulse.telegrambot.bot.command.internal;

import com.eb.pulse.telegrambot.bot.command.BotCommand;
import com.eb.pulse.telegrambot.service.DataService;
import com.eb.schedule.shared.bean.Match;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egor on 09.10.2017.
 */
public class GeneralMatchHeroStatsCmd extends BotCommand {
    public GeneralMatchHeroStatsCmd() {
        super("/mgh", "");
    }

    @Override
    public BotApiMethod executeCmd(Message message, String... arguments) {
        String matchId = arguments[1];
        Match match = DataService.INSTANCE.getMatchById(matchId);

        EditMessageText editMessage = new EditMessageText();
        editMessage.setText("Choose side");
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
        inlineRow.add(new InlineKeyboardButton().setText("Radiant").setCallbackData("/mhs r " + match.getMatchId()));
        inlineRow.add(new InlineKeyboardButton().setText("Dire").setCallbackData("/mhs d " + match.getMatchId()));
        rows.add(inlineRow);

        inlineRow = new ArrayList<>();
        inlineRow.add(new InlineKeyboardButton().setText("« Back").setCallbackData("/mg " + match.getMatchId()));
        rows.add(inlineRow);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
