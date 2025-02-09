package org.exp.botservice.commands.admincommands.msgcmds;

import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.database.DB;
import org.exp.entity.adminentities.Admin;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.Main.telegramBot;

@RequiredArgsConstructor
public class CheckTgUserAvailabilityAndRedirect implements BotCommand {
    private final Admin admin;
    private final String text;

    @Override
    public void process() {
        Long receivedChatId = Long.parseLong(text.substring(7));

        if (isExistTgUser(receivedChatId)){
            telegramBot.execute(new DeleteMessage(
                    admin.getChatId(),
                    admin.getMessageId())
            );
            new MsgFormatsCmd(admin).process();

        } else {
            /*telegramBot.execute(new DeleteMessage(
                    admin.getChatId(),
                    admin.getMessageId())
            );*/
            //Main.telegramBot.execute(new SendMessage(admin.getChatId(), "TgUser doesn't exist in bot!"));
            telegramBot.execute(
                    new SendMessage(
                            admin.getChatId(),
                            "TgUser doesn't exist in bot!"
                    )
            );
            new SendingMsgToOneUser(admin).process();
        }
    }

    static boolean isExistTgUser(Long receivedChatId) {
        for (TgUser tgUser : DB.TG_USERS_LIST) {
            if (tgUser.getChatId().equals(receivedChatId)) {
                return true;
            }
        }
        return false;
    }
}
