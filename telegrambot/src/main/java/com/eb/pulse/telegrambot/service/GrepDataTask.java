package com.eb.pulse.telegrambot.service;

import com.eb.pulse.telegrambot.entity.Data;
import com.eb.schedule.shared.bean.GameBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Egor on 03.10.2017.
 */

public class GrepDataTask implements Runnable {

    private RestTemplate restTemplate;
    private String dataServerUrl;

    public GrepDataTask(RestTemplate restTemplate, String dataServerUrl) {
        this.restTemplate = restTemplate;
        this.dataServerUrl = dataServerUrl;
    }

    @Override
    public void run() {
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(dataServerUrl, String.class);
            String body = entity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            Data data = mapper.readValue(body, Data.class);
            Map<String, List<String>> next = data.getCurrentGames();

            List<GameBean> gameBeanList = new ArrayList<>();
            for (List<String> gamesPerDay : next.values()) {
                for (String str : gamesPerDay) {
                    GameBean gameBean = mapper.readValue(str, GameBean.class);
                    gameBeanList.add(gameBean);
                }
            }
            DataService.INSTANCE.setCurrentGames(gameBeanList);

        } catch (Exception e) {
            e.printStackTrace();//todo
        }
    }
}
