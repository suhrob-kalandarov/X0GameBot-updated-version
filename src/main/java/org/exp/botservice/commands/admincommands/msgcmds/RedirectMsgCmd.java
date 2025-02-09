package org.exp.botservice.commands.admincommands.msgcmds;

import lombok.RequiredArgsConstructor;

import org.exp.botservice.commands.BotCommand;
import org.exp.entity.adminentities.Admin;

import java.util.Objects;

@RequiredArgsConstructor
public class RedirectMsgCmd implements BotCommand {
   private final Admin admin;
   private final String data;

    @Override
    public void process() {
        /*BotCommand command = switch (data) {
            case "admin_send_message_to_users" -> new MsgFormatsCmd(admin);
            case "admin_send_message_to_user" -> new SendingMsgToOneUser(admin);

            //default -> new AdminBackButtonCmd(tgUser, admin, data);
            default -> throw new IllegalStateException("Unexpected value: " + data);
        };
        Objects.requireNonNull(command).process();*/
    }
}
