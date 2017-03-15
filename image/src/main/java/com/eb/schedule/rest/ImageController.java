package com.eb.schedule.rest;

import com.eb.schedule.HeroImageProcessor;
import com.eb.schedule.ItemImageProcessor;
import com.eb.schedule.TeamImageProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by Egor on 14.06.2016.
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    @Value("${itemPaths:f:/nginx-1.11.1/image/item}")
    String itemPaths;

    @Value("${teamPaths:f:/nginx-1.11.1/image/team}")
    String teamPaths;

    @Value("${heroPaths:f:/nginx-1.11.1/image/hero}")
    String heroPaths;

    private TeamImageProcessor teamImageUtils;
    private ItemImageProcessor itemImageUtils;
    private HeroImageProcessor heroImageUtils;


    @PostConstruct
    public void init() throws IOException {
        itemImageUtils = new ItemImageProcessor(itemPaths);
        teamImageUtils = new TeamImageProcessor(teamPaths);
        heroImageUtils = new HeroImageProcessor(heroPaths);
    }

    @RequestMapping(value = "/item/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] item(@PathVariable("id") String id) {
        return itemImageUtils.getImage(id);
    }

    @RequestMapping(value = "/team/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] team(@PathVariable("id") String id) {
        return teamImageUtils.getImage(id);
    }

    @RequestMapping(value = "/hero/{name}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] hero(@PathVariable("name") String name) {
        return heroImageUtils.getImage(name);
    }
}
