package org.exp.botservice.commands;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.botservice.service.BotButtonService;
import org.exp.botservice.servicemessages.Constant;
import org.exp.botservice.servicemessages.ResourceMessageManager;
import org.exp.entity.State;
import org.exp.entity.TgUser;

import static org.exp.Main.telegramBot;
import static org.exp.botservice.database.DB.isAdmin;

@RequiredArgsConstructor
public class CabinetCmd implements BotCommand{
    private final TgUser tgUser;

    @Override
    public void process() {
        if (isAdmin(tgUser)) {
            SendMessage message = new SendMessage(
                    tgUser.getChatId(),
                    "You're admin! I added admin button!"
            );
            message.replyMarkup(BotButtonService.genAdminBtn());
            SendResponse response = telegramBot.execute(message);
            tgUser.setMessageId(response.message().messageId());
        }

        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                ResourceMessageManager.getString(Constant.START_MSG)
        );
        sendMessage.replyMarkup(BotButtonService.genCabinetButtons(tgUser));
        SendResponse response = telegramBot.execute(sendMessage);
        tgUser.setMessageId(response.message().messageId());
        tgUser.setState(State.CABINET);
    }
}
