package org.exp.botservice.commands.admincommands.msgcmds;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.adminentities.Admin;

@RequiredArgsConstructor
public class MsgFormatsCmd implements BotCommand {
    //private final String text;
    private final Admin admin;

    @Override
    public void process() {
        SendResponse response = (SendResponse) Main.telegramBot.execute(
                new EditMessageText(
                        admin.getChatId(), admin.getMessageId(),
                        "Choose a message format:"
                ).replyMarkup(BotButtonService.genMessageStylesBtns())
        );
        admin.setMessageId(response.message().messageId());
    }
}
