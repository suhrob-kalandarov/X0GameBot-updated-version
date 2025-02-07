package org.exp.botservice.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.exp.entity.TgUser;

import static org.exp.botservice.servicemessages.Constant.*;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

public interface BotButtonService {

    static InlineKeyboardMarkup genCabinetButtons(TgUser tgUser) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(getString(PLAY_BTN))
                                .callbackData(PLAY_BTN)
                )
                .addRow(
                        new InlineKeyboardButton(getString(LANGUAGE_MSG))
                                .callbackData(LANGUAGE_MSG)
                );
        if (tgUser.getChatId().equals(adminChatId)) {
            inlineKeyboardMarkup.addRow(new InlineKeyboardButton("ADMIN PANEL").callbackData("admin"));
        }
        return inlineKeyboardMarkup;
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

    static InlineKeyboardMarkup genAdminBtns() {
        return new InlineKeyboardMarkup()
                .addRow(new InlineKeyboardButton(SEND_MSG_TO_BOT_USERS_BTN).callbackData("admin_send_message"))
                .addRow(new InlineKeyboardButton(GET_USERS_LIST_BTN).callbackData("admin_get_users_list"))
                .addRow(new InlineKeyboardButton(DATA_STORAGE_BTN).callbackData("admin_data_storage"))
                .addRow(new InlineKeyboardButton(UPDATE_USERS_DATA_BTN).callbackData("admin_update_users_data"))
                .addRow(new InlineKeyboardButton(getString(BACK_BUTTON_MSG)).callbackData("back_to_cabinet"));
    }

    static InlineKeyboardMarkup genMessageStylesBtns() {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(SEND_MSG_WITH_PIC).callbackData(SEND_MSG_WITH_PIC),
                        new InlineKeyboardButton(SEND_MSG_WITH_VID).callbackData(SEND_MSG_WITH_VID)
                )
                .addRow(
                        new InlineKeyboardButton(SEND_MSG_WITH_TXT).callbackData(SEND_MSG_WITH_TXT),
                        new InlineKeyboardButton("null?").callbackData("null?")
                )
                /*.addRow(
                        new InlineKeyboardButton(getString(BACK_BUTTON_MSG)).callbackData("admin_back_to_cabinet")
                )*/;
    }
}

