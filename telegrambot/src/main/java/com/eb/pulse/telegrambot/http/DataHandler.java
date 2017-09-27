package com.eb.pulse.telegrambot.http;

import com.eb.pulse.telegrambot.entity.Data;
import com.eb.pulse.telegrambot.service.DataService;
import com.eb.schedule.shared.bean.Match;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Egor on 24.09.2017.
 */
@RestController
@RequestMapping("/v1")
public class DataHandler {

ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void setData(@RequestBody String str) throws IOException {
        JSONObject jsonObject = new JSONObject(str);
        JSONObject currentMatches = jsonObject.getJSONObject("currentMatches");
        Set keys = currentMatches.keySet();
        Map<String, Match> matchesById = new HashMap<>();
        for (Object key : keys) {
            String o = (String) currentMatches.get((String)key);
            Match match = mapper.readValue(o, Match.class);
            matchesById.put((String)key, match);
        }
        JSONObject games = jsonObject.getJSONObject("currentGames");


        DataService.INSTANCE.setData(new Data());
    }

}
