package org.exp.botservice.commands;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.botservice.commands.admincommands.AdminCmd;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.State;
import org.exp.entity.TgUser;

import static org.exp.Main.telegramBot;
import static org.exp.botservice.servicemessages.Constant.*;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class BackButtonCmd implements BotCommand{
    private final TgUser tgUser;
    private final Update update;
    private final String data;

    @Override
    public void process() {
        if (data.equals("back_to_cabinet") || data.equals("admin_back_to_game_cabinet")) {
            EditMessageText editMessageText = new EditMessageText(
                    tgUser.getChatId(),
                    tgUser.getMessageId(),
                    getString(START_MSG) + "\n\n" + getString(CHOOSE_AN_OPTION)
            );
            editMessageText.replyMarkup(BotButtonService.genCabinetButtons(tgUser));

            tgUser.setState(State.CABINET);
            SendResponse response = (SendResponse) telegramBot.execute(editMessageText);
            tgUser.setMessageId(response.message().messageId());

        } else if (data.equals("admin_back_to_cabinet")) {

            EditMessageText editMessageText = new EditMessageText(
                    tgUser.getChatId(), tgUser.getMessageId(),
                    "Choose an option:"
            );
            editMessageText.replyMarkup(BotButtonService.genAdminBtns());
            SendResponse response = (SendResponse) telegramBot.execute(editMessageText);
            tgUser.setMessageId(response.message().messageId());

        }
    }
}
