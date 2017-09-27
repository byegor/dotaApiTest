package com.eb.pulse.telegrambot.http;

import com.eb.pulse.telegrambot.entity.Data;
import com.eb.pulse.telegrambot.service.DataService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Egor on 24.09.2017.
 */
@RestController
@RequestMapping("/v1")
public class DataHandler {



    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void setData(@RequestBody Data data) {
        DataService.INSTANCE.setData(data);
    }

}
