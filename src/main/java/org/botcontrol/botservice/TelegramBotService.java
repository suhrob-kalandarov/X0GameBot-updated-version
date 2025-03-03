package org.botcontrol.botservice;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.DeleteMessage;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.botcontrol.entities.User;
import org.botcontrol.botcommands.*;
import org.botcontrol.botcommands.botplayer.*;
import org.botcontrol.botcommands.multiplayer.InviteFriendCmd;
import org.botcontrol.botservice.msgservice.ResourceMessageManager;

import java.util.Locale;
import static java.util.Objects.requireNonNull;

import static org.botcontrol.Main.*;
import static org.botcontrol.botservice.dbservice.DB.*;
import static org.botcontrol.botservice.msgservice.Constant.*;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.*;

public class TelegramBotService {
    private static final Logger logger = LogManager.getLogger(TelegramBotService.class);
    public static void handleUpdate(Update update) {
        try {
            BotCommand command = null;
            logger.trace("Yangi update qabul qilindi (ID: {})", update.updateId());

            if (update.message() != null) {
                User user = getOrCreateUser(update);
                logger.debug("Xabar user ID: {}", user);
                String text = update.message().text();

                if (text != null){
                    logger.info("Foydalanuvchi xabari: {}", text);

                    if (text.equals(START)) {
                        logger.info("/start komandasi qabul qilindi");

                        if (user.getMessageId() != null) {
                            new WarningHandlerCmd(user).process();
                        }

                        if (ResourceMessageManager.bundle == null) {
                            logger.error("ResourceBundle yuklanmagan! Qayta yuklashga harakat qilamiz.");
                            ResourceMessageManager.loadBundle(
                                    "messages",
                                    new Locale(user.getLanguage())
                            );
                        }

                    } else {
                        new WarningHandlerCmd(user).process();
                    }

                } else {
                    telegramBot.execute(new DeleteMessage(
                            user.getUserId(),
                            user.getMessageId())
                    );
                    int newMessageId = telegramBot.execute(
                            new SendMessage(chatId, getString(WARNING_MSG))
                    ).message().messageId();
                    user.setMessageId(newMessageId);
                }
                new MainMenuCmd(user).process();
            }

            if (update.callbackQuery() != null) {
                User user = getOrCreateUser(update);
                CallbackQuery callbackedQuery = update.callbackQuery();

                logger.debug("CallbackQuery data: {}", callbackedQuery.data());

                String data = callbackedQuery.data();

                if (data.startsWith(CELL)) {
                    command = new InGame(user, data);

                } else if (data.startsWith(LANG)) {
                    command = new ChangeLanguageCmd(user, data);

                } else if (data.equals(PLAY_WITH_BOT_BTN)) {
                    command = new SelectionSymbolCmd(user);

                } else if (data.equals(PLAY_WITH_FRIEND_BTN)) {
                    command = new InviteFriendCmd(user);
                    
                } else if (data.startsWith(CHOSEN_SYMBOL)) {
                    command = new PlayGameCmd(user, data);

                } else if (data.equals(LANGUAGE_MSG)) {
                    command = new LanguageMainMenuCmd(user);

                } else if (data.equals(STATISTICS_BTN)) {
                    command = new StatisticsCmd(user);

                } else if (data.startsWith(BACK)) {
                    command = new BackButtonCmd(user, data);

                } else if (data.equals(SUPPORT_BTN)) {
                    command = new AssistanceCmd(user);

                } else if (data.equals(DIFFICULTY_LEVEL_BTN)) {
                    command = new DifficultyLevelMainMenuCmd(user);

                } else if (data.startsWith("level_")) {
                    command = new ChangeDifficultyLevelCmd(user,data);
                }
                requireNonNull(command).process();
            }
        } catch (Exception e){
            logger.error("Xatolik yuz berdi: {}", e.getMessage(), e);
        }
    }
}

