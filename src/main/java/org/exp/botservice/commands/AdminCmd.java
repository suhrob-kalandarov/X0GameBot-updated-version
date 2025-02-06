package org.exp.botservice.commands;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.entity.State;
import org.exp.entity.TgUser;

import java.util.Objects;

@RequiredArgsConstructor
public class AdminCmd implements BotCommand {
    private final TgUser tgUser;
    private final Update update;

    @Override
    public void process() {
        if (Objects.equals(tgUser.getChatId(), "6513286717")){
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "You admin!");
            tgUser.setState(State.ADMIN_CABINET);

            Main.telegramBot.execute(sendMessage);
        }
    }
}