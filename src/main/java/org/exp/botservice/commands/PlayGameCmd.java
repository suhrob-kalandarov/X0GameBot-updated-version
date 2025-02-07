package org.exp.botservice.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import org.exp.Main;
import org.exp.botservice.gamelogic.GameLogic;
import org.exp.botservice.service.BotButtonService;
import org.exp.botservice.servicemessages.Constant;
import org.exp.entity.State;
import org.exp.entity.TgUser;

import static org.exp.Main.telegramBot;
import static org.exp.botservice.servicemessages.Constant.*;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PlayGameCmd implements BotCommand {
    private final TgUser tgUser;
    private final Update update;
    private final String data;

    @Override
    public void process() {
        getAndSetChosenSymbol();
        tgUser.initializeBoard();

        // Agar foydalanuvchi ⭕ ni tanlagan bo'lsa, bot birinchi yurishi kerak
        if (tgUser.getPlayerSymbol().equals(O_SIGN)) {
            int[] botMove = findBestMove(tgUser.getGameBoard());
            tgUser.getGameBoard()[botMove[0]][botMove[1]] = 2; // Bot yurishi
        }

        // O'yin boshlanish xabarini yuborish
        EditMessageText editMessage = new EditMessageText(
                tgUser.getChatId(),
                tgUser.getMessageId(),
                formatGameStartMessage()
        );
        editMessage.replyMarkup(BotButtonService.genGameBoard(tgUser.getGameBoard(), tgUser.getPlayerSymbol()));
        SendResponse response = (SendResponse) telegramBot.execute(editMessage);
        tgUser.setMessageId(response.message().messageId());
        tgUser.setState(State.IN_GAME);
    }

    public void handleMove(int row, int col) {
        // Katakcha band bo'lsa
        if (tgUser.getGameBoard()[row][col] != 0) {
            return;
        }

        // Foydalanuvchi harakati
        tgUser.getGameBoard()[row][col] = 1;

        // Foydalanuvchi yutganligini tekshirish
        if (checkWin(tgUser.getGameBoard(), 1)) {
            tgUser.setUserScore(tgUser.getUserScore()+1);
            sendResult(YOU_WON_MSG);
            return;
        }

        // Doska to'liqligini tekshirish (foydalanuvchi yurishidan keyin)
        if (isBoardFull(tgUser.getGameBoard())) {
            tgUser.setDrawScore(tgUser.getDrawScore()+1);
            sendResult(DRAW_MSG);
            return;
        }

        // Bot harakati
            int[] botMove = new GameLogic().findBestMove(tgUser.getGameBoard());
            tgUser.getGameBoard()[botMove[0]][botMove[1]] = 2;

        // Bot yutganligini tekshirish
        if (checkWin(tgUser.getGameBoard(), 2)) {
            tgUser.setBotScore(tgUser.getBotScore()+1);
            sendResult(YOU_LOST_MSG);
            return;
        }

        // Doska to'liqligini tekshirish (bot yurishidan keyin)
        if (isBoardFull(tgUser.getGameBoard())) {
            tgUser.setDrawScore(tgUser.getDrawScore()+1);
            sendResult(DRAW_MSG);
            return;
        }
        updateGameBoard();
    }

    // Doskani yangilash
    private void updateGameBoard() {
        EditMessageText editMessage = new EditMessageText(
                tgUser.getChatId(),
                tgUser.getMessageId(),
                formatGameStartMessage()
        );
        editMessage.replyMarkup(BotButtonService.genGameBoard(tgUser.getGameBoard(), tgUser.getPlayerSymbol()));
        Main.telegramBot.execute(editMessage);
    }

    // Bot uchun eng yaxshi yurishni topish
    private int[] findBestMove(int[][] board) {
        return new GameLogic().findBestMove(board);
    }

    // Yutishni tekshirish
    private boolean checkWin(int[][] board, int player) {
        int size = board.length;

        // Gorizontal va vertikal
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

    // Doska to'liq to'ldirilganligini tekshirish
    private boolean isBoardFull(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) return false;
            }
        }
        return true;
    }

    // Natijani yuborish
    private void sendResult(String resultMessage) {
        String boardState = formatBoard(tgUser.getGameBoard());

        EditMessageText editMessage = getEditMessageText(resultMessage, boardState);

        editMessage.parseMode(ParseMode.valueOf("HTML"));
        Main.telegramBot.execute(editMessage);


        SendMessage sendMainMessage = new SendMessage(
                tgUser.getChatId(),
                getString(Constant.START_MSG)
        );
        sendMainMessage.replyMarkup(BotButtonService.genCabinetButtons(tgUser));
        SendResponse sendResponse = telegramBot.execute(sendMainMessage);
        tgUser.setMessageId(sendResponse.message().messageId());
        tgUser.setState(State.CABINET);

        // Doskani reset qilish
        tgUser.initializeBoard();
    }

    @NotNull
    private EditMessageText getEditMessageText(String resultMessage, String boardState) {
        String message = String.format(getString(USER_STATISTICS_MSG),
                tgUser.getUserScore(),
                tgUser.getDrawScore(),
                tgUser.getBotScore()
        );

        return new EditMessageText(
                tgUser.getChatId(),
                tgUser.getMessageId(),
                "<b> " + getString(RESULT_MSG) + " </b><b>"
                        + getString(resultMessage)
                        + "</b>\n\n<b>" + getString(BOARD_MSG) + "</b>"
                        + boardState
                        + "\n\n"
                        + message
        );
    }

    // Doskani formatlash
    private String formatBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        String padding = "  ";

        sb.append("<pre>");
        for (int[] row : board) {
            sb.append(padding);
            for (int cell : row) {
                String symbol;
                if (cell == 1) {
                    symbol = tgUser.getPlayerSymbol();
                } else if (cell == 2) {
                    symbol = tgUser.getBotSymbol();
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


    private String formatGameStartMessage() {
        return getString(GAME_MENU_MSG).formatted(
                tgUser.getPlayerSymbol(),
                tgUser.getBotSymbol()
        );
    }

    private void getAndSetChosenSymbol() {
        if (data.equals("CHOOSE_X")) {
            tgUser.setPlayerSymbol(X_SIGN);
            tgUser.setBotSymbol(O_SIGN);
        } else if (data.equals("CHOOSE_O")) {
            tgUser.setPlayerSymbol(O_SIGN);
            tgUser.setBotSymbol(X_SIGN);
        } else {
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Error: Undefined character!");
            sendMessage.replyMarkup(BotButtonService.genCabinetButtons(tgUser));
            SendResponse sendResponse = telegramBot.execute(sendMessage);
            tgUser.setMessageId(sendResponse.message().messageId());
        }
    }
}