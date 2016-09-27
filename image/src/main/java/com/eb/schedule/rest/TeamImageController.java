package com.eb.schedule.rest;

import com.eb.schedule.TeamImageUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by Egor on 14.06.2016.
 */
@RestController
public class TeamImageController {

    @Value("${teamImageFolder:f:/nginx-1.11.1/image/team}")
    String imageFolder;

    private TeamImageUtils teamImageUtils;


    @PostConstruct
    public void init() throws IOException {
        teamImageUtils = new TeamImageUtils(imageFolder);
    }

    @RequestMapping(value = "/image/team/{id}.png", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] image(@PathVariable("id") String id) {
        return teamImageUtils.getImage(id);
    }
}
