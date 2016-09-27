package com.eb.schedule.rest;

import com.eb.schedule.ItemImageUtils;
import com.eb.schedule.TeamImageUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by Egor on 14.06.2016.
 */
@RestController
public class ItemImageController {

    @Value("${itemImageFolder:f:/nginx-1.11.1/image/item}")
    String imageFolder;

    private ItemImageUtils itemImageUtils;


    @PostConstruct
    public void init() throws IOException {
        itemImageUtils = new ItemImageUtils(imageFolder);
    }

    @RequestMapping(value = "/image/item/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] image(@PathVariable("id") String id) {
        return itemImageUtils.getImage(id);
    }
}
