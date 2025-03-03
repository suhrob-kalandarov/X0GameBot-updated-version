package org.botcontrol.botcommands.multiplayer;

import com.pengrad.telegrambot.request.EditMessageText;
import lombok.RequiredArgsConstructor;

import org.botcontrol.botcommands.BotCommand;
import org.botcontrol.botcommands.botplayer.StatisticsCmd;
import org.botcontrol.entities.User;

import static org.botcontrol.Main.telegramBot;

@RequiredArgsConstructor
public class InviteFriendCmd implements BotCommand {
    private final User user;

    @Override
    public void process() {
        telegramBot.execute(new EditMessageText(
                user.getUserId(), user.getMessageId(),
                "↩SOON")
        );
        new StatisticsCmd(user).process();
    }
}
