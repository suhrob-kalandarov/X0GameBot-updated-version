package org.botcontrol.commands.updatecmds;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.ChosenInlineResult;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.entities.User;


@RequiredArgsConstructor
public class UpdateCmd implements UpdateCommand {
    private final Update update;

    @Override
    public void handle() {
        Message message = update.message();
        CallbackQuery callback = update.callbackQuery();
        ChosenInlineResult chosenInlineResult = update.chosenInlineResult();

        User user = DB.getOrCreateUser(update);

        if (message != null) {

            new MessageCmd(update, user).handle();

        } else if (callback != null) {

            new CallbackCmd(update, user).handle();

        } else if (chosenInlineResult != null) {

            new InlineResultCmd(update, user).handle();

        } else {
            System.out.println("Unknown update: " + update);
        }
    }
}
