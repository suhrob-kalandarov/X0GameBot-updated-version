package org.exp.botservice.commands.admincommands;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.TgUser;

@RequiredArgsConstructor
public class AdminCmd implements BotCommand {
    private final TgUser tgUser;

    @Override
    public void process() {
        SendMessage message = new SendMessage(
                tgUser.getChatId(),
                "ADMIN PANEL\n#ChatId " + tgUser.getChatId()
                        + "\nusername: @" + tgUser.getUsername()
        );
        message.replyMarkup(BotButtonService.genAdminBtns());
        SendResponse response = Main.telegramBot.execute(message);
        tgUser.setMessageId(response.message().messageId());
    }
}