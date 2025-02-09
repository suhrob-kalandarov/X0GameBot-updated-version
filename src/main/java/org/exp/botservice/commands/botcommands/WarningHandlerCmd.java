package org.exp.botservice.commands.botcommands;

import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.exp.botservice.commands.BotCommand;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.Main.telegramBot;
import static org.exp.botservice.servicemessages.Constant.WARNING_MSG;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class WarningHandlerCmd implements BotCommand {
    private final TgUser tgUser;

    @Override
    public void process() {
        telegramBot.execute(new DeleteMessage(
                tgUser.getChatId(),
                tgUser.getMessageId())
        );

        tgUser.setMessageId(telegramBot.execute(
                new SendMessage(
                        tgUser.getChatId(),
                        getString(WARNING_MSG)
                )
        ).message().messageId());
    }
}
