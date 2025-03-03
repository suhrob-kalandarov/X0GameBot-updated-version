package org.botcontrol.botcommands.botplayer;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.botcontrol.botcommands.BotCommand;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.botservice.msgservice.Constant;
import org.botcontrol.entities.User;
import org.botcontrol.entities.UserState;

import static org.botcontrol.Main.telegramBot;
import static org.botcontrol.botservice.btnservice.BotButtonService.chooseSymbolButtons;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class SelectionSymbolCmd implements BotCommand {
    private final User user;

    private static final Logger logger = LogManager.getLogger(SelectionSymbolCmd.class);

    @Override
    public void process() {
        logger.info("Belgi tanlash menyusi ko'rsatilmoqda (User: {})", user.getUserId());
        try {
            SendResponse response = (SendResponse) telegramBot.execute(
                    new EditMessageText(
                            user.getUserId(),
                            user.getMessageId(),
                            getString(Constant.CHOOSE_SYMBOL_MSG)

                    ).replyMarkup(chooseSymbolButtons())
            );

            user.setUserState(UserState.SYMBOL_CHOOSING);
            user.setMessageId(response.message().messageId());

            DB.updateMessageId(user.getUserId(), user.getMessageId());
            DB.updateUserState(user.getUserId(), user.getUserState().toString());

            logger.debug("Belgi tanlash menyusi yuborildi");

        } catch (Exception e) {
            logger.error("Belgi tanlash menyusini yuborishda xatolik: {}", e.getMessage(), e);
        }
    }
}