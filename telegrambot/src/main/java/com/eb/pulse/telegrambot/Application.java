package com.eb.pulse.telegrambot;

import com.eb.pulse.telegrambot.bot.DotaStatisticsBot;
import com.eb.pulse.telegrambot.service.GrepDataTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Egor on 14.06.2016.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new DotaStatisticsBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        runDataTask(context);
    }



    private static void runDataTask(ApplicationContext context) {

        RestTemplate restTemplate = new RestTemplate();
        String dataServerUrl = context.getEnvironment().getProperty("data.server.url");
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new GrepDataTask(restTemplate, dataServerUrl), 0, 30, TimeUnit.SECONDS);
    }


}
