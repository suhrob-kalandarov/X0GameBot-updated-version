package org.exp.botservice.commands.admincommands.msgcmds;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.entity.TgUser;

@RequiredArgsConstructor
public class SendingMsgToOneUser implements BotCommand {
    private final TgUser tgUser;

    @Override
    public void process() {
        SendMessage message = new SendMessage(tgUser.getChatId(),
                "null"
        );
        Main.telegramBot.execute(message);
    }
}
