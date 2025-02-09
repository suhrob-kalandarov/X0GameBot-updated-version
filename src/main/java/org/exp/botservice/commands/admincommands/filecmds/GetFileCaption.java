package org.exp.botservice.commands.admincommands.filecmds;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.entity.adminentities.Admin;

@RequiredArgsConstructor
public class GetFileCaption implements BotCommand {
    private final Admin admin;
    private final Update update;

    @Override
    public void process() {
        admin.setPhoto(update.message().text().substring("photo_".length()));
        SendMessage message = new SendMessage(
                admin.getChatId(),
                "Send the image caption in the caption_ view."
        );
        Main.telegramBot.execute(message);
    }
}