package org.botcontrol.commands.updatecmds;

import com.pengrad.telegrambot.model.ChosenInlineResult;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.botcontrol.entities.User;

@RequiredArgsConstructor
public class InlineResultCmd implements UpdateCommand {
    private final Update update;
    private final User user;

    private final ChosenInlineResult chosenInlineResult = update.chosenInlineResult();

    @Override
    public void handle() {
        System.out.println("ChosenInlineResult: {" + chosenInlineResult + "}");


    }
}
