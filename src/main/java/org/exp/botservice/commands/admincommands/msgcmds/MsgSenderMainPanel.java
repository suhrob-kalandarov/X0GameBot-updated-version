package org.exp.botservice.commands.admincommands.msgcmds;

import com.pengrad.telegrambot.request.EditMessageText;
import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.entity.TgUser;

@RequiredArgsConstructor
public class MsgSenderMainPanel implements BotCommand {
    private final TgUser tgUser;

    @Override
    public void process() {

    }
}
