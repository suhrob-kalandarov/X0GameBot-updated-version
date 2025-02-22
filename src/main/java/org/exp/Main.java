package org.exp;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import com.pengrad.telegrambot.request.SendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exp.botservice.TelegramBotService;
import org.exp.botservice.logging.LogScheduler;
import org.exp.database.DB;
import org.exp.entity.tguserentities.TgUser;

import java.util.concurrent.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static TelegramBot telegramBot = new TelegramBot("7937505866:AAECNv4B3FqAMfLns6_8ZHVZMCOXFqWPoaI"); /// 7958534479:AAFoiNaYc548GKjSSTXnQuLOUwL8Vo2kfeM
    public static String chatId = "@Game0XX0"; /// Log channel @Game0XX0
    public static ExecutorService executorService = Executors.newFixedThreadPool(50);

    public static void main(String[] args) {

        /*{
            sendMsgToUsers();
        }*/

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

    private static void sendMsgToUsers() {
        for (TgUser tgUser : DB.getAllUsersFromDatabase()) {
            telegramBot.execute(
                    new SendMessage(
                            tgUser.getChatId(),
                            "Hello, @" + tgUser.getUsername() +
                                    "!\nYou haven't been active in the bot lately. Let's test your skills again! Click /start to check your chances of defeating the bot.\n" +
                                    "\n" +
                                    "Also, new updates are coming soon! Start playing now to be ready for them."
                    )
            );
            logger.info("Xabar muvaffaqiyatli userga yuborildi!");
        }
        logger.info("Xabar barchaga muvaffaqiyatli yuborildi!");
    }
}