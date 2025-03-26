package org.botcontrol.commands.playercmds;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.pengrad.telegrambot.request.EditMessageText;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.botcontrol.entities.User;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.botservice.btnservice.BotButtonService;

import static org.botcontrol.Main.telegramBot;
import static org.botcontrol.botservice.dbservice.DB.*;
import static org.botcontrol.botservice.msgservice.Constant.*;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class PlayGameCmd implements BotCommand {
    private final User user;
    private final String data;

    private static final Logger logger = LogManager.getLogger(PlayGameCmd.class);

    @Override
    public void process() {
        getAndSetChosenSymbol();
        user.initializeBoard();

        if (user.getPlayerSymbol() != null && user.getPlayerSymbol().equals("⭕")) {
            int[] botMove = new GameCmd(user).findBestMove(user.getGameBoard());
            if (botMove != null) {
                user.getGameBoard()[botMove[0]][botMove[1]] = 2;
                DB.updateGameBoard(user.getUserId(), user.getGameBoard());
            }
        }

        SendResponse response = (SendResponse) telegramBot.execute(
                new EditMessageText(
                        user.getUserId(), user.getMessageId(),
                        formatGameStartMessage()

                ).replyMarkup(BotButtonService.genGameBoard(
                        user.getGameBoard(), user.getPlayerSymbol())
                )
        );
        user.setMessageId(response.message().messageId());
        DB.updateMessageId(user.getUserId(), user.getMessageId());
    }

    private String formatGameStartMessage() {
        return getString(GAME_MENU_MSG).formatted(
                DB.getUserSign(user.getUserId()),
                DB.getBotSign(user.getUserId())
        );
    }

    private void getAndSetChosenSymbol() {
        if (data.equals("CHOOSE_X")) {
            user.setPlayerSymbol(X_SIGN);
            user.setBotSymbol(O_SIGN);

            updateUserSign(user.getUserId(), user.getPlayerSymbol());
            updateBotSign(user.getUserId(), user.getBotSymbol());

        } else if (data.equals("CHOOSE_O")) {
            user.setPlayerSymbol(O_SIGN);
            user.setBotSymbol(X_SIGN);

            updateUserSign(user.getUserId(), user.getPlayerSymbol());
            updateBotSign(user.getUserId(), user.getBotSymbol());

        } else {
            user.setMessageId(
                    telegramBot.execute(
                            new SendMessage(
                                    user.getUserId(), "ERROR: Try with /start!"

                            ).replyMarkup(BotButtonService.genCabinetButtons())

                    ).message().messageId()
            );
        }
    }
}
