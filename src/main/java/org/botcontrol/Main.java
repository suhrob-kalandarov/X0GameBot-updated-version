package org.botcontrol;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.botcontrol.entities.User;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.botservice.TelegramBotService;
import org.botcontrol.botservice.logservice.LogScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static TelegramBot telegramBot = new TelegramBot("BOT_TOKEN");
    public static String chatId = "LOGS_PUBLIC_TELEGRAM_CHANNEL";
    public static ExecutorService executorService = Executors.newFixedThreadPool(50);

    public static void main(String[] args) {
        LogScheduler.logSender(chatId, telegramBot.getToken());

       /* {
            sendingStartMsgToUsers();
        }*/

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
            e.printStackTrace();
        }
    }

    private static void sendingStartMsgToUsers() {
        for (User user : DB.getAllUsersFromDatabase()) {
            if (!user.getUserId().toString().startsWith("-100")){
                telegramBot.execute(
                        new SendMessage(
                                user.getUserId(),
                                """
                                        🎉 Dear user, we are thrilled to share some exciting news with you! 🎉
                                        
                                        Our bot's 2.0 Demo Version has successfully launched! 🚀
                                        This update brings you a host of new features, improved functionality, and a more user-friendly interface.
                                        
                                        👉 New features include:
                                        * Faster and more efficient performance ⚡
                                        * New game modes and challenges 🎮
                                        * Enhanced usability and interface improvements 🖥️
                                        
                                        Coming soon:
                                        * Play with a friend feature! 👫🎲
                                        Invite your friends and enjoy the bot together. Stay tuned for this exciting addition!
                                        
                                        To get started, simply type:
                                        👉 /start
                                        
                                        Your support means the world to us. Thank you! ❤️"""
                        )
                );
            }
        }
    }
}