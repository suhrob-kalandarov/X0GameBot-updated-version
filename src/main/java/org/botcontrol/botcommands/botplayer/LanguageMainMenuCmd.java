package org.botcontrol.botcommands.botplayer;

import lombok.RequiredArgsConstructor;
import com.pengrad.telegrambot.response.SendResponse;
import com.pengrad.telegrambot.request.EditMessageText;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.botcontrol.Main;
import org.botcontrol.entities.User;
import org.botcontrol.entities.UserState;
import org.botcontrol.botcommands.BotCommand;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.botservice.btnservice.BotButtonService;

import static org.botcontrol.botservice.msgservice.Constant.CHOOSE_LANG;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class LanguageMainMenuCmd implements BotCommand {
    private final User user;

    private static final Logger logger = LogManager.getLogger(LanguageMainMenuCmd.class);

    @Override
    public void process() {
        logger.info("Til tanlash menyusi ko'rsatilmoqda (User: {})", user.getUserId());
        try {
            SendResponse sendResponse = (SendResponse) Main.telegramBot.execute(
                    new EditMessageText(
                            user.getUserId(), user.getMessageId(),
                            getString(CHOOSE_LANG)

                    ).replyMarkup(BotButtonService.genLanguageButtons())
            );

            user.setMessageId(sendResponse.message().messageId());
            user.setUserState(UserState.LANG_MENU);

            DB.updateMessageId(user.getUserId(), user.getMessageId());
            DB.updateUserState(user.getUserId(), user.getUserState().toString());

            logger.debug("Til tanlash menyusi yuborildi");
        } catch (Exception e) {
            logger.error("Til menyusini ko'rsatishda xatolik: {}", e.getMessage(), e);
        }
    }
}
