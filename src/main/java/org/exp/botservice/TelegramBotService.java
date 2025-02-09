package org.exp.botservice;

import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;

import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.commands.admincommands.AdminCmd;
import org.exp.botservice.commands.admincommands.filecmds.DownloadPhotoCmd;
import org.exp.botservice.commands.admincommands.filecmds.GetFileCaption;
import org.exp.botservice.commands.admincommands.filecmds.PhotoSenderCmd;
import org.exp.botservice.commands.admincommands.maincmds.AdminPanelCmd;
import org.exp.botservice.commands.admincommands.msgcmds.*;

import org.exp.botservice.commands.botcommands.*;
import org.exp.entity.adminentities.Admin;
import org.exp.entity.adminentities.AdminState;
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
                Admin admin = getAdmin(6513286717L);
                admin.setUsername(update.message().chat().username());
                String text = update.message().text();
                PhotoSize[] photo = update.message().photo();
                BotCommand command = null;

                if (text != null){
                    if (text.equals(START)) {
                        if (tgUser.getMessageId() != null) {
                            telegramBot.execute(new DeleteMessage(
                                    tgUser.getChatId(),
                                    tgUser.getMessageId())
                            );
                        } else {
                            tgUser.setLanguage(DEF_LANG_EN);
                            setLocale(new Locale(DEF_LANG_EN));
                        }
                        command = new CabinetCmd(tgUser);

                    } else if (
                            text.equals(ADMIN)
                                    && isAdmin(tgUser)
                    ){
                        telegramBot.execute(new DeleteMessage(
                                tgUser.getChatId(),
                                tgUser.getMessageId())
                        );
                        command = new AdminCmd(admin);

                    } else if (
                            text.startsWith("photo_")
                                    && admin.getAdminState().equals(AdminState.GET_PHOTO_NAME)
                    ){
                        command = new GetFileCaption(admin, update);

                    } else if (
                            text.startsWith("caption_")
                                    && admin.getAdminState().equals(AdminState.GET_PHOTO_CAPTION)
                    ){
                        command = new PhotoSenderCmd(admin, update);

                    } else if (text.startsWith("chatId_")) {
                        command = new CheckTgUserAvailabilityAndRedirect(admin, text);

                    } else {
                        new WarningHandlerCmd(tgUser).process();
                        command = new CabinetCmd(tgUser);
                    }

                } else if (
                        photo != null && isAdmin(tgUser)
                                && admin.getAdminState().equals(AdminState.RECEIVING_PHOTO)
                ){
                    command = new DownloadPhotoCmd(tgUser, update);

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
                Admin admin = getAdmin(6513286717L);
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
                    command = new BackButtonCmd(tgUser, data);

                } else if (data.startsWith(ADMIN_CALLBACK)) {
                    command = new AdminPanelCmd(tgUser, admin, update, data);

                } else if (data.startsWith("msg_with_")) {
                    command = new MsgSenderMainPanel(admin, data);

                }
                requireNonNull(command).process();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}