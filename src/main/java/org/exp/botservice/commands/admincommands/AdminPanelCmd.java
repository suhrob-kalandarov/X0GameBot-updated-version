package org.exp.botservice.commands.admincommands;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;

import org.exp.botservice.commands.BackButtonCmd;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.commands.admincommands.msgcmds.MsgFormatsCmd;
import org.exp.botservice.commands.admincommands.msgcmds.SendingMsgToOneUser;
import org.exp.entity.TgUser;

import java.util.Objects;

@RequiredArgsConstructor
public class AdminPanelCmd implements BotCommand {
    private final TgUser tgUser;
    private final Update update;
    private final String data;

    @Override
    public void process() {
        BotCommand command = switch (data) {
            case "admin_send_message_to_users" -> new MsgFormatsCmd(tgUser);
            case "admin_send_message_to_user" -> new SendingMsgToOneUser(tgUser);
            case "admin_get_users_list" -> new DataStorageCmd(tgUser);
            case "admin_view_photo_files" -> new ViewPhotoFilesCmd(tgUser, update);
            //case "admin_back_to_game_cabinet" -> new BackButtonCmd(tgUser, update, data);
            default -> new BackButtonCmd(tgUser, update, data);
        };
        Objects.requireNonNull(command).process();
    }
}
