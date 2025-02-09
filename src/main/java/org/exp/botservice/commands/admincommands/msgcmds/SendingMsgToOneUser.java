package org.exp.botservice.commands.admincommands.msgcmds;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.adminentities.Admin;
import org.exp.entity.adminentities.AdminState;

@RequiredArgsConstructor
public class SendingMsgToOneUser implements BotCommand {
    private final Admin admin;

    @Override
    public void process() {
        Main.telegramBot.execute(new SendMessage(admin.getChatId(), "Send me the user chatId in the form chatId_1234567890"));
        SendResponse response = (SendResponse) Main.telegramBot.execute(
                new EditMessageText(
                        admin.getChatId(),
                        admin.getMessageId(),
                        "Send chatId or Choose an option")
                        .replyMarkup(
                                BotButtonService.SendingMsgToOneUserMainOptionBtns()
                        )
        );
        admin.setMessageId(response.message().messageId());
        admin.setAdminState(AdminState.USER_CHAT_ID_REQUIRED);
    }
}