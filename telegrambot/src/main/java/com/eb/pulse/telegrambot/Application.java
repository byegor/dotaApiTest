package com.eb.pulse.telegrambot;

import com.eb.pulse.telegrambot.bot.DotaStatisticsBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

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

        SpringApplication.run(Application.class, args);
    }


}
