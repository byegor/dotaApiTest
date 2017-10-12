package com.eb.pulse.telegrambot.bot.command.internal;

import com.eb.pulse.telegrambot.bot.command.BotCommand;
import com.eb.pulse.telegrambot.service.DataService;
import com.eb.pulse.telegrambot.util.TransformerUtil;
import com.eb.schedule.shared.bean.Match;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egor on 03.10.2017.
 */
public class GeneralMatchInfoCmd extends BotCommand {
    public GeneralMatchInfoCmd() {
        super("/mg", "get general info regarding match");
    }

    @Override
    public EditMessageText executeCmd(Message message, String... arguments) {
        String matchId = arguments[1];
        Match match = DataService.INSTANCE.getMatchById(matchId);
        String matchString = TransformerUtil.transformMatchFoGeneralInf(match);
        matchString += EmojiParser.parseToUnicode("      :clock4: ") + match.getDuration();

        EditMessageText editMessage = new EditMessageText();
        editMessage.setText(matchString);
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
        List<InlineKeyboardButton> inlineRow = null;
        if (!match.getRadianBans().isEmpty() && !match.getDireBans().isEmpty()) {
            inlineRow = new ArrayList<>();
            inlineRow.add(new InlineKeyboardButton().setText("Pick/Bans").setCallbackData("/mp " + match.getMatchId()));
            inlineRow.add(new InlineKeyboardButton().setText("Hero's stats").setCallbackData("/mgh " + match.getMatchId()));
            rows.add(inlineRow);
        }

        /*todo barracks and towers
        inlineRow = new ArrayList<>();
        inlineRow.add(new InlineKeyboardButton().setText("Barracks and Towers").setCallbackData("/ms " + match.getMatchId()));
        rows.add(inlineRow);*/

        inlineRow = new ArrayList<>();
        inlineRow.add(new InlineKeyboardButton().setText("« Back").setCallbackData("/gd " + DataService.INSTANCE.getGamesByMatchId(match.getMatchId().toString())));
        if (match.getMatchStatus() == 0) {
            inlineRow.add(new InlineKeyboardButton().setText("Update").setCallbackData("/mg " + match.getMatchId()));
        }
        rows.add(inlineRow);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
