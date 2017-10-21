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
import java.util.List;

/**
 * Created by Egor on 08.10.2017.
 */
public class PickCmd extends BotCommand {
    public PickCmd() {
        super("/mp", "");
    }

    public PickCmd(String s, String s1) {
        super(s,s1);
    }


    @Override
    public BotApiMethod executeCmd(Message message, String... arguments) {
        String matchId = arguments[1];

        Match match = DataService.INSTANCE.getMatchById(matchId);

        EditMessageText editMessage = new EditMessageText();
        editMessage.setText(generateMessage(match));
        editMessage.enableMarkdown(true);
        editMessage.setChatId(message.getChatId());
        editMessage.setMessageId(message.getMessageId());
        editMessage.setReplyMarkup(createInlineKeyboard(match));
        return editMessage;
    }

    protected void addPickInfo(StringBuilder sb, String teamName, List<HeroBean> heroes) {
        sb.append("_").append(teamName).append("_").append("\r\n");

        for (HeroBean hero : heroes) {
            sb.append("    ").append(hero.getName()).append("\r\n");
        }
    }

    @Override
    protected boolean validateCmdRequest(String[] arguments) {
        return arguments.length == 2;
    }

    protected InlineKeyboardMarkup createInlineKeyboard(Match match) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> inlineRow = new ArrayList<>();

        inlineRow.add(new InlineKeyboardButton().setText("« Back").setCallbackData("/mg " + match.getMatchId()));
        inlineRow.add(new InlineKeyboardButton().setText("Bans").setCallbackData("/mb " + match.getMatchId()));
        rows.add(inlineRow);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    protected String generateMessage(Match match){
        StringBuilder sb = new StringBuilder();

        addPickInfo(sb, match.getRadiantTeam().getName(), match.getRadianPicks());
        addPickInfo(sb, match.getDireTeam().getName(), match.getDirePicks());
        return sb.toString();
    }
}
