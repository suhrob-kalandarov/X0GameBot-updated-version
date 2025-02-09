package org.exp.botservice.commands.admincommands.maincmds;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;

import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.commands.admincommands.msgcmds.MsgFormatsCmd;
import org.exp.botservice.commands.admincommands.msgcmds.SendingMsgToOneUser;
import org.exp.entity.adminentities.Admin;
import org.exp.entity.tguserentities.TgUser;

import java.util.Objects;

@RequiredArgsConstructor
public class AdminPanelCmd implements BotCommand {
    private final TgUser tgUser;
    private final Admin admin;
    private final Update update;
    private final String data;

    @Override
    public void process() {
        BotCommand command = switch (data) {

            case "admin_want_to_send_message" -> new MsgMainPanel(admin, tgUser, data);

            case "admin_send_message_to_users" -> new MsgFormatsCmd(admin);

            case "admin_send_message_to_an_user" -> new SendingMsgToOneUser(admin);

            case "admin_want_to_get_users_list" -> new DataStorageCmd(admin, "admin_back_to_cabinet");

            case "admin_want_to_show_user_list" -> new DataStorageCmd(admin, "admin_send_message_to_an_user");

            case "admin_want_to_view_photo_files" -> new ViewPhotoFilesCmd(admin);

            default -> new AdminBackButtonCmd(tgUser, admin, data);
        };
        Objects.requireNonNull(command).process();
    }
}