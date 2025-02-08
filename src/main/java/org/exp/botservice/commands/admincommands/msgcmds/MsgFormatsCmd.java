package org.exp.botservice.commands.admincommands.msgcmds;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.TgUser;

@RequiredArgsConstructor
public class MsgFormatsCmd implements BotCommand {
    //private final String text;
    private final TgUser tgUser;

    @Override
    public void process() {
        EditMessageText editMessageText = new EditMessageText(
                tgUser.getChatId(), tgUser.getMessageId(),
                "Choose a message style:"
        );
        editMessageText.replyMarkup(BotButtonService.genMessageStylesBtns());
        SendResponse response = (SendResponse) Main.telegramBot.execute(editMessageText);
        tgUser.setMessageId(response.message().messageId());
    }
}
