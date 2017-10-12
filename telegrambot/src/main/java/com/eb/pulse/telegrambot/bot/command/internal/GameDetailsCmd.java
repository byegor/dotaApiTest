package com.eb.pulse.telegrambot.bot.command.internal;

import com.eb.pulse.telegrambot.bot.command.BotCommand;
import com.eb.pulse.telegrambot.service.DataService;
import com.eb.pulse.telegrambot.util.TransformerUtil;
import com.eb.schedule.shared.bean.GameBean;
import com.eb.schedule.shared.bean.Match;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Egor on 03.10.2017.
 */
public class GameDetailsCmd extends BotCommand {
    public GameDetailsCmd() {
        super("/gd", "get details for a specific game");
    }

    @Override
    public SendMessage executeCmd(Message message, String... arguments) {
        Integer gameId = Integer.parseInt(arguments[1]);
        Optional<GameBean> first = DataService.INSTANCE.getCurrentGames().stream().filter(game -> game.getId() == gameId).findFirst();
        if (first.isPresent()) {
            StringBuilder sb = new StringBuilder();
            GameBean gameBean = first.get();
            sb.append("League: _").append(gameBean.getLeague().getName()).append(", ").append(gameBean.getSeriesType()).append("_\r\n");
            sb.append(gameBean.getRadiant().getName()).append("  ").append(gameBean.getRadiantWin());
            sb.append(" : ").append(gameBean.getDireWin()).append("  ").append(gameBean.getDire().getName()).append("\r\n");


            List<String> mathesId = DataService.INSTANCE.getMatchesIdByGameId(arguments[1]);
            int index = 0;
            for (String mId : mathesId) {
                Match match = DataService.INSTANCE.getMatchById(mId);
                sb.append("*Game ").append(++index).append("*").append("\r\n");
                sb.append(TransformerUtil.transformMatchFoGeneralInf(match));
            }
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(sb.toString());
            sendMessage.setChatId(message.getChatId());
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(createInlineKeyboard(mathesId));
            return sendMessage;
        } else {
            log.debug("Couldn't find the game by Id: " + gameId + " " + message);
            return null;
        }
    }

    @Override
    protected boolean validateCmdRequest(String[] arguments) {
        return arguments.length == 2;
    }

    private InlineKeyboardMarkup createInlineKeyboard(List<String> matchesId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> inlineRow = null;
        for (int i = 0; i < matchesId.size(); i++) {
            if (i % 2 == 0) {
                if (inlineRow != null) {
                    rows.add(inlineRow);
                }
                inlineRow = new ArrayList<>();
            }
            inlineRow.add(new InlineKeyboardButton().setText("Game " + (i + 1) + " »").setCallbackData("/mg " + matchesId.get(i)));
        }
        if (inlineRow != null && !inlineRow.isEmpty()) {
            rows.add(inlineRow);
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
