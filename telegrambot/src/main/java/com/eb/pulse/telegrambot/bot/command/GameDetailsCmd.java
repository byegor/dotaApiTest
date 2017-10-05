package com.eb.pulse.telegrambot.bot.command;

import com.eb.pulse.telegrambot.service.DataService;
import com.eb.schedule.shared.bean.GameBean;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

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
        if(arguments.length!=2){
            //todo some error
        }else{
            Integer gameId = Integer.parseInt(arguments[1]);
            Optional<GameBean> first = DataService.INSTANCE.getCurrentGames().stream().filter(game -> game.getId() == gameId).findFirst();
            if (first.isPresent()) {
                StringBuilder sb = new StringBuilder();
                GameBean gameBean = first.get();
                sb.append("League: ").append(gameBean.getLeague().getName()).append("\r\n");
                sb.append(gameBean.getRadiant().getName()).append("  ").append(gameBean.getRadiantWin());
                sb.append(" : ").append(gameBean.getDireWin()).append("  ").append(gameBean.getDire().getName()).append("\r\n");

                StringBuilder s = new StringBuilder("             ");
                for (int i = 0; i < gameBean.getRadiant().getName().length(); i++) {
                    s.append(" ");
                }
                sb.append(s).append(gameBean.getSeriesType()).append("\r\n");

                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(sb.toString());
                sendMessage.setChatId(message.getChatId());
                return sendMessage;
            }else{
                //todo error
            }
        }
        return null;//todo
    }
}
