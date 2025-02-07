package org.exp.botservice;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;

import org.exp.botservice.commands.*;
import org.exp.botservice.commands.admincommands.AdminCmd;
import org.exp.botservice.commands.admincommands.AdminPanelCmd;

import org.exp.botservice.servicemessages.ResourceMessageManager;
import org.exp.entity.TgUser;

import java.util.Locale;

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
                tgUser.setUsername(update.message().chat().username());
                String text = update.message().text();
                BotCommand command = null;

                //System.out.println("Update object: " + update);

                if (text != null){
                    if (text.equals(START)) {
                        tgUser.setLanguage(DEF_LANG_EN); /// default language 'english'
                        ResourceMessageManager.setLocale(new Locale(DEF_LANG_EN));

                        if (tgUser.getMessageId() != null) {
                            telegramBot.execute(new DeleteMessage(
                                    tgUser.getChatId(),
                                    tgUser.getMessageId())
                            );
                        }
                        command = new CabinetCmd(tgUser);
                    } else {
                        int newMessageId = telegramBot.execute(
                                new SendMessage(chatId, getString(WARNING_MSG))
                        ).message().messageId();
                        tgUser.setMessageId(newMessageId);

                        command = new CabinetCmd(tgUser);
                    }
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

                } else if (data.startsWith(BACK)) {
                    command = new BackButtonCmd(tgUser, update, data);

                } else if (data.equals(ADMIN)) {
                    command = new AdminCmd(tgUser);

                } else if (data.startsWith(ADMIN_CALLBACK)) {
                    command = new AdminPanelCmd(tgUser, update, data);
                }
                requireNonNull(command).process();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}