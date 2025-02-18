package org.exp.botservice.commands.admincommands.msgcmds;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.adminentities.Admin;
import org.exp.entity.adminentities.AdminState;

@RequiredArgsConstructor
public class MsgSenderMainPanel implements BotCommand {
    private final Admin admin;
    private final String data;

    @Override
    public void process() {
        if (data.equals("msg_with_pic")){
            SendResponse response = Main.telegramBot.execute(new SendMessage(
                    admin.getChatId(),
                    "Send me photo:")
                    .replyMarkup(BotButtonService.genBackButton("admin_back_to_msg_main_panel"))
            );
            admin.setMessageId(response.message().messageId());
            admin.setAdminState(AdminState.PHOTO_REQUIRED);

        } else if (data.equals("msg_only_text")) {
            SendResponse response = Main.telegramBot.execute(new SendMessage(
                    admin.getChatId(),
                    "Send me message:")
                    .replyMarkup(BotButtonService.genBackButton("admin_back_to_msg_main_panel"))
            );
            admin.setMessageId(response.message().messageId());
            admin.setAdminState(AdminState.TEXT_REQUIRED);
        }
    }
}
