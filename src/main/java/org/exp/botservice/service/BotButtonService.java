package org.exp.botservice.service;

import com.pengrad.telegrambot.model.request.*;

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
        /*if (tgUser.getChatId().equals(adminChatId)) {
            inlineKeyboardMarkup.addRow(new InlineKeyboardButton("ADMIN PANEL").callbackData("admin"));
        }*/
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
                .addRow(new InlineKeyboardButton(SEND_MSG_TO_BOT_USERS_BTN).callbackData("admin_send_message_to_users"))
                .addRow(new InlineKeyboardButton(SEND_MSG_TO_BOT_USER_BTN).callbackData("admin_send_message_to_user"))
                .addRow(new InlineKeyboardButton(GET_USERS_LIST_BTN).callbackData("admin_get_users_list"))
                .addRow(new InlineKeyboardButton("View Existing Photo Files").callbackData("admin_view_photo_files"))
                .addRow(new InlineKeyboardButton(getString(BACK_BUTTON_MSG)).callbackData("admin_back_to_game_cabinet"));
    }

    static InlineKeyboardMarkup genMessageStylesBtns() {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(SEND_MSG_WITH_PIC).callbackData("msg_with_pic"),
                        new InlineKeyboardButton(SEND_MSG_WITH_TXT).callbackData("msg_with_text")
                )
                .addRow(
                        new InlineKeyboardButton(getString(BACK_BUTTON_MSG)).callbackData("admin_back_to_cabinet")
                );
    }

    static Keyboard genAdminBtn() {
        return new ReplyKeyboardMarkup((new KeyboardButton(ADMIN))).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    static InlineKeyboardMarkup genBackButton() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton(getString(BACK_BUTTON_MSG)).callbackData("admin_back_to_cabinet"));
    }
}

