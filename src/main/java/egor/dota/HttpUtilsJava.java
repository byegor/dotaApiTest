package egor.dota;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Egor on 11.07.2015.
 */
public class HttpUtilsJava {

    private static final String HEROES = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/?key=9EBD51CD27F27324F1554C53BEDA17C3";
    public static final String GET_BATCH_OF_MATCHES = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?min_players=10&game_mode=1&key=9EBD51CD27F27324F1554C53BEDA17C3";
    public static final String GET_NEXT_PAGE_OF_MATCHES = GET_BATCH_OF_MATCHES + "&start_at_match_id=";
    public static final String GET_MATCH_DETAILS = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/v001/?key=9EBD51CD27F27324F1554C53BEDA17C3&match_id=";

    public static final String GET_BATCH_OF_MATCHES_BY_SEQUENCE = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistoryBySequenceNum/v0001/?min_players=10&key=9EBD51CD27F27324F1554C53BEDA17C3&start_at_match_seq_num=";


    public static JSONObject getMatchHistoryResults(long lastMatchId) {
        String requestUrl;
        if (lastMatchId == -1) {
            requestUrl = GET_BATCH_OF_MATCHES;
        } else {
            requestUrl = GET_NEXT_PAGE_OF_MATCHES + lastMatchId;
        }
        return getResponseAsJson(requestUrl);
    }

    public static JSONObject getMatchDetailsResults(long matchId) {
        return getResponseAsJson(GET_MATCH_DETAILS + matchId);
    }

    public static JSONObject getMatchDetailsResultsBySequence(long seqNum) {
        Exception re = null;
        for (int i = 0; i < 10; i++) {
            try {
                return getResponseAsJson(GET_BATCH_OF_MATCHES_BY_SEQUENCE + seqNum);
            } catch (Exception e) {
                re = e;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignore) {
                }
            }
        }
        throw new RuntimeException(re);
    }


    private static JSONObject getResponseAsJson(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            JSONObject json = null;
            try (CloseableHttpResponse response1 = httpClient.execute(httpGet)) {
                HttpEntity entity1 = response1.getEntity();
                if (entity1 != null) {
                    String s = EntityUtils.toString(entity1);
                    json = new JSONObject(s);
                }
                EntityUtils.consume(entity1);
                return json;
            } catch (Exception e) {
//                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
//            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
