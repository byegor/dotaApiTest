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
public class RetrieveStatisticsJob implements Runnable {
    @Override
    public void run() {
        List<Hero> heroes = DataBaseHelper.INSTANCE.getHeroes();
        Map<Integer, Hero> heroMap = new HashMap<>();
        for (Hero hero : heroes) {
            heroMap.put(hero.getId(), hero);
        }
        boolean reachEnd = false;
        while (!reachEnd) {
            long latRecordedMatchId = DataBaseHelper.INSTANCE.getLatRecordedMatchId();
            JSONObject matchHistoryJson = HttpUtils.getMatchHistoryResults(latRecordedMatchId);
            List<Match> matches = parseMatchHistory(matchHistoryJson, heroMap);
            Match lastMatch = matches.remove(matches.size() - 1);
            long last = lastMatch.getId();
            List<MatchResult> results = new ArrayList<>();
            for (Match match : matches) {
                if (latRecordedMatchId == match.getId()) {
                    reachEnd = true;
                    break;
                } else {
                    MatchResult matchResult = getMatchResult(match);
                    if (matchResult != null) {
                        results.add(matchResult);
                    }
                }
            }
            DataBaseHelper.INSTANCE.insertMatchResults(results, last);
            int remainingResults = matchHistoryJson.getJSONObject("result").getInt("results_remaining");
            if (!reachEnd) {
                reachEnd = remainingResults == 0;
            }
        }
    }

    public static MatchResult getMatchResult(Match match) {
        if (match.isNormalMatch()) {
            JSONObject matchDetailResponse = HttpUtils.getMatchDetailsResults(match.getId());
            JSONObject result = matchDetailResponse.optJSONObject("result");
            if (result != null) {
                boolean radiantWin = result.getBoolean("radiant_win");
                return new MatchResult(match.getRadiant(), match.getDire(), radiantWin, match.getId());
            }
        }
        return null;
    }


    private static List<Match> parseMatchHistory(JSONObject matchHistory, Map<Integer, Hero> heros) {
        List<Match> matches = new ArrayList<>(25);
        JSONArray mathesList = matchHistory.getJSONObject("result").getJSONArray("matches");
        for (int i = 0; i < mathesList.length(); i++) {
            JSONObject matchJson = mathesList.getJSONObject(i);
            Long id = matchJson.getLong("match_id");
            Match match = new Match(id);
            JSONArray players = matchJson.getJSONArray("players");
            for (int j = 0; j < players.length(); j++) {
                JSONObject player = players.getJSONObject(j);
                int heroId = player.getInt("hero_id");
                Hero hero = heros.get(heroId);
                //todo
                if (j <= 4) {
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
