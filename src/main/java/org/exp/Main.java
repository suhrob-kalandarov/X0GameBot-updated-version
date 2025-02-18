package org.exp;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exp.botservice.TelegramBotService;
import org.exp.botservice.logging.LogScheduler;

import java.util.concurrent.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static TelegramBot telegramBot = new TelegramBot("bot-token"); /// 7958534479:AAFoiNaYc548GKjSSTXnQuLOUwL8Vo2kfeM
    public static String chatId = "channel-user"; /// Log channel @Game0XX0
    public static ExecutorService executorService = Executors.newFixedThreadPool(50);

    public static void main(String[] args) {
        LogScheduler.logSender(chatId, telegramBot.getToken());

        System.out.println("✅ Log jo‘natish jadvali ishga tushdi!");

        logger.info("Telegram bot ishga tushmoqda...");
        try {
            telegramBot.removeGetUpdatesListener();
            logger.debug("Oldingi updates listenerlar tozalandi");

            telegramBot.setUpdatesListener(updates -> {
                logger.info("{} ta yangi update qabul qilindi", updates.size());

                for (Update update : updates) {
                    logger.debug("Update ID {} qabul qilindi", update.updateId());
                    if (update.message() != null) {
                        logger.trace("Xabar matni: {}", update.message().text());
                    }

                    executorService.execute(() -> {
                        try {
                            TelegramBotService.handleUpdate(update);
                            logger.debug("Update ID {} muvaffaqiyatli qayta ishlandi", update.updateId());
                        } catch (Exception e) {
                            logger.error("Update ID {} qayta ishlashda xatolik: {}", update.updateId(), e.getMessage(), e);
                        }
                    });
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            });

            logger.info("Bot muvaffaqiyatli ishga tushdi!");
        } catch (Exception e) {
            logger.fatal("Botni ishga tushirishda jiddiy xatolik: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}