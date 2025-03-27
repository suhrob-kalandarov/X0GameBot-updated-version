package org.botcontrol.botservice.btnservice;

import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.EditMessageText;
import org.botcontrol.Main;
import org.botcontrol.entities.MultiGame;

import static org.botcontrol.botservice.msgservice.Constant.*;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;


public interface BotButtonService {

    static InlineKeyboardMarkup genCabinetButtons() {
        return new InlineKeyboardMarkup()
                .addRow(new InlineKeyboardButton(getString(PLAY_WITH_BOT_BTN))
                                .callbackData(PLAY_WITH_BOT_BTN),
                        new InlineKeyboardButton(getString(PLAY_WITH_FRIEND_BTN))
                                //.callbackData(PLAY_WITH_FRIEND_BTN)
                                .switchInlineQuery(" ")
                )
                .addRow(new InlineKeyboardButton(getString(DIFFICULTY_LEVEL_BTN))
                        .callbackData(DIFFICULTY_LEVEL_BTN)
                )
                .addRow(new InlineKeyboardButton(getString(STATISTICS_BTN))
                                .callbackData(STATISTICS_BTN)
                )
                .addRow(new InlineKeyboardButton(getString(LANGUAGE_MSG))
                                .callbackData(LANGUAGE_MSG)
                );
    }

    static InlineKeyboardMarkup genAfterGameCabinetButtons() {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(getString(PLAY_WITH_BOT_BTN))
                                .callbackData(PLAY_WITH_BOT_BTN),
                        new InlineKeyboardButton(getString(PLAY_WITH_FRIEND_BTN))
                                //.callbackData(PLAY_WITH_FRIEND_BTN)
                                .switchInlineQuery(" ")

                )
                .addRow(new InlineKeyboardButton(getString(DIFFICULTY_LEVEL_BTN))
                                .callbackData(DIFFICULTY_LEVEL_BTN)
                )
                .addRow(new InlineKeyboardButton(getString(SUPPORT_BTN))
                                .callbackData(SUPPORT_BTN)
                )
                .addRow(new InlineKeyboardButton(getString(LANGUAGE_MSG))
                                .callbackData(LANGUAGE_MSG)
                );
    }

    static InlineKeyboardMarkup genGameBoard(int[][] board, String playerSymbol) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (int i = 0; i < board.length; i++) {
            InlineKeyboardButton[] row = new InlineKeyboardButton[board[i].length];
            for (int j = 0; j < board[i].length; j++) {
                String symbol = switch (board[i][j]) {
                    case 1 -> playerSymbol;
                    case 2 -> (playerSymbol.equals(X_SIGN) ? O_SIGN : X_SIGN);
                    default -> EMPTY_SIGN;
                };
                row[j] = new InlineKeyboardButton(symbol)
                        .callbackData("cell_" + i + "_" + j);
            }
            markup.addRow(row);
        }
        return markup;
    }

    static InlineKeyboardMarkup genDifficultyLevelButtons(){
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(getString(LEVEL_EASY)).callbackData("level_easy")
                )
                .addRow(
                        new InlineKeyboardButton(getString(LEVEL_AVERAGE)).callbackData("level_average"),
                        new InlineKeyboardButton(getString(LEVEL_DIFFICULT)).callbackData("level_difficult")
                )
                .addRow(
                        new InlineKeyboardButton(getString(LEVEL_EXTREME)).callbackData("level_extreme")
                )
                .addRow(
                        new InlineKeyboardButton(getString(BACK_BUTTON_MSG)).callbackData("back_to_cabinet")
                );
    }

    static InlineKeyboardMarkup genLanguageButtons() {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton("\uD83C\uDDFA\uD83C\uDDFFUzbek").callbackData("lang_uz"),
                        new InlineKeyboardButton("\uD83C\uDDF7\uD83C\uDDFAРусский").callbackData("lang_ru"),
                        new InlineKeyboardButton("\uD83C\uDDFA\uD83C\uDDF8English").callbackData("lang_en")
                )
                .addRow(new InlineKeyboardButton(getString(BACK_BUTTON_MSG))
                        .callbackData("back_to_cabinet")
                );
    }

    static InlineKeyboardMarkup chooseSymbolButtons() {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(X_SIGN).callbackData("CHOOSE_X"),
                        new InlineKeyboardButton(O_SIGN).callbackData("CHOOSE_O")
                )
                .addRow(
                        new InlineKeyboardButton(getString(BACK_BUTTON_MSG)).callbackData("back_to_cabinet")
                );
    }

    static InlineKeyboardMarkup getBoardBtns(int[][] board, int gameId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (int i = 0; i < board.length; i++) {
            InlineKeyboardButton[] row = new InlineKeyboardButton[board[i].length];
            for (int j = 0; j < board[i].length; j++) {
                String cellText = switch (board[i][j]) {
                    case 1 -> "❌";
                    case 2 -> "⭕";
                    default -> "⬜";
                };
                row[j] = new InlineKeyboardButton(cellText)
                        .callbackData("MOVE_" + gameId + "_" + i + "_" + j);
            }
            markup.addRow(row);
        }
        return markup;
    }


    static void sendGameBoard(MultiGame game, String inlineMessageId) {
        if (game.getTurn() == null) {
            //log("Navbat belgilanmagan: Game ID = " + game.getGameId());
            return;
        }

        //String status = "Navbat: " + (game.getTurn().equals(game.getPlayerXId()) ? "❌" : "⭕");

        Long xPlayer = game.getPlayerO().getUserId();
        Long oPlayer = game.getPlayerX().getUserId();

        String xPlayerFullName = game.getPlayerX().getFullName();
        String oPlayerFullName = game.getPlayerO().getFullName();

        Long turnId = game.getTurn();

        String status = "❌ " + xPlayerFullName + "%s\n⭕ " + oPlayerFullName + "%s";
        if (!turnId.equals(xPlayer)) {
            status = status.formatted("👈", "");
        } else {
            status = status.formatted("", "👈");
        }

        EditMessageText editMessage = new EditMessageText(
                inlineMessageId,
                status
        ).replyMarkup(getBoardBtns(game.getGameBoard(), game.getGameId()))
                .parseMode(ParseMode.Markdown);
        Main.telegramBot.execute(editMessage);

        //log("Game board yuborildi: Game ID = " + game.getGameId());
    }

    static String formatBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(switch (board[i][j]) {
                    case 1 -> "❌";
                    case 2 -> "⭕";
                    default -> "⬜";
                });
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    static InlineKeyboardMarkup endGameBtns() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("🔄").switchInlineQueryCurrentChat(" "),
                new InlineKeyboardButton("🤖").url("https://t.me/" + Main.BOT_USERNAME)
        );
    }
}
