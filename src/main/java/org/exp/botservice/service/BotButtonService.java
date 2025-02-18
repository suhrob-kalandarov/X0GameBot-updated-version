package org.exp.botservice.service;

import com.pengrad.telegrambot.model.request.*;
import org.exp.entity.adminentities.Admin;

import java.util.ArrayList;
import java.util.List;

import static org.exp.botservice.servicemessages.Constant.*;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

public interface BotButtonService {

    static InlineKeyboardMarkup genCabinetButtons() {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(getString(PLAY_BTN))
                                .callbackData(PLAY_BTN)
                )
                .addRow(
                        new InlineKeyboardButton(getString(STATISTICS_BTN))
                                .callbackData(STATISTICS_BTN)
                )
                .addRow(
                        new InlineKeyboardButton(getString(LANGUAGE_MSG))
                                .callbackData(LANGUAGE_MSG)
                );
    }

    static InlineKeyboardMarkup genAfterGameCabinetButtons() {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(getString(PLAY_BTN))
                                .callbackData(PLAY_BTN)
                )
                .addRow(
                        new InlineKeyboardButton(getString(SUPPORT_BTN))
                                .callbackData(SUPPORT_BTN)
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


    /// ADMIN BUTTONS
    static InlineKeyboardMarkup genAdminBtns() {
        return new InlineKeyboardMarkup()
                .addRow(new InlineKeyboardButton(SEND_MSG_BTN).callbackData("admin_want_to_send_message"))
                .addRow(new InlineKeyboardButton(GET_USERS_LIST_BTN).callbackData("admin_want_to_get_users_list"))
                .addRow(new InlineKeyboardButton("View Existing Photo Files").callbackData("admin_want_to_view_photo_files"))
                .addRow(new InlineKeyboardButton("Back↩️").callbackData("admin_want_to_back_to_game_cabinet"));
    }

    static InlineKeyboardMarkup genMessageStylesBtns() {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(SEND_MSG_WITH_PIC).callbackData("msg_with_pic"),
                        new InlineKeyboardButton(SEND_MSG_WITH_TXT).callbackData("msg_only_text")
                )
                .addRow(
                        new InlineKeyboardButton("Back↩️").callbackData("admin_want_to_send_message")
                );
    }

    static InlineKeyboardMarkup getMessageMainPanelOptions() {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton(SEND_MSG_TO_BOT_USER_BTN).callbackData("admin_send_message_to_an_user"),
                        new InlineKeyboardButton(SEND_MSG_TO_BOT_USERS_BTN).callbackData("admin_send_message_to_users")
                )
                .addRow(new InlineKeyboardButton("Back↩️").callbackData("admin_back_to_cabinet"));
    }

    static Keyboard genAdminMenuBtn() {
        return new ReplyKeyboardMarkup((new KeyboardButton(ADMIN))).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    static Keyboard genGameMenuBtn() {
        return new ReplyKeyboardMarkup((new KeyboardButton(GAME))).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    static InlineKeyboardMarkup genBackButton(String callBackData) {
        return new InlineKeyboardMarkup(new InlineKeyboardButton("Back↩️").callbackData(callBackData));
    }

    /*
    static Keyboard fetchUserList(Admin admin) {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Show user list").callbackData("admin_want_to_show_user_list"),
                new InlineKeyboardButton("Back↩️").callbackData("admin_back_to_msg_panel")
        );
    }
    */

    static InlineKeyboardMarkup SendingMsgToOneUserMainOptionBtns() {
        return new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton("Show user list").callbackData("admin_want_to_show_user_list")
                )
                .addRow(new InlineKeyboardButton("Back↩️").callbackData("admin_want_to_send_message") //admin_back_to_msg_panel
                );
    }


    static InlineKeyboardMarkup generateImgNameBtns(List<String> photoFiles) {
        if (photoFiles == null || photoFiles.isEmpty()) {
            return new InlineKeyboardMarkup(
                    new InlineKeyboardButton("❌ No files found").callbackData("no_files")
            );
        }

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (String fileName : photoFiles) {
            if (fileName.matches("^[a-f0-9\\-]+\\.\\w+$")) { // UUID shaklidagi fayllarni ajratish
                String buttonText = fileName.split("-")[0]; // Birinchi `-` gacha bo‘lgan qismini olish
                buttons.add(new InlineKeyboardButton("🖼 " + buttonText).callbackData("img_" + fileName));
            }
        }

        int size = buttons.size();
        List<InlineKeyboardButton[]> keyboardRows = new ArrayList<>();

        for (int i = 0; i < size - 1; i += 2) {
            keyboardRows.add(new InlineKeyboardButton[]{buttons.get(i), buttons.get(i + 1)});
        }

        if (size % 2 != 0) {
            keyboardRows.add(new InlineKeyboardButton[]{buttons.get(size - 1)});
        }

        return new InlineKeyboardMarkup(keyboardRows.toArray(new InlineKeyboardButton[0][]))
                .addRow(new InlineKeyboardButton("Back↩️").callbackData("admin_back_to_cabinet"));
    }
}

