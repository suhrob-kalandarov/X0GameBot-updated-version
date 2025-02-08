package org.exp.botservice.servicemessages;

public interface Constant {

    // Xabar kalitlari
    String EMPTY_SIGN = "⬜";
    String X_SIGN = "❌";
    String O_SIGN = "⭕";

    String START_MSG = "start_msg";
    String BACK_BUTTON_MSG = "back_btn";
    String PLAY_BTN = "play_btn";
    String LANGUAGE_MSG = "language_msg";

    String STATISTICS_MSG = "statistics_msg";

    String RESULT_MSG = "result_msg";
    String YOU_WON_MSG = "win_msg";
    String DRAW_MSG = "draw_msg";
    String YOU_LOST_MSG = "lose_msg";

    String BOARD_MSG = "board_msg";

    String USER_STATISTICS_MSG = "user_statistics_msg";

    String CHOOSE_LANG = "choose_language";
    String LANG_SUCCESS_MSG = "language_success_msg";
    String CHOOSE_SYMBOL_MSG = "choose_symbol_msg";
    String GAME_MENU_MSG = "in_game_menu_msg";
    String CHOOSE_AN_OPTION = "choose_an_option";
    String WARNING_MSG = "warning_msg";


    // Admin message keys
    String SEND_MSG_TO_BOT_USERS_BTN = "Send message to users";
    String SEND_MSG_TO_BOT_USER_BTN = "Send message to user";
    String GET_USERS_LIST_BTN = "Get users list";
    String DATA_STORAGE_BTN = "Save data";
    String UPDATE_USERS_DATA_BTN = "Updating user information in the database";

    // cmd keys
    String START = "/start";
    String ADMIN = "ADMIN";
    String CELL = "cell_";
    String BACK = "back";
    String LANG = "lang_";
    String DEF_LANG_EN = "en";
    String ADMIN_CALLBACK = "admin_";
    String CHOSEN_SYMBOL = "CHOOSE_";

    //msg cmd keys
    String SEND_MSG_WITH_TXT = "Send text msg";
    String SEND_MSG_WITH_PIC = "Send msg with a picture";
    String SEND_MSG_WITH_VID = "Send msg with a video";


}
