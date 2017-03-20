package com.eb.schedule;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Egor on 14.06.2016.
 */
public class TeamImageProcessor extends ImageProcessor {

    private static final String GET_TEAM_LOGO = "http://api.steampowered.com/ISteamRemoteStorage/GetUGCFileDetails/v1/?key=D998B8BDFA96FAA893E52903D6A77EEA&appid=570&ugcid=";

    public TeamImageProcessor(String dataFolder) throws IOException {
        super(dataFolder);
    }

    @Override
    public String getImageUrl(String name) {
        return GET_TEAM_LOGO + name;
    }

    @Override
    protected InputStream getImageAsStream(String name) throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(getImageUrl(name))
                .header("accept", "application/json")
                .asJson();
        JSONObject data = jsonResponse.getBody().getObject().getJSONObject("data");
        String url = data.getString("url");
        return Unirest.get(url).asBinary().getBody();
    }
}
