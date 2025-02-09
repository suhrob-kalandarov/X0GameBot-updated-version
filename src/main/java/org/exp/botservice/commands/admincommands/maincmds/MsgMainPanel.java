package org.exp.botservice.commands.admincommands.maincmds;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.adminentities.Admin;
import org.exp.entity.adminentities.AdminState;
import org.exp.entity.tguserentities.TgUser;


@RequiredArgsConstructor
public class MsgMainPanel implements BotCommand {
    private final Admin admin;
    private final TgUser tgUser;
    private final String data;

    @Override
    public void process() {
        SendResponse response = (SendResponse) Main.telegramBot.execute(
                new EditMessageText(
                        admin.getChatId(),
                        admin.getMessageId(),
                        "MsgMainPanel"
                ).replyMarkup(
                        BotButtonService.getMessageMainPanelOptions()
                )
        );
        admin.setMessageId(response.message().messageId());
        admin.setAdminState(AdminState.MSG_MAIN_PANEL);

        /*BotCommand command = switch (data) {
            case "admin_send_message_to_users" -> new MsgFormatsCmd(admin);
            case "admin_send_message_to_user" -> new SendingMsgToOneUser(admin);
            default -> new AdminBackButtonCmd(tgUser, admin, data);
        };
        Objects.requireNonNull(command).process();*/
    }
}
