package org.exp.botservice.commands.admincommands.msgcmds;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.entity.TgUser;

@RequiredArgsConstructor
public class MsgSenderMainPanel implements BotCommand {
    private final TgUser tgUser;
    private final String data;

    @Override
    public void process() {
        if (data.equals("msg_with_pic")){
            Main.telegramBot.execute(new SendMessage(tgUser.getChatId(), "null"));

        } else if (data.equals("msg_with_text")) {
            Main.telegramBot.execute(new SendMessage(tgUser.getChatId(), "null"));
            /* for (TgUser tgUser : DB.TG_USERS_LIST) {
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), text);
            Main.telegramBot.execute(sendMessage);
        }*/
        }
    }
}
