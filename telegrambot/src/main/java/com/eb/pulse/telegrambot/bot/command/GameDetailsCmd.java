package com.eb.pulse.telegrambot.bot.command;

import com.eb.pulse.telegrambot.service.DataService;
import com.eb.schedule.shared.bean.GameBean;
import com.eb.schedule.shared.bean.Match;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.List;
import java.util.Optional;

/**
 * Created by Egor on 03.10.2017.
 */
//todo the same code for done and live games
public class GameDetailsCmd extends BotCommand {
    public GameDetailsCmd() {
        super("/gd", "get details for a specific game");
    }

    @Override
    public SendMessage processCommand(Message message, String... arguments) {
        if (arguments.length != 2) {
            //todo some error
        } else {
            Integer gameId = Integer.parseInt(arguments[1]);
            Optional<GameBean> first = DataService.INSTANCE.getCurrentGames().stream().filter(game -> game.getId() == gameId).findFirst();
            if (first.isPresent()) {
                StringBuilder sb = new StringBuilder();
                GameBean gameBean = first.get();
                sb.append("League: _").append(gameBean.getLeague().getName()).append("_\r\n");
                sb.append(gameBean.getRadiant().getName()).append("  ").append(gameBean.getRadiantWin());
                sb.append(" : ").append(gameBean.getDireWin()).append("  ").append(gameBean.getDire().getName()).append("\r\n");

                StringBuilder s = new StringBuilder();
                for (int i = 0; i < gameBean.getRadiant().getName().length(); i++) {
                    s.append(" ");
                }
                sb.append(s).append(gameBean.getSeriesType()).append("\r\n");
                List<String> mathesId = DataService.INSTANCE.getMatchesIdByGameId(arguments[1]);
                int index = 0;
                for (String mId : mathesId) {
                    Match match = DataService.INSTANCE.getMatchById(mId);
                    appendMatchInfo(match, sb, ++index);
                }
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(sb.toString());
                sendMessage.setChatId(message.getChatId());
                sendMessage.enableMarkdown(true);
                return sendMessage;
            } else {
                //todo error
            }
        }
        return null;//todo
    }

    private void appendMatchInfo(Match match, StringBuilder sb, int index) {
        if (match != null) {
            sb.append("*Game ").append(index).append("*").append("\r\n");
            if (match.getMatchStatus() == 0) {
                sb.append("    Live: ").append("\r\n");
                sb.append("      ").append(match.getRadiantTeam().getName()).append(" ").append(match.getMatchScore()).append(" ").append(match.getDireTeam().getName()).append("\r\n");
                sb.append("      NetWorth:  ").append(match.getNetworth().get(match.getNetworth().size() - 1));
            } else if (match.getMatchStatus() == 1) {
                sb.append("    Win: ").append(match.getRadiantTeam().getName()).append("\r\n");
                sb.append("      ").append(match.getRadiantTeam().getName()).append(" ").append(match.getMatchScore()).append(" ").append(match.getDireTeam().getName()).append("\r\n");
                sb.append("      NetWorth:  ").append(match.getNetworth().get(match.getNetworth().size() - 1));
            } else if (match.getMatchStatus() == 2) {
                sb.append("    Win: ").append(match.getDireTeam().getName()).append("\r\n");
                sb.append("      ").append(match.getRadiantTeam().getName()).append(" ").append(match.getMatchScore()).append(" ").append(match.getDireTeam().getName()).append("\r\n");
                sb.append("      NetWorth:  ").append(match.getNetworth().get(match.getNetworth().size() - 1));
            }
            sb.append("\r\n");

        }

    }
}
