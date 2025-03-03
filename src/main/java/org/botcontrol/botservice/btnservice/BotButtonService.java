package org.botcontrol.botservice.btnservice;

import com.pengrad.telegrambot.model.request.*;

import static org.botcontrol.botservice.msgservice.Constant.*;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;


public interface BotButtonService {

    static InlineKeyboardMarkup genCabinetButtons() {
        return new InlineKeyboardMarkup()
                .addRow(new InlineKeyboardButton(getString(PLAY_WITH_BOT_BTN))
                                .callbackData(PLAY_WITH_BOT_BTN),
                        new InlineKeyboardButton(getString(PLAY_WITH_FRIEND_BTN))
                                .callbackData(PLAY_WITH_FRIEND_BTN)
                                //.switchInlineQuery("")
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
                                .callbackData(PLAY_WITH_FRIEND_BTN)
                                //.switchInlineQuery("")

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
}
