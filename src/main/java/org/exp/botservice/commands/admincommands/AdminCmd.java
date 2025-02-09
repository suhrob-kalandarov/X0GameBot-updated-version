package org.exp.botservice.commands.admincommands;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.adminentities.Admin;

@RequiredArgsConstructor
public class AdminCmd implements BotCommand {
    private final Admin admin;

    @Override
    public void process() {
        SendMessage message = new SendMessage(
                admin.getChatId(),
                "ADMIN PANEL\n#ChatId " + admin.getChatId()
                        + "\nusername: @" + admin.getUsername()
        );
        message.replyMarkup(BotButtonService.genAdminBtns());
        SendResponse response = Main.telegramBot.execute(message);
        admin.setMessageId(response.message().messageId());
    }
}
