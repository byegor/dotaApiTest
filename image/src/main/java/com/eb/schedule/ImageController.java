package com.eb.schedule;

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
public class ImageController {

    @Value("${imageFolder:f:/nginx-1.11.1/image}")
    String imageFolder;

    private ImageUtils imageUtils;


    @PostConstruct
    public void init() throws IOException {
        imageUtils = new ImageUtils(imageFolder);
    }

    @RequestMapping(value = "/image/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] image(@PathVariable("id") String id) {
        return imageUtils.getImage(id);
    }
}
