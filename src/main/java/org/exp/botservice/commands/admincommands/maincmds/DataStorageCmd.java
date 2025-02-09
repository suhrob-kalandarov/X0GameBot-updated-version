package org.exp.botservice.commands.admincommands.maincmds;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.adminentities.Admin;
import org.exp.entity.tguserentities.TgUser;

import java.util.StringJoiner;

import static org.exp.database.DB.ADMIN_LIST;
import static org.exp.database.DB.TG_USERS_LIST;

@RequiredArgsConstructor
public class DataStorageCmd implements BotCommand {
    private final Admin admin;
    private final String backCallBackData;

    @Override
    public void process() {
        EditMessageText editMessageText = new EditMessageText(
                admin.getChatId(), admin.getMessageId(),
                "Existing data: \n\n" + getAllUsers()
        );
        editMessageText.replyMarkup(BotButtonService.genBackButton(backCallBackData));

        SendResponse response = (SendResponse) Main.telegramBot.execute(editMessageText);

        // ✅ Null tekshiruv qo‘shildi
        if (response != null && response.message() != null) {
            admin.setMessageId(response.message().messageId());
        } else {
            System.err.println("⚠️ SendResponse yoki response.message() null! Xabar jo‘natilmadi.");
        }
    }


    private String getAllUsers() {
        StringJoiner users = new StringJoiner("\n");
        users.add("USERS:\n");
        StringJoiner admins = new StringJoiner("\n");
        admins.add("\n\nADMINS:\n");
        for (TgUser user : TG_USERS_LIST) {
            for (Admin admin : ADMIN_LIST) {
                if (user.getChatId().equals(admin.getChatId())) {
                    admins.add("🆔 " + user.getChatId()).add("👤 @" + user.getUsername());
                } else {
                    users.add("🆔 " + user.getChatId()).add("👤 @" + user.getUsername()); //.add("🌐 " + user.getLanguage())
                }
            }
        }
        return String.valueOf(users.merge(admins));
    }
}