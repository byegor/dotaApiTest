package com.eb.schedule;

import java.io.IOException;

/**
 * Created by Egor on 14.06.2016.
 */
public class HeroImageProcessor extends ImageProcessor {

    private static final String GET_HERO_LOGO = "http://cdn.dota2.com/apps/dota2/images/heroes/%s_full.png";

    public HeroImageProcessor(String dataFolder) throws IOException {
        super(dataFolder);
    }

    @Override
    public String getImageUrl(String name) {
        return String.format(GET_HERO_LOGO, name);
    }
}
