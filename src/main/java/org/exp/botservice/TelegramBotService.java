package org.exp.botservice;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.commands.botcommands.*;
import org.exp.botservice.servicemessages.ResourceMessageManager;
import org.exp.database.DB;
import org.exp.entity.tguserentities.TgUser;

import java.util.Locale;

import static java.util.Objects.requireNonNull;
import static org.exp.Main.telegramBot;
import static org.exp.botservice.servicemessages.Constant.*;
import static org.exp.botservice.servicemessages.ResourceMessageManager.*;
import static org.exp.database.DB.*;

public class TelegramBotService {
    private static final Logger logger = LogManager.getLogger(TelegramBotService.class);

    public static void handleUpdate(Update update) {
        try {
            logger.trace("Yangi update qabul qilindi (ID: {})", update.updateId());

            if (update.message() != null) {
                Long chatId = update.message().chat().id();
                logger.debug("Xabar chat ID: {}", chatId);

                TgUser tgUser = getOrCreateUser(chatId);
                tgUser.setUsername(update.message().chat().username());
                DB.updateUsername(tgUser.getChatId(), tgUser.getUsername());
                String text = update.message().text();
                BotCommand command = null;

                if (text != null){
                    logger.info("Foydalanuvchi xabari: {}", text);
                    if (text.equals(START)) {
                        logger.info("/start komandasi qabul qilindi");
                        if (tgUser.getMessageId() != null) {
                            telegramBot.execute(new DeleteMessage(
                                    tgUser.getChatId(),
                                    tgUser.getMessageId())
                            );

                            String language = DB.getLanguage(tgUser.getChatId());

                            if (language == null || language.isEmpty()) {
                                language = "en"; // Default til
                            }

                            tgUser.setLanguage(language);
                            DB.updateLanguage(tgUser.getChatId(), tgUser.getLanguage());
                        }

                        if (tgUser.getLanguage() != null) {
                            setLocale(new Locale(tgUser.getLanguage()));
                        } else {
                            logger.error("tgUser.getLanguage() null! Default til `en` o‘rnatildi.");
                            setLocale(new Locale("en"));
                        }

                        if (ResourceMessageManager.bundle == null) {
                            logger.error("ResourceBundle yuklanmagan! Qayta yuklashga harakat qilamiz.");
                            ResourceMessageManager.loadBundle("messages", new Locale(tgUser.getLanguage()));
                        }
                        command = new CabinetCmd(tgUser);

                    } else if (text.equals("message_") && isAdmin(tgUser)) {

                        

                    } else {
                        new WarningHandlerCmd(tgUser).process();
                        command = new CabinetCmd(tgUser);
                    }

                } else {
                    telegramBot.execute(new DeleteMessage(
                            tgUser.getChatId(),
                            tgUser.getMessageId())
                    );
                    int newMessageId = telegramBot.execute(
                            new SendMessage(chatId, getString(WARNING_MSG))
                    ).message().messageId();
                    tgUser.setMessageId(newMessageId);

                    command = new CabinetCmd(tgUser);
                }
                requireNonNull(command).process();
            }

            if (update.callbackQuery() != null){
                logger.debug("CallbackQuery data: {}", update.callbackQuery().data());
                BotCommand command = null;
                Long chatId = update.callbackQuery().from().id();
                TgUser tgUser = getOrCreateUser(chatId);
                String data = update.callbackQuery().data();

                if (data.startsWith(CELL)) {
                    command = new InGame(tgUser, update, data);

                } else if (data.startsWith(LANG)) {
                    command = new LanguageChangerCmd(tgUser, data);

                } else if (data.equals(PLAY_BTN)) {
                    command = new SelectionSymbolCmd(tgUser, update);

                } else if (data.startsWith(CHOSEN_SYMBOL)) {
                    command = new PlayGameCmd(tgUser, update, data);

                } else if (data.equals(LANGUAGE_MSG)) {
                    command = new LanguageCmd(tgUser);

                } else if (data.equals(STATISTICS_BTN)) {
                    command = new StatisticsCmd(tgUser);

                } else if (data.startsWith(BACK)) {
                    command = new BackButtonCmd(tgUser, data);

                } else if (data.equals(SUPPORT_BTN)) {
                    command = new AssistanceCmd(tgUser);
                }
                requireNonNull(command).process();
            }
        } catch (Exception e){
            logger.error("Xatolik yuz berdi: {}", e.getMessage(), e);
        }
    }
}


/*
package org.exp.botservice;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;

import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.commands.botcommands.*;
import org.exp.botservice.servicemessages.ResourceMessageManager;
import org.exp.database.DB;
import org.exp.entity.tguserentities.TgUser;

import java.util.Locale;

import static java.util.Objects.requireNonNull;
import static org.exp.Main.telegramBot;
import static org.exp.botservice.servicemessages.Constant.*;
import static org.exp.botservice.servicemessages.ResourceMessageManager.*;
import static org.exp.database.DB.*;

public class TelegramBotService {
    public static void handleUpdate(Update update) {
        try {
            if (update.message() != null) {
                Long chatId = update.message().chat().id();
                TgUser tgUser = getOrCreateUser(chatId);
                tgUser.setUsername(update.message().chat().username());
                DB.updateUsername(tgUser.getChatId(), tgUser.getUsername());
                String text = update.message().text();
                BotCommand command = null;

                if (text != null){
                    if (text.equals(START)) {
                        if (tgUser.getMessageId() != null) {
                            telegramBot.execute(new DeleteMessage(
                                    tgUser.getChatId(),
                                    tgUser.getMessageId())
                            );

                            String language = DB.getLanguage(tgUser.getChatId());

                            if (language == null || language.isEmpty()) {
                                language = "en"; // Default til
                            }

                            tgUser.setLanguage(language);
                            DB.updateLanguage(tgUser.getChatId(), tgUser.getLanguage());
                        }

                        if (tgUser.getLanguage() != null) {
                            setLocale(new Locale(tgUser.getLanguage()));
                        } else {
                            System.err.println("Xatolik: tgUser.getLanguage() null! Default til `en` o‘rnatildi.");
                            setLocale(new Locale("en"));
                        }

                        if (ResourceMessageManager.bundle == null) {
                            System.err.println("Xatolik: ResourceBundle yuklanmagan! Uni qayta yuklashga harakat qilamiz.");
                            ResourceMessageManager.loadBundle("messages", new Locale(tgUser.getLanguage()));
                        }
                        command = new CabinetCmd(tgUser);

                    } else {
                        new WarningHandlerCmd(tgUser).process();
                        command = new CabinetCmd(tgUser);
                    }

                } else {
                    telegramBot.execute(new DeleteMessage(
                            tgUser.getChatId(),
                            tgUser.getMessageId())
                    );
                    int newMessageId = telegramBot.execute(
                            new SendMessage(chatId, getString(WARNING_MSG))
                    ).message().messageId();
                    tgUser.setMessageId(newMessageId);

                    command = new CabinetCmd(tgUser);
                }
                requireNonNull(command).process();
            }
            
            if (update.callbackQuery() != null){
                BotCommand command = null;
                Long chatId = update.callbackQuery().from().id();
                TgUser tgUser = getOrCreateUser(chatId);
                String data = update.callbackQuery().data();

                if (data.startsWith(CELL)) {
                    command = new InGame(tgUser, update, data);

                } else if (data.startsWith(LANG)) {
                    command = new LanguageChangerCmd(tgUser, data);

                } else if (data.equals(PLAY_BTN)) {
                    command = new SelectionSymbolCmd(tgUser, update);

                } else if (data.startsWith(CHOSEN_SYMBOL)) {
                    command = new PlayGameCmd(tgUser, update, data);

                } else if (data.equals(LANGUAGE_MSG)) {
                    command = new LanguageCmd(tgUser);

                } else if (data.equals(STATISTICS_BTN)) {
                    command = new StatisticsCmd(tgUser);

                } else if (data.startsWith(BACK)) {
                    command = new BackButtonCmd(tgUser, data);

                } else if (data.equals(SUPPORT_BTN)) {
                    command = new AssistanceCmd(tgUser);
                }
                requireNonNull(command).process();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}*/
