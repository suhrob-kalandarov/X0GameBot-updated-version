package org.exp.botservice;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;

import org.exp.botservice.commands.*;
import org.exp.botservice.servicemessages.Constant;

import org.exp.botservice.servicemessages.ResourceMessageManager;
import org.exp.entity.TgUser;

import java.util.Locale;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.exp.Main.telegramBot;
import static org.exp.botservice.database.DB.*;
import static org.exp.botservice.servicemessages.Constant.*;
import static org.exp.botservice.servicemessages.ResourceMessageManager.*;

public class TelegramBotService {
    public static void handleUpdate(Update update) {
        try {
            if (update.message() != null) {
                Long chatId = update.message().chat().id();
                TgUser tgUser = getOrCreateUser(chatId);
                String text = update.message().text();
                BotCommand command = null;

                System.out.println("Update object: " + update);

                if (text != null && text.equals("/start")) {
                    tgUser.setLanguage("en"); /// default language 'english'

                    ResourceMessageManager.setLocale(new Locale("en"));

                    command = new CabinetCmd(tgUser);
                    if (tgUser.getMessageId()!=null){
                        telegramBot.execute(new DeleteMessage(
                                tgUser.getChatId(),
                                tgUser.getMessageId())
                        );
                        restoringTgUserList();
                    }

                    ///Terminal log
                    System.out.println("\n" + tgUser);
                    System.out.println("Update object: " + update);

                } else if (text != null && text.equals("/admin") && Objects.equals(tgUser.getChatId(), "6513286717")) {
                    command = new AdminCmd(tgUser, update);

                    ///Terminal log
                    System.out.println("\n" + tgUser);

                } else {
                    telegramBot.execute(new SendMessage(chatId, getString(Constant.WARNING_MSG)));
                    command = new CabinetCmd(tgUser);
                }
                requireNonNull(command).process();
            }
            
            if (update.callbackQuery() != null){
                BotCommand command = null;
                Long chatId = update.callbackQuery().from().id();
                TgUser tgUser = getOrCreateUser(chatId);
                String data = update.callbackQuery().data();

                if (data.startsWith("cell_")) {
                    command = new InGame(tgUser, update, data);

                    ///Terminal log
                    System.out.println("\n" + tgUser);

                } else if (data.startsWith("lang_")) {
                    command = new LanguageChangerCmd(tgUser, data);

                    ///Terminal log
                    System.out.println("\n" + tgUser);

                } else if (data.equals(PLAY_BTN)) {
                    command = new SelectionSymbolCmd(tgUser, update);

                    ///Terminal log
                    System.out.println("\n" + tgUser);

                } else if (data.equals(LANGUAGE_MSG)) {
                    command = new LanguageCmd(tgUser);

                    ///Terminal log
                    System.out.println("\n" + tgUser);

                } else if (data.startsWith("CHOOSE_")) {
                    command = new PlayGameCmd(tgUser, update, data);

                    ///Terminal log
                    System.out.println("\n" + tgUser);

                } else if (data.startsWith("back")) {
                    command = new BackButtonCmd(tgUser, update, data);

                    ///Terminal log
                    System.out.println("\n" + tgUser);
                }
                requireNonNull(command).process();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}