package org.botcontrol.commands.updatecmds;

import com.pengrad.telegrambot.model.*;
import lombok.RequiredArgsConstructor;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.entities.User;

import java.util.Objects;


@RequiredArgsConstructor
public class UpdateCmd implements UpdateCommand {
    private final Update update;

    @Override
    public void handle() {
        try {
            UpdateCommand command = null;
            Message message = update.message();
            CallbackQuery callback = update.callbackQuery();
            InlineQuery inlineQuery = update.inlineQuery();
            ChosenInlineResult chosenInlineResult = update.chosenInlineResult();

            User user = DB.getOrCreateUser(update);

            if (message != null) {

                command = new MessageCmd(update, user);

            } else if (callback != null) {

                command = new CallbackCmd(update, user);

            } else if (inlineQuery != null) {

                command = new InlineQueryCmd(inlineQuery);

            } else if (chosenInlineResult != null) {

                command = new InlineResultCmd(update, user);

            } else {
                System.out.println("Unknown update: " + update);
            }
            Objects.requireNonNull(command).handle();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
