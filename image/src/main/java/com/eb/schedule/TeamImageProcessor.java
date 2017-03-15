package com.eb.schedule;

import java.io.IOException;

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
}
