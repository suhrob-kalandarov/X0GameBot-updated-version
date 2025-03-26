package org.botcontrol.commands.playercmds;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;

import org.botcontrol.Main;

import org.botcontrol.entities.User;
import org.botcontrol.entities.UserState;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.entities.DifficultyLevel;
import org.botcontrol.botservice.gameservice.GameLogic;
import org.botcontrol.botservice.btnservice.BotButtonService;

import static org.botcontrol.Main.telegramBot;
import static org.botcontrol.botservice.btnservice.BotButtonService.genGameBoard;
import static org.botcontrol.botservice.dbservice.DB.*;
import static org.botcontrol.botservice.msgservice.Constant.*;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class GameCmd implements BotCommand {
    private final User user;

    @Override
    public void process() {

    }

    public void handleMove(int row, int col) {
        try {
            // Restart the board if it's full
            if (isBoardFull(user.getGameBoard())) {
                user.initializeBoard();
                DB.updateGameBoard(user.getUserId(), user.getGameBoard()); // ✅ Game board reset
            }

            // Return if the selected slot is busy
            if (user.getGameBoard()[row][col] != 0) {
                return;
            }

            // User walkthrough
            user.getGameBoard()[row][col] = 1;
            DB.updateGameBoard(user.getUserId(), user.getGameBoard()); // ✅ User walk update

            if (checkWin(user.getGameBoard(), 1)) {
                updateGameScore(user.getUserId(), user.getDifficultyLevel(), "win_count",
                        getUserGameStat(
                                user.getUserId(),
                                user.getDifficultyLevel(),
                                "win_count"
                        ) + 1
                );
                sendResult(YOU_WON_MSG);
                return;
            }

            if (isBoardFull(user.getGameBoard())) {
                updateGameScore(user.getUserId(), user.getDifficultyLevel(), "draw_count",
                        getUserGameStat(
                                user.getUserId(),
                                user.getDifficultyLevel(),
                                "draw_count"
                        ) + 1
                );
                sendResult(DRAW_MSG);
                return;
            }

            // Bot walk
            int[] botMove = new GameLogic().findBestMove(user.getGameBoard(), user.getDifficultyLevel());
            user.getGameBoard()[botMove[0]][botMove[1]] = 2;
            DB.updateGameBoard(user.getUserId(), user.getGameBoard()); // ✅ Bot walk update

            if (checkWin(user.getGameBoard(), 2)) {
                updateGameScore(user.getUserId(), user.getDifficultyLevel(), "lose_count",
                        getUserGameStat(
                                user.getUserId(),
                                user.getDifficultyLevel(),
                                "lose_count"
                        ) + 1
                );
                sendResult(YOU_LOST_MSG);
                return;
            }

            if (isBoardFull(user.getGameBoard())) {
                updateGameScore(user.getUserId(), user.getDifficultyLevel(), "draw_count",
                        getUserGameStat(
                                user.getUserId(),
                                user.getDifficultyLevel(),
                                "draw_count"
                        ) + 1
                );
                sendResult(DRAW_MSG);
                return;
            }

            updateGameBoard(); // Refresh the UI
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Update the board
    private void updateGameBoard() {
        EditMessageText editMessage = new EditMessageText(
                user.getUserId(), user.getMessageId(), formatGameStartMessage()
        );
        editMessage.replyMarkup(genGameBoard(getGameBoard(user.getUserId()), getUserSign(user.getUserId())));
        Main.telegramBot.execute(editMessage);
    }

    private String formatGameStartMessage() {
        return getString(GAME_MENU_MSG).formatted(
                DB.getUserSign(user.getUserId()),
                DB.getBotSign(user.getUserId())
        );
    }

    // Find the best walk for the bot
    public int[] findBestMove(int[][] board) {
        return new GameLogic().findBestMove(board, user.getDifficultyLevel());
    }

    // Check for ingestion
    private boolean checkWin(int[][] board, int player) {
        int size = board.length;

        // Horizontal and vertical
        for (int i = 0; i < size; i++) {
            boolean rowWin = true;
            boolean colWin = true;
            for (int j = 0; j < size; j++) {
                if (board[i][j] != player) rowWin = false;
                if (board[j][i] != player) colWin = false;
            }
            if (rowWin || colWin) return true;
        }

        // Diagonal
        boolean diag1 = true;
        boolean diag2 = true;
        for (int i = 0; i < size; i++) {
            if (board[i][i] != player) diag1 = false;
            if (board[i][size - 1 - i] != player) diag2 = false;
        }
        return diag1 || diag2;
    }

    // Check if the board is completely filled
    private boolean isBoardFull(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) return false;
            }
        }
        return true;
    }

    // Send result message
    private void sendResult(String resultMessage) {
        Main.telegramBot.execute(
                getResultMessageText(
                        resultMessage,
                        formatBoard(user.getGameBoard())
                )
        );

        // Send main menu
        user.setMessageId(
                telegramBot.execute(
                        new SendMessage(user.getUserId(), getString(USER_STATISTICS_MSG)
                                .formatted(DB.getUserScores(user.getUserId()))
                        ).parseMode(ParseMode.valueOf("HTML")
                        ).replyMarkup(BotButtonService.genAfterGameCabinetButtons())
                ).message().messageId()
        );
        //user.setUserState(UserState.CABINET);

        updateMessageId(user.getUserId(), user.getMessageId());
        //updateUserState(user.getUserId(), user.getUserState().toString());

        // Initialize game board
        user.initializeBoard();
        DB.updateGameBoard(user.getUserId(), user.getGameBoard());
    }

    @NotNull
    private EditMessageText getResultMessageText(String resultMessage, String boardState) {
        return new EditMessageText(
                user.getUserId(), user.getMessageId(),
                RESULT_MSG.formatted(
                        getString(resultMessage),
                        DifficultyLevel.getTrueLevelMsg(user.getDifficultyLevel()),
                        getString(BOARD_MSG)
                ) + boardState
        ).parseMode(
                ParseMode.valueOf("HTML")
        );
    }

    //Get game board results
    private String formatBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        String padding = "  ";

        sb.append("<pre>");
        for (int[] row : board) {
            sb.append(padding);
            for (int cell : row) {
                String symbol;
                if (cell == 1) {
                    symbol = getUserSign(user.getUserId());
                } else if (cell == 2) {
                    symbol = getBotSign(user.getUserId());
                } else {
                    symbol = EMPTY_SIGN; // Bo'sh joy
                }
                sb.append(symbol);
                sb.append(padding);
            }
            sb.append("\n");
        }
        sb.append("</pre>");
        return sb.toString();
    }
}
