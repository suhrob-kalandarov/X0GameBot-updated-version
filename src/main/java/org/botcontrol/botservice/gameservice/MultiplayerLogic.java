package org.botcontrol.botservice.gameservice;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.botcontrol.botservice.btnservice.BotButtonService;
import org.botcontrol.commands.updatecmds.MessageCmd;
import org.botcontrol.entities.MultiGame;
import org.botcontrol.entities.MultiplayerUser;

import java.util.Arrays;


import static org.botcontrol.Main.telegramBot;
import static org.botcontrol.botservice.btnservice.BotButtonService.formatBoard;
import static org.botcontrol.botservice.btnservice.BotButtonService.sendGameBoard;
import static org.botcontrol.botservice.dbservice.DB.games;
import static org.botcontrol.botservice.dbservice.DB.log;

public class MultiplayerLogic {

   static final Logger log = LogManager.getLogger(MultiplayerLogic.class);

    // Yangi o'yin yaratish: yaratuvchi (creator) birinchi bo'lib qo'shiladi va navbat shu bo'ladi.
    public static int createNewGame(Long creatorId) {
        int gameId = games.size() + 1;
        MultiGame game = MultiGame.builder()
                .gameId(gameId)
                .creatorId(creatorId)
                .turn(creatorId) // Boshlang'ich navbat yaratuvchida
                .build();
        game.initializeBoard();
        games.put(gameId, game);
        log("Yangi o'yin yaratildi: Game ID = " + gameId + ", Creator ID = " + creatorId);
        return gameId;
    }

    public static void joinGame(MultiplayerUser user, int gameId, String symbol, String inlineMessageId, CallbackQuery callbackQuery) {
        MultiGame game = games.get(gameId);

        if (game == null) {
            log("O'yin topilmadi: Game ID = " + gameId);
            return;
        }

        if (symbol.equalsIgnoreCase("X")) {
            if (game.getPlayerX() == null) {
                game.setPlayerX(user);
            } else {
                log("❌ o'yinchisi allaqachon bor: Game ID = " + gameId);
                return;
            }

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                    new InlineKeyboardButton("⭕Waiting oppenent...")
                            .callbackData("SELECT_O_" + gameId)
            );
            telegramBot.execute(new EditMessageReplyMarkup(inlineMessageId).replyMarkup(markup));
            log("❌ o'yinchisi qo'shildi: Game ID = " + gameId + ", User ID = " + user.getUserId());

        } else if (symbol.equalsIgnoreCase("O")) {
            if (game.getPlayerO() == null) {
                // Faqat `playerX` bilan bir xil emasligini tekshiramiz
                if (!game.getPlayerX().getUserId().equals(user.getUserId())) {
                    game.setPlayerO(user);
                } else {
                    if (callbackQuery != null) {
                        AnswerCallbackQuery alert = new AnswerCallbackQuery(callbackQuery.id())
                                .text("⚠ It's not for you!")
                                .showAlert(true); // Modal ko‘rinishda chiqarish

                        telegramBot.execute(alert);
                    }
                    log("O'yinga qo'shilib bo'lmaydi: User ID = " + user.getUserId() + " ❌ sifatida o'ynayapti!");
                    return;
                }
            } else {
                log("⭕ o'yinchisi allaqachon bor: Game ID = " + gameId);
                return;
            }
            log("⭕ o'yinchisi qo'shildi: Game ID = " + gameId + ", User ID = " + user.getUserId());
        } else {
            log("Noto'g'ri belgi: " + symbol);
            return;
        }

        game.setInlineMessageId(inlineMessageId);
        checkGameReady(game);
    }

    static void checkGameReady(MultiGame game) {
        if (game.getPlayerX() != null && game.getPlayerO() != null) {
            log("O'yin tayyor: Game ID = " + game.getGameId());
            sendGameBoard(game, game.getInlineMessageId());
        } else {
            log("O'yin tayyor emas: Game ID = " + game.getGameId());
        }
    }

    public static void handleMove(int gameId, int row, int col, Long userId, String inlineMessageId, CallbackQuery callbackQuery) {
        MultiGame game = games.get(gameId);
        if (game == null) {
            log("O'yin topilmadi: Game ID = " + gameId);
            return;
        }

        log("O'yin doskasi holati: " + Arrays.deepToString(game.getGameBoard()));
        log("Navbatdagi o'yinchi: " + game.getTurn());

        if (game.getTurn() == null) {
            log("Navbat belgilanmagan: Game ID = " + gameId);
            return;
        }

        if (!game.getTurn().equals(userId)) {
            log("Sizning navbatingiz emas: Game ID = " + gameId + ", User ID = " + userId);

            if (callbackQuery != null) {
                AnswerCallbackQuery alert = new AnswerCallbackQuery(callbackQuery.id())
                        .text("⚠ It's not your move!")
                        .showAlert(true); // Modal ko‘rinishda chiqarish

                telegramBot.execute(alert);
            }
            return;
        }

        int[][] board = game.getGameBoard();
        if (board[row][col] != 0) {
            log("Ushbu katak allaqachon to'ldirilgan: Game ID = " + gameId + ", Row = " + row + ", Col = " + col);
            if (callbackQuery != null) {
                AnswerCallbackQuery alert = new AnswerCallbackQuery(callbackQuery.id())
                        .text("⚠ This field is already filled in!")
                        .showAlert(true); // Modal ko‘rinishda chiqarish

                telegramBot.execute(alert);
            }
            return;
        }

        int playerMark = (userId.equals(game.getPlayerX().getUserId())) ? 1 : 2; // 1 = ❌, 2 = ⭕
        board[row][col] = playerMark;

        if (checkWin(board, playerMark)) {
            String resultMessage = (playerMark == 1)
                    ? "Winner: ❌" + game.getPlayerX().getFullName() + " !"
                    : "Winner: ⭕" + game.getPlayerO().getFullName() + " !";
            sendResult(game, resultMessage, inlineMessageId);
            return;
        } else if (checkDraw(board)) {
            sendResult(game,"Draw: " + game.getPlayerX().getFullName() + "🤝" + game.getPlayerO().getFullName(),
                    inlineMessageId
            );
            return;
        }

        // Navbatni almashtiramiz
        game.setTurn(userId.equals(game.getPlayerX().getUserId()) ? game.getPlayerO().getUserId() : game.getPlayerX().getUserId());
        sendGameBoard(game, game.getInlineMessageId());
    }


    private static boolean checkWin(int[][] board, int player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player)
                return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player)
                return true;
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player)
            return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player)
            return true;
        return false;
    }

    private static boolean checkDraw(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0)
                    return false;
            }
        }
        return true;
    }

    private static void sendResult(MultiGame game, String resultMessage, String inlineMessageId) {
        String message = "Game over!\n" + resultMessage + "\n\n" + formatBoard(game.getGameBoard()); //"Game over!\n" +
        EditMessageText editMessage = new EditMessageText(
                inlineMessageId,
                message
        ).replyMarkup(BotButtonService.endGameBtns())
                .parseMode(ParseMode.Markdown);

        telegramBot.execute(editMessage);

        games.remove(game.getGameId());
        log("O'yin tugatildi: Game ID = " + game.getGameId() + ", Natija = " + resultMessage);
    }

}
