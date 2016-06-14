package com.eb.schedule;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Egor on 14.06.2016.
 */
@RestController
public class ImageController {

    @RequestMapping("/image/{id}")
    public byte[] index(@PathVariable("id") String id) {
        return ImageUtils.getImage(id);
    }
}
