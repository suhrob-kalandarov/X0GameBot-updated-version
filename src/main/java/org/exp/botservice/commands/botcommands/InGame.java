package org.exp.botservice.commands.botcommands;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.exp.botservice.commands.BotCommand;
import org.exp.entity.tguserentities.TgUser;

@RequiredArgsConstructor
public class InGame implements BotCommand {
    private final TgUser tgUser;
    private final Update update;
    private final String data;

    @Override
    public void process() {
        int row = Integer.parseInt(data.split("_")[1]);
        int col = Integer.parseInt(data.split("_")[2]);
        new PlayGameCmd(tgUser, update, data).handleMove(row, col);
    }
}
