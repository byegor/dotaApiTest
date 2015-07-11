package egor.dota;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ���� on 09.07.2015.
 */
//todo remember start
public class DotaApi {

    private static final String HEROES = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/?key=9EBD51CD27F27324F1554C53BEDA17C3";
    public static final String GET_BATCH_OF_MATCHES = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?min_players=10&key=9EBD51CD27F27324F1554C53BEDA17C3";
    public static final String GET_NEXT_PAGE_OF_MATCHES = GET_BATCH_OF_MATCHES + "&start_at_match_id=";
    public static final String GET_MATCH_DETAILS = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/v001/?key=9EBD51CD27F27324F1554C53BEDA17C3&match_id="; //1622173365

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        List<Hero> heroes = DataBaseHelper.INSTANCE.getHeroes();
        Map<Integer, Hero> heroMap = new HashMap<>();
        for (Hero hero : heroes) {
            heroMap.put(hero.getId(), hero);
        }

        JSONObject matchHistory = getResponseAsJson(httpclient, GET_BATCH_OF_MATCHES);
        List<Match> matches = parseMatchHistory(matchHistory, heroMap);
        Match lastMatch = matches.get(matches.size() - 1);
        long last = lastMatch.getId();
        List<MatchResult> results = new ArrayList<>();
        for (Match match : matches) {
            MatchResult matchResult = getMatchResult(match, httpclient);
            if(matchResult != null){
                results.add(matchResult);
            }
        }
        DataBaseHelper.INSTANCE.insertMatchResults(results, last);
    }

    public static MatchResult getMatchResult(Match match, CloseableHttpClient httpClient){
        if(match.isNormalMatch()) {
            JSONObject detailsResponce = getResponseAsJson(httpClient, GET_MATCH_DETAILS + match.getId());
            JSONObject result = detailsResponce.optJSONObject("result");
            if (result != null) {
                boolean radiantWin = result.getBoolean("radiant_win");
                return new MatchResult(match.getRadiant(), match.getDire(), radiantWin);
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
            for (int j = 0; j < 10; j++) {
                JSONObject player = players.getJSONObject(j);
                int heroId = player.getInt("hero_id");
                Hero hero = heros.get(heroId);
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

    private static Map<String, String> getFightResult(JSONObject matchHistory, Map<Integer, String> heroNames) {
        Map<String, String> teams = new HashMap<>();
        JSONArray mathesList = matchHistory.getJSONObject("result").getJSONArray("matches");
        for (int i = 0; i < mathesList.length(); i++) {
            JSONObject match = mathesList.getJSONObject(i);
            JSONArray players = match.getJSONArray("players");
            if (players.length() == 10) {
                String radiant = "";
                String dire = "";
                for (int j = 0; j < 10; j++) {
                    JSONObject player = players.getJSONObject(j);
                    int heroId = player.getInt("hero_id");
                    String heroName = heroNames.get(heroId);
                    if (j <= 4) {
                        radiant += heroName + " ";
                    } else {
                        dire += heroName + " ";
                    }
                }
                teams.put(radiant, dire);
            }
        }
        return teams;
    }

    private static JSONObject getResponseAsJson(CloseableHttpClient httpclient, String url) {
        HttpGet httpGet = new HttpGet(url);
        JSONObject json = null;
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            HttpEntity entity1 = response1.getEntity();
            if (entity1 != null) {
                String s = EntityUtils.toString(entity1);
                json = new JSONObject(s);
            }
            EntityUtils.consume(entity1);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}
