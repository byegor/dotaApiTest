package com.eb.pulse.telegrambot.service;

import com.eb.pulse.telegrambot.entity.Data;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
            DataService.INSTANCE.setData(data);
        } catch (Exception e) {
            e.printStackTrace();//todo
        }
    }
}
