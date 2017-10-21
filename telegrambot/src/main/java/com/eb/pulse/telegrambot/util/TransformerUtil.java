package com.eb.pulse.telegrambot.util;

import com.eb.schedule.shared.bean.GameBean;
import com.eb.schedule.shared.bean.Match;
import com.eb.schedule.shared.bean.Player;
import com.vdurmont.emoji.EmojiParser;

import java.util.List;

/**
 * Created by Egor on 24.09.2017.
 */
public class TransformerUtil {

    public static final String WIN = "    Win: ";
    public static final String LIVE = "    Live: ";
    public static final String NET_WORTH = "      NetWorth:  ";
    public static final String WIN_BOLD = "    *Win*: ";
    public static final String LIVE_BOLD = "    *Live*: ";
    public static final String NET_WORTH_EMOJI = EmojiParser.parseToUnicode("     :moneybag:  ");

    public static String transform(GameBean gameBean) {
        StringBuilder sb = new StringBuilder();
        sb.append(gameBean.radiant.getName()).append("   ")
                .append(gameBean.radiantWin).append(" : ").append(gameBean.direWin).append("  ")
                .append(gameBean.dire.getName());
        return sb.toString();
    }

    public static String transformMatchFoGeneralInfo(Match match) {
        return getMatchInfo(match, WIN, LIVE, NET_WORTH);
    }

    public static String transformMatchInfo(Match match) {
        return getMatchInfo(match, WIN_BOLD, LIVE_BOLD, NET_WORTH_EMOJI);
    }

    private static String getMatchInfo(Match match, String win, String live, String netWorth){
        StringBuilder sb = new StringBuilder();
        if (match != null) {

            if (match.getMatchStatus() == 0) {
                sb.append(live).append("\r\n");
                sb.append("      ").append(match.getRadiantTeam().getName()).append(" ").append(match.getMatchScore()).append(" ").append(match.getDireTeam().getName()).append("\r\n");
            } else if (match.getMatchStatus() == 1) {
                sb.append(win).append(match.getRadiantTeam().getName()).append("\r\n");
                sb.append("      ").append(match.getRadiantTeam().getName()).append(" ").append(match.getMatchScore()).append(" ").append(match.getDireTeam().getName()).append("\r\n");
            } else if (match.getMatchStatus() == 2) {
                sb.append(win).append(match.getDireTeam().getName()).append("\r\n");
                sb.append("      ").append(match.getRadiantTeam().getName()).append(" ").append(match.getMatchScore()).append(" ").append(match.getDireTeam().getName()).append("\r\n");
            }
            int value = 0;
            List<Integer> networth = match.getNetworth();
            if (networth != null && !networth.isEmpty()) {
                value = networth.get(networth.size() - 1);
            }
            sb.append(netWorth).append(value).append("\r\n");
        }
        return sb.toString();
    }


    public static String transformPlayerForPick(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Player player : players) {
                sb.append(player.getName()).append(":  _").append(player.getHero().getName()).append("_\r\n");
            }
            return sb.toString();
        }

    }
}
