package egor.dota;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Egor on 11.07.2015.
 */
public class RetrieveStatisticsBasedOnSequenceJob implements Runnable {
    @Override
    public void run() {
        List<Hero> heroes = DataBaseHelper.INSTANCE.getHeroes();
        Map<Integer, Hero> heroMap = new HashMap<>();
        for (Hero hero : heroes) {
            heroMap.put(hero.getId(), hero);
        }
        for (int i = 0; i < 90000; i++) {
            long latRecordedSequenceNum = DataBaseHelper.INSTANCE.getLatRecordedMatchId();
            JSONObject matchHistoryJson = HttpUtils.getMatchDetailsResultsBySequence(latRecordedSequenceNum);
            List<MatchSequence> matches = parseMatchHistory(matchHistoryJson, heroMap);
            MatchSequence lastMatch = matches.remove(matches.size() - 1);
            long lastSequenceNum = lastMatch.getSequence();
            List<MatchResult> results = new ArrayList<>();
            for (MatchSequence match : matches) {
                if (match.isNormalMatch()) {
                    results.add(new MatchResult(match.getRadiant(), match.getDire(), match.isRadiantWin(), match.getId()));
                }
            }
            DataBaseHelper.INSTANCE.insertMatchResults(results, lastSequenceNum);
        }
    }


    private static List<MatchSequence> parseMatchHistory(JSONObject matchHistory, Map<Integer, Hero> heros) {
        List<MatchSequence> matches = new ArrayList<>(100);
        JSONArray mathesList = matchHistory.getJSONObject("result").getJSONArray("matches");
        for (int i = 0; i < mathesList.length(); i++) {
            JSONObject matchJson = mathesList.getJSONObject(i);
            Long id = matchJson.getLong("match_id");
            Long sequence = matchJson.getLong("match_seq_num");
            Integer duration = matchJson.getInt("duration");
            Boolean radiantWin = matchJson.getBoolean("radiant_win");
            MatchSequence match = new MatchSequence(id, sequence, radiantWin, duration);
            JSONArray players = matchJson.getJSONArray("players");
            for (int j = 0; j < players.length(); j++) {
                JSONObject player = players.getJSONObject(j);
                int heroId = player.getInt("hero_id");
                int slot = player.getInt("player_slot");
                Hero hero = heros.get(heroId);
                if (slot <= 4) {
                    match.addRadiant(hero);
                } else {
                    match.addDire(hero);
                }
            }
            matches.add(match);
        }
        return matches;

    }

}
