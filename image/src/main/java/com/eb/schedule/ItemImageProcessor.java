package com.eb.schedule;

import java.io.IOException;

/**
 * Created by Egor on 14.06.2016.
 */
public class ItemImageProcessor extends ImageProcessor {

    private static final String GET_ITEM_LOGO = "http://media.steampowered.com/apps/dota2/images/items/%s_lg.png";

    public ItemImageProcessor(String dataFolder) throws IOException {
        super(dataFolder);
    }

    @Override
    public String getImageUrl(String name) {
        return String.format(GET_ITEM_LOGO, name);
    }
}
