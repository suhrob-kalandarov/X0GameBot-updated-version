package org.exp.botservice.commands;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.botservice.database.DB;
import org.exp.entity.State;
import org.exp.entity.TgUser;

@RequiredArgsConstructor
public class AdminCmd implements BotCommand {
    private final TgUser tgUser;
    private final Update update;

    @Override
    public void process() {
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                """
                ADMIN PANEL
                
                """ + DB.TG_USERS_LIST.toString()
        );
        tgUser.setState(State.ADMIN_CABINET);
        SendResponse response = Main.telegramBot.execute(sendMessage);
        tgUser.setMessageId(response.message().messageId());
    }
}