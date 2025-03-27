package org.botcontrol.commands.playercmds;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.entities.MultiplayerUser;

import static org.botcontrol.botservice.dbservice.DB.users;
import static org.botcontrol.botservice.gameservice.MultiplayerLogic.handleMove;
import static org.botcontrol.botservice.gameservice.MultiplayerLogic.joinGame;
import static org.botcontrol.botservice.msgservice.Constant.*;

@RequiredArgsConstructor
public class InviteFriendCmd implements BotCommand {
    private final Update update;
    //private final User user;

    @Override
    public void process() {
        try {
            CallbackQuery callback = update.callbackQuery();
            Long userId = callback.from().id();
            String firstName = callback.from().firstName();
            String lastName = callback.from().lastName();

            MultiplayerUser user = MultiplayerUser.builder()
                    .userId(userId)
                    .fullName(DB.setFullName(firstName, lastName))
                    .build();

            users.add(user);

            String data = callback.data();

            if (data.startsWith(SELECT_X)) {
                int gameId = Integer.parseInt(data.split("_")[2]);
                joinGame(user, gameId, "X", callback.inlineMessageId(), callback);

            } else if (data.startsWith(SELECT_O)) {
                int gameId = Integer.parseInt(data.split("_")[2]);
                joinGame(user, gameId, "O", callback.inlineMessageId(), callback);

            } else if (data.startsWith(MOVE)) {

                String[] parts = data.split("_");

                int gameId = Integer.parseInt(parts[1]);
                int row = Integer.parseInt(parts[2]);
                int col = Integer.parseInt(parts[3]);

                handleMove(gameId, row, col, userId, callback.inlineMessageId(), callback);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
