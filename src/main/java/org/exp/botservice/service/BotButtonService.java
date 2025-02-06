package org.exp.botservice.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.exp.entity.TgUser;

import static org.exp.botservice.servicemessages.Constant.*;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

public interface BotButtonService {

    static InlineKeyboardMarkup genCabinetButtons(TgUser tgUser) {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(getString(PLAY_BTN))
                                .callbackData(PLAY_BTN)
                )
                .addRow(
                        new InlineKeyboardButton(getString(LANGUAGE_MSG))
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

    static InlineKeyboardMarkup genLanguageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(
                new InlineKeyboardButton("\uD83C\uDDFA\uD83C\uDDFFUzbek").callbackData("lang_uz"),
                new InlineKeyboardButton("\uD83C\uDDF7\uD83C\uDDFAРусский").callbackData("lang_ru"),
                new InlineKeyboardButton("\uD83C\uDDFA\uD83C\uDDF8English").callbackData("lang_en")
        );
        return inlineKeyboardMarkup.addRow(new InlineKeyboardButton(getString(BACK_BUTTON_MSG))
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
