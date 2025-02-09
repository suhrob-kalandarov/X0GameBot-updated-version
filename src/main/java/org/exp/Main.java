package org.exp;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.exp.botservice.TelegramBotService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static TelegramBot telegramBot = new TelegramBot("7891432527:AAFeVZVjm8xkEcgQXtmU1FdIXay4zzJ8mZY");
    public static ExecutorService executorService = Executors.newFixedThreadPool(50);
    public static void main(String[] args) {
        telegramBot.removeGetUpdatesListener();
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                executorService.execute(() -> {
                    TelegramBotService.handleUpdate(update);
                });
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}