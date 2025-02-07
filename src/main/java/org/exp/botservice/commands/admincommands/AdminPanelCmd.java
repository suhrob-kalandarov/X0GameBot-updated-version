package org.exp.botservice.commands.admincommands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.commands.admincommands.msgcmds.MsgFormatsCmd;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.TgUser;

import java.util.Objects;

import static org.exp.botservice.servicemessages.Constant.*;

@RequiredArgsConstructor
public class AdminPanelCmd implements BotCommand {
    private final TgUser tgUser;
    private final Update update;
    private final String data;

    @Override
    public void process() {
        BotCommand command = null;

        if (data.equals(SEND_MSG_TO_BOT_USERS_BTN)) {
            command = new MsgFormatsCmd(tgUser);

        } else if (data.equals(GET_USERS_LIST_BTN)) {

        } else if (data.equals(DATA_STORAGE_BTN)) {

        } else if (data.equals(UPDATE_USERS_DATA_BTN)) {

        } else {
            command = new AdminCmd(tgUser);
        }
        Objects.requireNonNull(command).process();
    }
}
