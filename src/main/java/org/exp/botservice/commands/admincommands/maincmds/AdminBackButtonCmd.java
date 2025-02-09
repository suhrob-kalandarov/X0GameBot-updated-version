package org.exp.botservice.commands.admincommands.maincmds;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.adminentities.Admin;
import org.exp.entity.adminentities.AdminState;
import org.exp.entity.tguserentities.State;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.Main.telegramBot;
import static org.exp.botservice.servicemessages.Constant.CHOOSE_AN_OPTION;
import static org.exp.botservice.servicemessages.Constant.START_MSG;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class AdminBackButtonCmd implements BotCommand {
    private final TgUser tgUser;
    private final Admin admin;
    private final String data;

    @Override
    public void process() {
        switch (data) {
            case "admin_want_to_back_to_game_cabinet" -> {
                EditMessageText editMessageText = new EditMessageText(
                        admin.getChatId(),
                        admin.getMessageId(),
                        getString(START_MSG) + "\n\n" + getString(CHOOSE_AN_OPTION)
                );
                editMessageText.replyMarkup(BotButtonService.genCabinetButtons());

                SendResponse response = (SendResponse) telegramBot.execute(editMessageText);
                admin.setMessageId(response.message().messageId());

                tgUser.setMessageId(response.message().messageId());
                tgUser.setState(State.CABINET);

            }
            case "admin_back_to_cabinet" -> {
                EditMessageText editMessageText = new EditMessageText(
                        admin.getChatId(), admin.getMessageId(),
                        "Choose an option:"
                );
                editMessageText.replyMarkup(BotButtonService.genAdminBtns());
                SendResponse response = (SendResponse) telegramBot.execute(editMessageText);
                admin.setMessageId(response.message().messageId());
                admin.setAdminState(AdminState.ADMIN_PANEL);
            }
            case "admin_want_to_back_to_msg_main_panel" -> {
                EditMessageText editMessageText = new EditMessageText(
                        admin.getChatId(), admin.getMessageId(),
                        "Choose an option:"
                );
                SendResponse response = (SendResponse) telegramBot.execute(editMessageText);
                admin.setMessageId(response.message().messageId());
                admin.setAdminState(AdminState.MSG_MAIN_PANEL);
            }
        }
    }
}