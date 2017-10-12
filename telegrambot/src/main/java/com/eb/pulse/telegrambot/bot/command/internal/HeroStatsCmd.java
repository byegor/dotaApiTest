package com.eb.pulse.telegrambot.bot.command.internal;

import com.eb.pulse.telegrambot.bot.command.BotCommand;
import com.eb.pulse.telegrambot.service.DataService;
import com.eb.schedule.shared.bean.Item;
import com.eb.schedule.shared.bean.Match;
import com.eb.schedule.shared.bean.Player;
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
public class HeroStatsCmd extends BotCommand {
    public HeroStatsCmd() {
        super("/mhs", "");
    }

    @Override
    public BotApiMethod executeCmd(Message message, String... arguments) {
        String matchId = arguments[2];
        boolean radiant = "r".equals(arguments[1]);

        Match match = DataService.INSTANCE.getMatchById(matchId);
        List<Player> players = radiant ? match.getRadiantTeam().getPlayers() : match.getDireTeam().getPlayers();
        StringBuilder sb = new StringBuilder();
        for (Player player : players) {
            sb.append("*").append(player.getName()).append("*:\r\n");
            sb.append("   _Hero_: ").append(player.getHero().getName()).append(", ").append(player.getLevel()).append(" lvl").append("\r\n");
            sb.append("   _KDA_: ").append(player.getKills()).append("/").append(player.getDeaths()).append("/").append(player.getAssists()).append("\r\n");
            sb.append("   _Items_: ");
            List<Item> items = player.getItems();
            for (Item item : items) {
                sb.append(item.getName()).append("  ");
            }
            sb.append("\r\n");
        }

        EditMessageText editMessage = new EditMessageText();
        editMessage.setText(sb.toString());
        editMessage.enableMarkdown(true);
        editMessage.setChatId(message.getChatId());
        editMessage.setMessageId(message.getMessageId());
        editMessage.setReplyMarkup(createInlineKeyboard(match, radiant));
        return editMessage;
    }

    @Override
    protected boolean validateCmdRequest(String[] arguments) {
        return arguments.length == 3;
    }

    private InlineKeyboardMarkup createInlineKeyboard(Match match, boolean radiant) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> inlineRow = new ArrayList<>();

        String buttonName;
        String cmdValue;
        if (radiant) {
            buttonName = "Dire heroes";
            cmdValue = "d ";
        } else {
            buttonName = "Radiant heroes";
            cmdValue = "r ";
        }

        inlineRow.add(new InlineKeyboardButton().setText("« Back").setCallbackData("/mgh " + match.getMatchId()));
        inlineRow.add(new InlineKeyboardButton().setText(buttonName).setCallbackData("/mhs " + cmdValue + match.getMatchId()));
        rows.add(inlineRow);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
